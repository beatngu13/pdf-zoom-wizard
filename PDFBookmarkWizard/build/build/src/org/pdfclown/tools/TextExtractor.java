/*
  Copyright 2009-2011 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)

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

package org.pdfclown.tools;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.ITextString;
import org.pdfclown.documents.contents.TextChar;
import org.pdfclown.documents.contents.TextStyle;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.documents.contents.objects.XObject;
import org.pdfclown.util.math.Interval;

/**
  Tool for extracting text from {@link IContentContext content contexts}.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/01/11
*/
public final class TextExtractor
{
  // <class>
  // <classes>
  /**
    Text-to-area matching mode.
  */
  public enum AreaModeEnum
  {
    /**
      Text string must be contained by the area.
    */
    Containment,
    /**
      Text string must intersect the area.
    */
    Intersection
  }

  /**
    Text filter by interval.
    <p>Iterated intervals MUST be ordered.</p>
  */
  public interface IIntervalFilter
    extends Iterator<Interval<Integer>>
  {
    /**
      Notifies current matching.

      @param interval Current interval.
      @param match Text string matching the current interval.
    */
    void process(
      Interval<Integer> interval,
      ITextString match
      );
  }

  /**
    Text string.
    <p>This is typically used to assemble contiguous raw text strings.</p>
  */
  private static class TextString
    implements ITextString
  {
    // <class>
    // <dynamic>
    // <fields>
    private final List<TextChar> textChars = new ArrayList<TextChar>();
    // </fields>

    // <interface>
    // <public>
    @Override
    public Rectangle2D getBox(
      )
    {
      Rectangle2D box = null;
      for(TextChar textChar : textChars)
      {
        if(box == null)
        {box = (Rectangle2D)textChar.getBox().clone();}
        else
        {box.add(textChar.getBox());}
      }
      return box;
    }

    @Override
    public String getText(
      )
    {
      StringBuilder textBuilder = new StringBuilder();
      for(TextChar textChar : textChars)
      {textBuilder.append(textChar);}
      return textBuilder.toString();
    }

    @Override
    public List<TextChar> getTextChars(
      )
    {return textChars;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }

  /**
    Text string position comparator.
   */
  private static class TextStringPositionComparator
    implements Comparator<ITextString>
  {
    // <class>
    // <static>
    /**
      Gets whether the specified boxes lay on the same text line.
    */
    public static boolean isOnTheSameLine(
      Rectangle2D box1,
      Rectangle2D box2
      )
    {
      /*
        NOTE: In order to consider the two boxes being on the same line,
        we apply a simple rule of thumb: at least 25% of a box's height MUST
        lay on the horizontal projection of the other one.
      */
      double minHeight = Math.min(box1.getHeight(), box2.getHeight());
      double yThreshold = minHeight * .75;
      return ((box1.getY() > box2.getY() - yThreshold
          && box1.getY() < box2.getMaxY() + yThreshold - minHeight)
        || (box2.getY() > box1.getY() - yThreshold
          && box2.getY() < box1.getMaxY() + yThreshold - minHeight));
    }
    // </static>

    // <dynamic>
    // <Comparator>
    @Override
    public int compare(
      ITextString textString1,
      ITextString textString2
      )
    {
      Rectangle2D box1 = textString1.getBox();
      Rectangle2D box2 = textString2.getBox();
      if(isOnTheSameLine(box1,box2))
      {
        if(box1.getX() < box2.getX())
          return -1;
        else if(box1.getX() > box2.getX())
          return 1;
        else
          return 0;
      }
      else if(box1.getY() < box2.getY())
        return -1;
      else
        return 1;
    }
    // </Comparator>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  /**
    Converts text information into plain text.

    @param textStrings Text information to convert.
    @return Plain text.
  */
  public static String toString(
    Map<Rectangle2D,List<ITextString>> textStrings
    )
  {return toString(textStrings, "", "");}

  /**
    Converts text information into plain text.

    @param textStrings Text information to convert.
    @param lineSeparator Separator to apply on line break.
    @param areaSeparator Separator to apply on area break.
    @return Plain text.
  */
  public static String toString(
    Map<Rectangle2D,List<ITextString>> textStrings,
    String lineSeparator,
    String areaSeparator
    )
  {
    StringBuilder textBuilder = new StringBuilder();
    for(List<ITextString> areaTextStrings : textStrings.values())
    {
      if(textBuilder.length() > 0)
      {textBuilder.append(areaSeparator);}

      for(ITextString textString : areaTextStrings)
      {textBuilder.append(textString.getText()).append(lineSeparator);}
    }
    return textBuilder.toString();
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private AreaModeEnum areaMode = AreaModeEnum.Containment;
  private List<Rectangle2D> areas;
  private double areaTolerance = 0;
  private boolean dehyphenated;
  private boolean sorted;
  // </fields>

  // <constructors>
  public TextExtractor(
    )
  {this(true, false);}

  public TextExtractor(
    boolean sorted,
    boolean dehyphenated
    )
  {this(null, sorted, dehyphenated);}

  public TextExtractor(
    List<Rectangle2D> areas,
    boolean sorted,
    boolean dehyphenated
    )
  {
    setAreas(areas);
    setDehyphenated(dehyphenated);
    setSorted(sorted);
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Extracts text strings from the specified content context.

    @param contentContext Source content context.
  */
  public Map<Rectangle2D,List<ITextString>> extract(
    IContentContext contentContext
    )
  {
    Map<Rectangle2D,List<ITextString>> extractedTextStrings;
    {
      List<ITextString> textStrings = new ArrayList<ITextString>();
      {
        // 1. Extract the source text strings!
        List<ContentScanner.TextStringWrapper> rawTextStrings = new ArrayList<ContentScanner.TextStringWrapper>();
        extract(
          new ContentScanner(contentContext),
          rawTextStrings
          );

        // 2. Sort the target text strings!
        if(sorted)
        {sort(rawTextStrings,textStrings);}
        else
        {textStrings.addAll(rawTextStrings);}
      }

      // 3. Filter the target text strings!
      if(areas.isEmpty())
      {
        extractedTextStrings = new HashMap<Rectangle2D, List<ITextString>>();
        extractedTextStrings.put(null, textStrings);
      }
      else
      {extractedTextStrings = filter(textStrings,areas.toArray(new Rectangle2D[areas.size()]));}
    }
    return extractedTextStrings;
  }

  /**
    Extracts text strings from the specified contents.

    @param contents Source contents.
  */
  public Map<Rectangle2D,List<ITextString>> extract(
    Contents contents
    )
  {return extract(contents.getContentContext());}

  /**
    Gets the text strings matching the specified intervals.

    @param textStrings Text strings to filter.
    @param intervals Text intervals to match. They MUST be ordered and not overlapping.
    @return A list of text strings corresponding to the specified intervals.
  */
  public List<ITextString> filter(
    Map<Rectangle2D,List<ITextString>> textStrings,
    final List<Interval<Integer>> intervals
    )
  {
    final List<ITextString> filteredTextStrings = new ArrayList<ITextString>();
    {
      filter(
        textStrings,
        new IIntervalFilter(
          )
        {
          int index = 0;

          @Override
          public boolean hasNext(
            )
          {return index < intervals.size();}

          @Override
          public Interval<Integer> next(
            )
          {return intervals.get(index++);}

          @Override
          public void process(
            Interval<Integer> interval,
            ITextString match
            )
          {filteredTextStrings.add(match);}

          @Override
          public void remove(
            )
          {throw new UnsupportedOperationException();}
        }
        );
    }
    return filteredTextStrings;
  }

  /**
    Processes the text strings matching the specified filter.

    @param textStrings Text strings to filter.
    @param filter Matching processor.
  */
  public void filter(
    Map<Rectangle2D,List<ITextString>> textStrings,
    IIntervalFilter filter
    )
  {
    Iterator<List<ITextString>> textStringsIterator = textStrings.values().iterator();
    if(!textStringsIterator.hasNext())
      return;

    Iterator<ITextString> areaTextStringsIterator = textStringsIterator.next().iterator();
    if(!areaTextStringsIterator.hasNext())
      return;

    List<TextChar> textChars = areaTextStringsIterator.next().getTextChars();
    int baseTextCharIndex = 0;
    int textCharIndex = 0;
    while(filter.hasNext())
    {
      Interval<Integer> interval = filter.next();
      TextString match = new TextString();
      {
        int matchStartIndex = interval.getLow();
        int matchEndIndex = interval.getHigh();
        while(matchStartIndex > baseTextCharIndex + textChars.size())
        {
          baseTextCharIndex += textChars.size();
          if(!areaTextStringsIterator.hasNext())
          {areaTextStringsIterator = textStringsIterator.next().iterator();}
          textChars = areaTextStringsIterator.next().getTextChars();
        }
        textCharIndex = matchStartIndex - baseTextCharIndex;

        while(baseTextCharIndex + textCharIndex < matchEndIndex)
        {
          if(textCharIndex == textChars.size())
          {
            baseTextCharIndex += textChars.size();
            if(!areaTextStringsIterator.hasNext())
            {areaTextStringsIterator = textStringsIterator.next().iterator();}
            textChars = areaTextStringsIterator.next().getTextChars();
            textCharIndex = 0;
          }
          match.textChars.add(textChars.get(textCharIndex++));
        }
      }
      filter.process(interval, match);
    }
  }

  /**
    Gets the text strings matching the specified area.

    @param textStrings Text strings to filter, grouped by source area.
    @param area Graphic area which text strings have to be matched to.
  */
  public List<ITextString> filter(
    Map<Rectangle2D,List<ITextString>> textStrings,
    Rectangle2D area
    )
  {return filter(textStrings,new Rectangle2D[]{area}).get(area);}

  /**
    Gets the text strings matching the specified areas.

    @param textStrings Text strings to filter, grouped by source area.
    @param areas Graphic areas which text strings have to be matched to.
  */
  public Map<Rectangle2D,List<ITextString>> filter(
    Map<Rectangle2D,List<ITextString>> textStrings,
    Rectangle2D... areas
    )
  {
    Map<Rectangle2D,List<ITextString>> filteredTextStrings = null;
    for(List<ITextString> areaTextStrings : textStrings.values())
    {
      Map<Rectangle2D,List<ITextString>> filteredAreasTextStrings = filter(areaTextStrings,areas);
      if(filteredTextStrings == null)
      {filteredTextStrings = filteredAreasTextStrings;}
      else
      {
        for(Map.Entry<Rectangle2D,List<ITextString>> filteredAreaTextStringsEntry : filteredAreasTextStrings.entrySet())
        {filteredTextStrings.get(filteredAreaTextStringsEntry.getKey()).addAll(filteredAreaTextStringsEntry.getValue());}
      }
    }
    return filteredTextStrings;
  }

  /**
    Gets the text strings matching the specified area.

    @param textStrings Text strings to filter.
    @param area Graphic area which text strings have to be matched to.
  */
  public List<ITextString> filter(
    List<? extends ITextString> textStrings,
    Rectangle2D area
    )
  {return filter(textStrings,new Rectangle2D[]{area}).get(area);}

  /**
    Gets the text strings matching the specified areas.

    @param textStrings Text strings to filter.
    @param areas Graphic areas which text strings have to be matched to.
  */
  public Map<Rectangle2D,List<ITextString>> filter(
    List<? extends ITextString> textStrings,
    Rectangle2D... areas
    )
  {
    Map<Rectangle2D,List<ITextString>> filteredAreasTextStrings = new HashMap<Rectangle2D,List<ITextString>>();
    for(Rectangle2D area : areas)
    {
      List<ITextString> filteredAreaTextStrings = new ArrayList<ITextString>();
      filteredAreasTextStrings.put(area, filteredAreaTextStrings);
      Rectangle2D toleratedArea = (areaTolerance != 0
        ? new Rectangle2D.Double(
          area.getX() - areaTolerance,
          area.getY() - areaTolerance,
          area.getWidth() + areaTolerance * 2,
          area.getHeight() + areaTolerance * 2
          )
        : area);
      for(ITextString textString : textStrings)
      {
        Rectangle2D textStringBox = textString.getBox();
        if(toleratedArea.intersects(textStringBox))
        {
          TextString filteredTextString = new TextString();
          List<TextChar> filteredTextStringChars = filteredTextString.getTextChars();
          for(TextChar textChar : textString.getTextChars())
          {
            Rectangle2D textCharBox = textChar.getBox();
            if((areaMode == AreaModeEnum.Containment && toleratedArea.contains(textCharBox))
              || (areaMode == AreaModeEnum.Intersection && toleratedArea.intersects(textCharBox)))
            {filteredTextStringChars.add(textChar);}
          }
          filteredAreaTextStrings.add(filteredTextString);
        }
      }
    }
    return filteredAreasTextStrings;
  }

  /**
    Gets the text-to-area matching mode.
   */
  public AreaModeEnum getAreaMode(
    )
  {return areaMode;}

  /**
    Gets the graphic areas whose text has to be extracted.
  */
  public List<Rectangle2D> getAreas(
    )
  {return areas;}

  /**
    Gets the admitted outer area (in points) for containment matching purposes.
    <p>This measure is useful to ensure that text whose boxes overlap with the area bounds
    is not excluded from the match.</p>
   */
  public double getAreaTolerance(
    )
  {return areaTolerance;}

  /**
    Gets whether the text strings have to be dehyphenated.
  */
  public boolean isDehyphenated(
    )
  {return dehyphenated;}

  /**
    Gets whether the text strings have to be sorted.
  */
  public boolean isSorted(
    )
  {return sorted;}

  /**
    @see #getAreaMode()
  */
  public void setAreaMode(
    AreaModeEnum value
    )
  {areaMode = value;}

  /**
    @see #getAreas()
  */
  public void setAreas(
    List<Rectangle2D> value
    )
  {areas = (value == null ? new ArrayList<Rectangle2D>() : value);}

  /**
    @see #getAreaTolerance()
  */
  public void setAreaTolerance(
    double value
    )
  {areaTolerance = value;}

  /**
    @see #isDehyphenated()
  */
  public void setDehyphenated(
    boolean value
    )
  {
    dehyphenated = value;
    if(dehyphenated)
    {setSorted(true);}
  }

  /**
    @see #isSorted()
  */
  public void setSorted(
    boolean value
    )
  {
    sorted = value;
    if(!sorted)
    {setDehyphenated(false);}
  }
  // </public>

  // <private>
  /**
    Scans a content level looking for text.
  */
  private void extract(
    ContentScanner level,
    List<ContentScanner.TextStringWrapper> extractedTextStrings
    )
  {
    if(level == null)
      return;

    while(level.moveNext())
    {
      ContentObject content = level.getCurrent();
      if(content instanceof Text)
      {
        // Collect the text strings!
        extractedTextStrings.addAll(
          ((ContentScanner.TextWrapper)level.getCurrentWrapper()).getTextStrings()
          );
      }
      else if(content instanceof XObject)
      {
        // Scan the external level!
        extract(
          ((XObject)content).getScanner(level),
          extractedTextStrings
          );
      }
      else if(content instanceof ContainerObject)
      {
        // Scan the inner level!
        extract(
          level.getChildLevel(),
          extractedTextStrings
          );
      }
    }
  }

  /**
    Sorts the extracted text strings.
    <h3>Remarks</h3>
    <p>Sorting implies text position ordering, integration and aggregation.</p>

    @param rawTextStrings Source (lower-level) text strings.
    @param textStrings Target (higher-level) text strings.
   */
  private void sort(
    List<ContentScanner.TextStringWrapper> rawTextStrings,
    List<ITextString> textStrings
    )
  {
    // Sorting the source text strings...
    {
      TextStringPositionComparator positionComparator = new TextStringPositionComparator();
      Collections.sort(rawTextStrings, positionComparator);
    }

    // Aggregating and integrating the source text strings into the target ones...
    TextString textString = null;
    TextStyle textStyle = null;
    TextChar previousTextChar = null;
    boolean dehyphenating = false;
    for(ContentScanner.TextStringWrapper rawTextString : rawTextStrings)
    {
      /*
        NOTE: Contents on the same line are grouped together within the same text string.
      */
      // Add a new text string in case of new line!
      if(textString != null
        && !textString.textChars.isEmpty()
        && !TextStringPositionComparator.isOnTheSameLine(
          textString.getBox(),
          rawTextString.getBox()
          ))
      {
        if(dehyphenated
          && previousTextChar.getValue() == '-') // Hyphened word.
        {
          textString.textChars.remove(previousTextChar);
          dehyphenating = true;
        }
        else // Full word.
        {
          // Add synthesized space character!
          textString.textChars.add(
            new TextChar(
              ' ',
              new Rectangle2D.Double(
                previousTextChar.getBox().getMaxX(),
                previousTextChar.getBox().getY(),
                0,
                previousTextChar.getBox().getHeight()
                ),
              textStyle,
              true
              )
            );
          textString = null;
          dehyphenating = false;
        }
        previousTextChar = null;
      }
      if(textString == null)
      {textStrings.add(textString = new TextString());}

      textStyle = rawTextString.getStyle();
      double spaceWidth = textStyle.getFont().getWidth(' ', textStyle.getFontSize());
      if(spaceWidth == 0)
      {spaceWidth = textStyle.getFontSize() * .25f;} // NOTE: as a rule of thumb, space width is estimated according to the font size.
      for(TextChar textChar : rawTextString.getTextChars())
      {
        if(previousTextChar != null)
        {
          /*
            NOTE: PDF files may have text contents omitting space characters,
            so they must be inferred and synthesized, marking them as virtual
            in order to allow the user to distinguish between original contents
            and augmented ones.
          */
          double characterSpace = textChar.getBox().getX() - previousTextChar.getBox().getMaxX();
          if(characterSpace >= spaceWidth)
          {
            // Add synthesized space character!
            textString.textChars.add(
              previousTextChar = new TextChar(
                ' ',
                new Rectangle2D.Double(
                  previousTextChar.getBox().getMaxX(),
                  textChar.getBox().getY(),
                  characterSpace,
                  textChar.getBox().getHeight()
                  ),
                textStyle,
                true
                )
              );
          }
          if(dehyphenating
            && previousTextChar.getValue() == ' ')
          {
            textStrings.add(textString = new TextString());
            dehyphenating = false;
          }
        }
        textString.textChars.add(previousTextChar = textChar);
      }
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}