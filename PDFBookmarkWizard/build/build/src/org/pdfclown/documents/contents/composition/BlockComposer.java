/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it):
      - porting and enhancement of [MG]'s line alignment .NET/C# implementation.
    * Manuel Guilbault (code contributor, manuel.guilbault@gmail.com):
      - line alignment.

  This file should be part of the source code distribution of "PDF Clown library"
  (the Program): see the accompanying README files for more info.

  This Program is free software; you can redistribute it and/or modify it under the terms
  of the GNU Lesser General Public License as published by the Free Software Foundation;
  either version 3 of the License, or (at your option) any later version.

  This Program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  either expressed or implied; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this
  Program (see README files); if not, go to the GNU website (http://www.gnu.org/licenses/).

  Redistribution and use, with or without modification, are permitted provided that such
  redistributions retain the above copyright notice, license and disclaimer, along with
  this list of conditions.
*/

package org.pdfclown.documents.contents.composition;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.composition.Length.UnitModeEnum;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.LocalGraphicsState;
import org.pdfclown.documents.contents.objects.ModifyCTM;
import org.pdfclown.documents.contents.objects.Operation;
import org.pdfclown.documents.contents.objects.SetWordSpace;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.OperationUtils;

/**
  Content block composer.
  <p>It provides content positioning functionalities for page typesetting.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @author Manuel Guilbault (manuel.guilbault@gmail.com)
  @since 0.0.3
  @version 0.1.2, 01/20/12
*/
/*
  TODO: Manage all the graphics parameters (especially
  those text-related, like horizontal scaling etc.) using ContentScanner -- see PDF:1.6:5.2-3!!!
*/
public final class BlockComposer
{
  // <class>
  // <classes>
  private static final class ContentPlaceholder
    extends Operation
  {
    public List<ContentObject> objects = new ArrayList<ContentObject>();

    public ContentPlaceholder(
      )
    {super(null);}

    @SuppressWarnings("unused")
    public List<ContentObject> getObjects(
      )
    {return objects;}

    @Override
    public void writeTo(
      IOutputStream stream,
      Document context
      )
    {
      for(ContentObject object : objects)
      {object.writeTo(stream, context);}
    }
  }

  private static final class Row
  {
    /**
      Row base line.
    */
    public double baseLine;
    /**
      Row's graphics objects container.
    */
    @SuppressWarnings("unused")
    public ContentPlaceholder container;
    public double height;
    /**
      Row's objects.
    */
    public ArrayList<RowObject> objects = new ArrayList<RowObject>();
    /**
      Number of space characters.
    */
    public int spaceCount = 0;
    public double width;
    /**
      Vertical location relative to the block frame.
    */
    public double y;

    Row(
      ContentPlaceholder container,
      double y
      )
    {
      this.container = container;
      this.y = y;
    }
  }

  private static final class RowObject
  {
    public enum TypeEnum
    {
      Text,
      XObject;
    }

    /**
      Base line.
    */
    public double baseLine;
    /**
      Graphics objects container associated to this object.
    */
    public ContainerObject container;
    public double height;
    /**
      Line alignment (can be either LineAlignmentEnum or Double).
    */
    public Object lineAlignment;
    public int spaceCount;
    @SuppressWarnings("unused")
    public TypeEnum type;
    public double width;

    RowObject(
      TypeEnum type,
      ContainerObject container,
      double height,
      double width,
      int spaceCount,
      Object lineAlignment,
      double baseLine
      )
    {
      this.type = type;
      this.container = container;
      this.height = height;
      this.width = width;
      this.spaceCount = spaceCount;
      this.lineAlignment = lineAlignment;
      this.baseLine = baseLine;
    }
  }
  // </classes>

  // <dynamic>
  /*
    NOTE: In order to provide fine-grained alignment,
    there are 2 postproduction state levels:
      1- row level (see endRow());
      2- block level (see end()).

    NOTE: Graphics instructions' layout follows this scheme (XS-BNF syntax):
      block = { beginLocalState translation parameters rows endLocalState }
      beginLocalState { "q\r" }
      translation = { "1 0 0 1 " number ' ' number "cm\r" }
      parameters = { ... } // Graphics state parameters.
      rows = { row* }
      row = { object* }
      object = { parameters beginLocalState translation content endLocalState }
      content = { ... } // Text, image (and so on) showing operators.
      endLocalState = { "Q\r" }
    NOTE: all the graphics state parameters within a block are block-level or row-object ones,
    i.e. they can't be represented inside row's local state, in order to allow parameter reuse
    within the same block.
  */
  // <fields>
  private final PrimitiveComposer baseComposer;
  private final ContentScanner scanner;

  private boolean hyphenation;
  private char hyphenationCharacter = '-';
  private LineAlignmentEnum lineAlignment = LineAlignmentEnum.BaseLine;
  private Length lineSpace = new Length(0, UnitModeEnum.Relative);
  private XAlignmentEnum xAlignment;
  private YAlignmentEnum yAlignment;

  /** Area available for the block contents. */
  private Rectangle2D frame;
  /** Actual area occupied by the block contents. */
  private Rectangle2D.Double boundBox;

  private Row currentRow;
  private boolean rowEnded;

  private LocalGraphicsState container;

  private double lastFontSize;
  // </fields>

  // <constructors>
  public BlockComposer(
    PrimitiveComposer baseComposer
    )
  {
    this.baseComposer = baseComposer;
    this.scanner = baseComposer.getScanner();
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Begins a content block.

    @param frame Block boundaries.
    @param xAlignment Horizontal alignment.
    @param yAlignment Vertical alignment.
  */
  public void begin(
    Rectangle2D frame,
    XAlignmentEnum xAlignment,
    YAlignmentEnum yAlignment
    )
  {
    this.frame = frame;
    this.xAlignment = xAlignment;
    this.yAlignment = yAlignment;
    lastFontSize = 0;

    // Open the block local state!
    /*
      NOTE: This device allows a fine-grained control over the block representation.
      It MUST be coupled with a closing statement on block end.
    */
    container = baseComposer.beginLocalState();

    boundBox = new Rectangle2D.Double(
      frame.getX(),
      frame.getY(),
      frame.getWidth(),
      0
      );

    beginRow();
  }

  /**
    Ends the content block.
  */
  public void end(
    )
  {
    // End last row!
    endRow(true);

    // Block translation.
    container.getObjects().add(
      0,
      new ModifyCTM(
        1, 0, 0, 1,
        boundBox.x, // Horizontal translation.
        -boundBox.y // Vertical translation.
        )
      );

    // Close the block local state!
    baseComposer.end();
  }

  /**
    Gets the base composer.
  */
  public PrimitiveComposer getBaseComposer(
    )
  {return baseComposer;}

  /**
    Gets the area occupied by the already-placed block contents.
  */
  public Rectangle2D getBoundBox(
    )
  {return boundBox;}

  /**
    Gets the area where to place the block contents.
  */
  public Rectangle2D getFrame(
    )
  {return frame;}

  /**
    Gets the character shown at the end of the line before a hyphenation break.
    Initial value: hyphen symbol (U+002D, i.e. '-').
  */
  public char getHyphenationCharacter(
    )
  {return hyphenationCharacter;}

  /**
    Gets the default line alignment.
    Initial value: {@link LineAlignmentEnum#BaseLine}.
  */
  public LineAlignmentEnum getLineAlignment(
    )
  {return lineAlignment;}

  /**
    Gets the text interline spacing.
    Initial value: 0.
  */
  public Length getLineSpace(
    )
  {return lineSpace;}

  /**
    Gets the content scanner.
  */
  public ContentScanner getScanner(
    )
  {return scanner;}

  /**
    Gets the horizontal alignment applied to the current content block.
  */
  public XAlignmentEnum getXAlignment(
    )
  {return xAlignment;}

  /**
    Gets the vertical alignment applied to the current content block.
  */
  public YAlignmentEnum getYAlignment(
    )
  {return yAlignment;}

  /**
    Gets whether the hyphenation algorithm has to be applied.
    Initial value: <code>false</code>.
  */
  public boolean isHyphenation(
    )
  {return hyphenation;}

  /**
    @see #isHyphenation()
  */
  public void setHyphenation(
    boolean value
    )
  {hyphenation = value;}

  public void setHyphenationCharacter(
    char value
    )
  {hyphenationCharacter = value;}

  /**
    @see #getLineAlignment()
  */
  public void setLineAlignment(
    LineAlignmentEnum value
    )
  {lineAlignment = value;}

  /**
    @see #getLineSpace()
  */
  public void setLineSpace(
    Length value
    )
  {lineSpace = value;}

  /**
    Ends current paragraph.
  */
  public void showBreak(
    )
  {
    endRow(true);
    beginRow();
  }

  /**
    Ends current paragraph, specifying the offset of the next one.
    <p>This functionality allows higher-level features such as paragraph indentation and margin.</p>

    @param offset Relative location of the next paragraph.
  */
  public void showBreak(
    Dimension2D offset
    )
  {
    showBreak();

    currentRow.y += offset.getHeight();
    currentRow.width = offset.getWidth();
  }

  /**
    Ends current paragraph, specifying the alignment of the next one.
    <p>This functionality allows higher-level features such as paragraph indentation and margin.</p>

    @param xAlignment Horizontal alignment.
  */
  public void showBreak(
    XAlignmentEnum xAlignment
    )
  {
    showBreak();

    this.xAlignment = xAlignment;
  }

  /**
    Ends current paragraph, specifying the offset and alignment of the next one.
    <p>This functionality allows higher-level features such as paragraph indentation and margin.</p>

    @param offset Relative location of the next paragraph.
    @param xAlignment Horizontal alignment.
  */
  public void showBreak(
    Dimension2D offset,
    XAlignmentEnum xAlignment
    )
  {
    showBreak(offset);

    this.xAlignment = xAlignment;
  }

  /**
    Shows text.
    <p>Default line alignment is applied.</p>

    @param text Text to show.
    @return Last shown character index.
  */
  public int showText(
    String text
    )
  {return showText(text, lineAlignment);}

  /**
    Shows text.

    @param text Text to show.
    @param lineAlignment Line alignment. It can be:
      <li>{@link LineAlignmentEnum}</li>
      <li>{@link Length}: arbitrary super-/sub-script, depending on whether the value is positive or
      not.</li>
    @return Last shown character index.
  */
  public int showText(
    String text,
    Object lineAlignment
    )
  {
    if(currentRow == null
      || text == null)
      return 0;

    ContentScanner.GraphicsState state = baseComposer.getState();
    Font font = state.getFont();
    double fontSize = state.getFontSize();
    double lineHeight = font.getLineHeight(fontSize);
    double baseLine = font.getAscent(fontSize);
    lineAlignment = resolveLineAlignment(lineAlignment);

    TextFitter textFitter = new TextFitter(
      text,
      0,
      font,
      fontSize,
      hyphenation,
      hyphenationCharacter
      );
    int textLength = text.length();
    int index = 0;

textShowing:
    while(true)
    {
      if(currentRow.width == 0) // Current row has just begun.
      {
        // Removing leading spaces...
        while(true)
        {
          if(index == textLength) // Text end reached.
            break textShowing;
          else if(text.charAt(index) != ' ') // No more leading spaces.
            break;

          index++;
        }
      }

      if(OperationUtils.compare(currentRow.y + lineHeight, frame.getHeight()) == 1) // Text's height exceeds block's remaining vertical space.
      {
        // Terminate current row and exit!
        endRow(false);
        break textShowing;
      }

      // Does the text fit?
      if(textFitter.fit(
        index,
        frame.getWidth() - currentRow.width, // Remaining row width.
        currentRow.spaceCount == 0
        ))
      {
        // Get the fitting text!
        String textChunk = textFitter.getFittedText();
        double textChunkWidth = textFitter.getFittedWidth();
        Point2D textChunkLocation = new Point2D.Double(
          currentRow.width,
          currentRow.y
          );

        // Insert the fitting text!
        RowObject object;
        {
          object = new RowObject(
            RowObject.TypeEnum.Text,
            baseComposer.beginLocalState(), // Opens the row object's local state.
            lineHeight,
            textChunkWidth,
            countOccurrence(' ',textChunk),
            lineAlignment,
            baseLine
            );
          baseComposer.showText(textChunk, textChunkLocation);
          baseComposer.end(); // Closes the row object's local state.
        }
        addRowObject(object, lineAlignment);

        index = textFitter.getEndIndex();
      }

      // Evaluating trailing text...
trailParsing:
      while(true)
      {
        if(index == textLength) // Text end reached.
          break textShowing;

        switch(text.charAt(index))
        {
          case '\r':
            break;
          case '\n':
            // New paragraph!
            index++;
            showBreak();
            break trailParsing;
          default:
            // New row (within the same paragraph)!
            endRow(false);
            beginRow();
            break trailParsing;
        }

        index++;
      }
    }
    if(index >= 0
      && lineAlignment == LineAlignmentEnum.BaseLine)
    {lastFontSize = fontSize;}

    return index;
  }

  /**
    Shows the specified external object.
    <p>Default line alignment is applied.</p>

    @param xObject External object.
    @param size Size of the external object.
    @return Whether the external object was successfully shown.
  */
  public boolean showXObject(
    XObject xObject,
    Dimension2D size
    )
  {return showXObject(xObject, size, lineAlignment);}

  /**
    Shows the specified external object.

    @param xObject External object.
    @param size Size of the external object.
    @param lineAlignment Line alignment. It can be:
      <li>{@link LineAlignmentEnum}</li>
      <li>{@link Length}: arbitrary super-/sub-script, depending on whether the value is positive or
      not.</li>
    @return Whether the external object was successfully shown.
  */
  public boolean showXObject(
    XObject xObject,
    Dimension2D size,
    Object lineAlignment
    )
  {
    if(currentRow == null
      || xObject == null)
      return false;

    if(size == null)
    {size = xObject.getSize();}
    lineAlignment = resolveLineAlignment(lineAlignment);

    while(true)
    {
      if(OperationUtils.compare(currentRow.y + size.getHeight(), frame.getHeight()) == 1) // Object's height exceeds block's remaining vertical space.
      {
        // Terminate current row and exit!
        endRow(false);
        return false;
      }
      else if(OperationUtils.compare(currentRow.width + size.getWidth(), frame.getWidth()) < 1) // There's room for the object in the current row.
      {
        Point2D location = new Point2D.Double(
          currentRow.width,
          currentRow.y
          );
        RowObject object;
        {
          object = new RowObject(
            RowObject.TypeEnum.XObject,
            baseComposer.beginLocalState(), // Opens the row object's local state.
            size.getHeight(),
            size.getWidth(),
            0,
            lineAlignment,
            size.getHeight()
            );
          baseComposer.showXObject(xObject, location, size);
          baseComposer.end(); // Closes the row object's local state.
        }
        addRowObject(object, lineAlignment);

        return true;
      }
      else // There's NOT enough room for the object in the current row.
      {
        // Go to next row!
        endRow(false);
        beginRow();
      }
    }
  }
  // </public>

  // <private>
  /**
    Adds an object to the current row.

    @param object Object to add.
    @param lineAlignment Object's line alignment.
  */
  private void addRowObject(
    RowObject object,
    Object lineAlignment
    )
  {
    currentRow.objects.add(object);
    currentRow.spaceCount += object.spaceCount;
    currentRow.width += object.width;

    if(lineAlignment instanceof Double || lineAlignment == LineAlignmentEnum.BaseLine)
    {
      double gap = (lineAlignment instanceof Double ? (Double)lineAlignment : 0);
      double superGap = object.baseLine + gap - currentRow.baseLine;
      if(superGap > 0)
      {
        currentRow.height += superGap;
        currentRow.baseLine += superGap;
      }
      double subGap = currentRow.baseLine + (object.height - object.baseLine) - gap - currentRow.height;
      if(subGap > 0)
      {currentRow.height += subGap;}
    }
    else if(object.height > currentRow.height)
    {currentRow.height = object.height;}
  }

  /**
    Begins a content row.
  */
  private void beginRow(
    )
  {
    rowEnded = false;

    double rowY = boundBox.height;
    if(rowY > 0)
    {
      ContentScanner.GraphicsState state = baseComposer.getState();
      rowY += lineSpace.getValue(state.getFont().getLineHeight(state.getFontSize()));
    }
    currentRow = new Row(
      (ContentPlaceholder)baseComposer.add(new ContentPlaceholder()),
      rowY
      );
  }

  private int countOccurrence(
    char value,
    String text
    )
  {
    int count = 0;
    int fromIndex = 0;
    do
    {
      int foundIndex = text.indexOf(value,fromIndex);
      if(foundIndex == -1)
        return count;

      count++;

      fromIndex = foundIndex + 1;
    }
    while(true);
  }

  /**
    Ends the content row.

    @param broken Indicates whether this is the end of a paragraph.
  */
  private void endRow(
    boolean broken
    )
  {
    if(rowEnded)
      return;

    rowEnded = true;

    double[] objectXOffsets = new double[currentRow.objects.size()]; // Horizontal object displacements.
    double wordSpace = 0; // Exceeding space among words.
    double rowXOffset = 0; // Horizontal row offset.

    List<RowObject> objects = currentRow.objects;

    // Horizontal alignment.
    XAlignmentEnum xAlignment = this.xAlignment;
    switch(xAlignment)
    {
      case Left:
        break;
      case Right:
        rowXOffset = frame.getWidth() - currentRow.width;
        break;
      case Center:
        rowXOffset = (frame.getWidth() - currentRow.width) / 2;
        break;
      case Justify:
        // Are there NO spaces?
        if(currentRow.spaceCount == 0
          || broken) // NO spaces.
        {
          /* NOTE: This situation equals a simple left alignment. */
          xAlignment = XAlignmentEnum.Left;
        }
        else // Spaces exist.
        {
          // Calculate the exceeding spacing among the words!
          wordSpace = (frame.getWidth() - currentRow.width) / currentRow.spaceCount;
          // Define the horizontal offsets for justified alignment.
          for(
            int index = 1,
              count = objects.size();
            index < count;
            index++
            )
          {
            /*
              NOTE: The offset represents the horizontal justification gap inserted
              at the left side of each object.
            */
            objectXOffsets[index] = objectXOffsets[index - 1] + objects.get(index - 1).spaceCount * wordSpace;
          }
        }
        break;
    }

    SetWordSpace wordSpaceOperation = new SetWordSpace(wordSpace);

    // Vertical alignment and translation.
    for(
      int index = objects.size() - 1;
      index >= 0;
      index--
      )
    {
      RowObject object = objects.get(index);

      // Vertical alignment.
      double objectYOffset = 0;
      {
        LineAlignmentEnum lineAlignment;
        double lineRise;
        {
          Object objectLineAlignment = object.lineAlignment;
          if(objectLineAlignment instanceof Double)
          {
            lineAlignment = LineAlignmentEnum.BaseLine;
            lineRise = (Double)objectLineAlignment;
          }
          else
          {
            lineAlignment = (LineAlignmentEnum)objectLineAlignment;
            lineRise = 0;
          }
        }
        switch (lineAlignment)
        {
            case Top:
                /* NOOP */
                break;
            case Middle:
                objectYOffset = -(currentRow.height - object.height) / 2;
                break;
            case BaseLine:
                objectYOffset = -(currentRow.baseLine - object.baseLine - lineRise);
                break;
            case Bottom:
                objectYOffset = -(currentRow.height - object.height);
                break;
            default:
                throw new NotImplementedException("Line alignment " + lineAlignment + " unknown.");
        }
      }

      List<ContentObject> containedGraphics = object.container.getObjects();
      // Word spacing.
      containedGraphics.add(0,wordSpaceOperation);
      // Translation.
      containedGraphics.add(
        0,
        new ModifyCTM(
          1, 0, 0, 1,
          objectXOffsets[index] + rowXOffset, // Horizontal alignment.
          objectYOffset // Vertical alignment.
          )
        );
    }

    // Update the actual block height!
    boundBox.height = currentRow.y + currentRow.height;

    // Update the actual block vertical location!
    double yOffset;
    switch(yAlignment)
    {
      case Bottom:
        yOffset = frame.getHeight() - boundBox.height;
        break;
      case Middle:
        yOffset = (frame.getHeight() - boundBox.height) / 2;
        break;
      case Top:
      default:
        yOffset = 0;
        break;
    }
    boundBox.y = frame.getY() + yOffset;

    // Discard the current row!
    currentRow = null;
  }

  private Object resolveLineAlignment(
    Object lineAlignment
    )
  {
    if(!(lineAlignment instanceof LineAlignmentEnum
      || lineAlignment instanceof Length))
      throw new IllegalArgumentException("'lineAlignment' param MUST be either LineAlignmentEnum or Length.");

    if(lineAlignment == LineAlignmentEnum.Super)
    {lineAlignment = new Length(0.33, UnitModeEnum.Relative);}
    else if(lineAlignment == LineAlignmentEnum.Sub)
    {lineAlignment = new Length(-0.33, UnitModeEnum.Relative);}
    if(lineAlignment instanceof Length)
    {
      if(lastFontSize == 0)
      {lastFontSize = baseComposer.getState().getFontSize();}
      lineAlignment = ((Length)lineAlignment).getValue(lastFontSize);
    }

    return lineAlignment;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}