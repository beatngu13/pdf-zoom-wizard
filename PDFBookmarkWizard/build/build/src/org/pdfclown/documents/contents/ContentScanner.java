/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.ColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceGrayColor;
import org.pdfclown.documents.contents.colorSpaces.DeviceGrayColorSpace;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.objects.CompositeObject;
import org.pdfclown.documents.contents.objects.ContainerObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.InlineImage;
import org.pdfclown.documents.contents.objects.ShowText;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.documents.contents.objects.XObject;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.geom.Dimension;

/**
  Content objects scanner.
  <p>It wraps a {@link Contents content objects collection} to scan its graphics state
  through an oriented cursor.</p>
  <p>Scanning is performed at an arbitrary depth, according to the content objects nesting:
  each depth level corresponds to a scan level so that at any time it's possible
  to seamlessly navigate across the levels (see {@link #getParentLevel()},
  {@link #getChildLevel()}).</p>
  <p>Scanning can be either "dry" (simulation of graphics state stacking without actual rendering)
  or "wet" ({@link #render(Graphics2D,Dimension2D) rendering over a given device context}).

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 08/23/12
*/
public final class ContentScanner
{
  // <class>
  // <interfaces>
  /**
    Content scanner listener.
  */
  public interface IListener
  {
    /**
      Notifies the scan start.

      @param scanner Content scanner started.
    */
    void onStart(
      ContentScanner scanner
      );
  }
  // </interfaces>

  // <classes>
  /**
    Graphics state [PDF:1.6:4.3].
  */
  public static final class GraphicsState
    implements Cloneable
  {
    // <class>
    // <dynamic>
    // <fields>
    private List<BlendModeEnum> blendMode;
    private double charSpace;
    private AffineTransform ctm;
    private Color<?> fillColor;
    private ColorSpace<?> fillColorSpace;
    private Font font;
    private double fontSize;
    private double lead;
    private LineCapEnum lineCap;
    private LineDash lineDash;
    private LineJoinEnum lineJoin;
    private double lineWidth;
    private double miterLimit;
    private TextRenderModeEnum renderMode;
    private double rise;
    private double scale;
    private Color<?> strokeColor;
    private ColorSpace<?> strokeColorSpace;
    private AffineTransform tlm;
    private AffineTransform tm;
    private double wordSpace;

    private ContentScanner scanner;
    // </fields>

    // <constructors>
    private GraphicsState(
      ContentScanner scanner
      )
    {
      this.scanner = scanner;
      initialize();
    }
    // </constructors>

    // <interface>
    // <public>
    /**
      Gets a deep copy of the graphics state object.
    */
    @Override
    public GraphicsState clone(
      )
    {
      GraphicsState clone;
      {
        // Shallow copy.
        try
        {clone = (GraphicsState)super.clone();}
        catch(CloneNotSupportedException e)
        {throw new RuntimeException(e);} // NOTE: It should never happen.

        // Deep copy.
        /* NOTE: Mutable objects are to be cloned. */
        clone.ctm = (AffineTransform)ctm.clone();
        clone.tlm = (AffineTransform)tlm.clone();
        clone.tm = (AffineTransform)tm.clone();
      }
      return clone;
    }

    /**
      Copies this graphics state into the specified one.

      @param state Target graphics state object.
    */
    public void copyTo(
      GraphicsState state
      )
    {
      state.blendMode = blendMode;
      state.charSpace = charSpace;
      state.ctm = (AffineTransform)ctm.clone();
      state.fillColor = fillColor;
      state.fillColorSpace = fillColorSpace;
      state.font = font;
      state.fontSize = fontSize;
      state.lead = lead;
      state.lineCap = lineCap;
      state.lineDash = lineDash;
      state.lineJoin = lineJoin;
      state.lineWidth = lineWidth;
      state.miterLimit = miterLimit;
      state.renderMode = renderMode;
      state.rise = rise;
      state.scale = scale;
      state.strokeColor = strokeColor;
      state.strokeColorSpace = strokeColorSpace;
    //TODO:temporary hack (define TextState for textual parameters!)...
      if(state.scanner.getParent() instanceof Text)
      {
        state.tlm = (AffineTransform)tlm.clone();
        state.tm = (AffineTransform)tm.clone();
      }
      else
      {
        state.tlm = new AffineTransform();
        state.tm = new AffineTransform();
      }
      state.wordSpace = wordSpace;
    }

    /**
      Gets the current blend mode to be used in the transparent imaging model [PDF:1.6:5.2.1].
      <p>The application should use the first blend mode in the list that it recognizes.</p>
    */
    public List<BlendModeEnum> getBlendMode(
      )
    {return blendMode;}

    /**
      Gets the current character spacing [PDF:1.6:5.2.1].
    */
    public double getCharSpace(
      )
    {return charSpace;}

    /**
      Gets the current transformation matrix.
    */
    public AffineTransform getCtm(
      )
    {return ctm;}

    /**
      Gets the current color for nonstroking operations [PDF:1.6:4.5.1].
    */
    public Color<?> getFillColor(
      )
    {return fillColor;}

    /**
      Gets the current color space for nonstroking operations [PDF:1.6:4.5.1].
    */
    public ColorSpace<?> getFillColorSpace(
      )
    {return fillColorSpace;}

    /**
      Gets the current font [PDF:1.6:5.2].
    */
    public Font getFont(
      )
    {return font;}

    /**
      Gets the current font size [PDF:1.6:5.2].
    */
    public double getFontSize(
      )
    {return fontSize;}

    /**
      Gets the initial current transformation matrix.

      @since 0.1.0
    */
    public AffineTransform getInitialCtm(
      )
    {
      AffineTransform initialCtm;
      if(getScanner().getRenderContext() == null) // Device-independent.
      {
        initialCtm = new AffineTransform(); // Identity.
      }
      else // Device-dependent.
      {
        IContentContext contentContext = getScanner().getContentContext();
        Dimension2D canvasSize = getScanner().getCanvasSize();

        // Axes orientation.
        RotationEnum rotation = contentContext.getRotation();
        switch(rotation)
        {
          case Downward:
            initialCtm = new AffineTransform(1, 0, 0, -1, 0, canvasSize.getHeight());
            break;
          case Leftward:
            initialCtm = new AffineTransform(0, 1, 1, 0, 0, 0);
            break;
          case Upward:
            initialCtm = new AffineTransform(-1, 0, 0, 1, canvasSize.getWidth(), 0);
            break;
          case Rightward:
            initialCtm = new AffineTransform(0, -1, -1, 0, canvasSize.getWidth(), canvasSize.getHeight());
            break;
          default:
            throw new NotImplementedException();
        }

        // Scaling.
        Rectangle2D contentBox = contentContext.getBox();
        Dimension2D rotatedCanvasSize = rotation.transform(canvasSize);
        initialCtm.scale(
          rotatedCanvasSize.getWidth() / contentBox.getWidth(),
          rotatedCanvasSize.getHeight() / contentBox.getHeight()
          );

        // Origin alignment.
        initialCtm.translate(-contentBox.getMinX(), -contentBox.getMinY());
      }
      return initialCtm;
    }

    /**
      Gets the current leading [PDF:1.6:5.2.4].
    */
    public double getLead(
      )
    {return lead;}

    /**
      Gets the current line cap style [PDF:1.6:4.3.2].
    */
    public LineCapEnum getLineCap(
      )
    {return lineCap;}

    /**
      Gets the current line dash pattern [PDF:1.6:4.3.2].
    */
    public LineDash getLineDash(
      )
    {return lineDash;}

    /**
      Gets the current line join style [PDF:1.6:4.3.2].
    */
    public LineJoinEnum getLineJoin(
      )
    {return lineJoin;}

    /**
      Gets the current line width [PDF:1.6:4.3.2].
    */
    public double getLineWidth(
      )
    {return lineWidth;}

    /**
      Gets the current miter limit [PDF:1.6:4.3.2].
    */
    public double getMiterLimit(
      )
    {return miterLimit;}

    /**
      Gets the current text rendering mode [PDF:1.6:5.2.5].
    */
    public TextRenderModeEnum getRenderMode(
      )
    {return renderMode;}

    /**
      Gets the current text rise [PDF:1.6:5.2.6].
    */
    public double getRise(
      )
    {return rise;}

    /**
      Gets the current horizontal scaling [PDF:1.6:5.2.3].
    */
    public double getScale(
      )
    {return scale;}

    /**
      Gets the scanner associated to this state.
    */
    public ContentScanner getScanner(
      )
    {return scanner;}

    /**
      Gets the current color for stroking operations [PDF:1.6:4.5.1].
    */
    public Color<?> getStrokeColor(
      )
    {return strokeColor;}

    /**
      Gets the current color space for stroking operations [PDF:1.6:4.5.1].
    */
    public ColorSpace<?> getStrokeColorSpace(
      )
    {return strokeColorSpace;}

    /**
      Gets the current text line matrix [PDF:1.6:5.3].
    */
    public AffineTransform getTlm(
      )
    {return tlm;}

    /**
      Gets the current text matrix [PDF:1.6:5.3].
    */
    public AffineTransform getTm(
      )
    {return tm;}

    /**
      Gets the current word spacing [PDF:1.6:5.2.2].
    */
    public double getWordSpace(
      )
    {return wordSpace;}

    /**
      @see #getBlendMode()
    */
    public void setBlendMode(
      List<BlendModeEnum> value
      )
    {blendMode = value;}

    /**
      @see #getCharSpace()
    */
    public void setCharSpace(
      double value
      )
    {charSpace = value;}

    /**
      @see #getCtm()
    */
    public void setCtm(
      AffineTransform value
      )
    {ctm = value;}

    /**
      @see #getFillColor()
    */
    public void setFillColor(
      Color<?> value
      )
    {fillColor = value;}

    /**
      @see #getFillColorSpace()
    */
    public void setFillColorSpace(
      ColorSpace<?> value
      )
    {fillColorSpace = value;}

    /**
      @see #getFont()
    */
    public void setFont(
      Font value
      )
    {font = value;}

    /**
      @see #getFontSize()
    */
    public void setFontSize(
      double value
      )
    {fontSize = value;}

    /**
      @see #getLead()
    */
    public void setLead(
      double value
      )
    {lead = value;}

    /**
      @see #getLineCap()
    */
    public void setLineCap(
      LineCapEnum value
      )
    {lineCap = value;}

    /**
      @see #getLineDash()
    */
    public void setLineDash(
      LineDash value
      )
    {lineDash = value;}

    /**
      @see #getLineJoin()
    */
    public void setLineJoin(
      LineJoinEnum value
      )
    {lineJoin = value;}

    /**
      @see #getLineWidth()
    */
    public void setLineWidth(
      double value
      )
    {lineWidth = value;}

    /**
      @see #getMiterLimit()
    */
    public void setMiterLimit(
      double value
      )
    {miterLimit = value;}

    /**
      @see #getRenderMode()
    */
    public void setRenderMode(
      TextRenderModeEnum value
      )
    {renderMode = value;}

    /**
      @see #getRise()
    */
    public void setRise(
      double value
      )
    {rise = value;}

    /**
      @see #getScale()
    */
    public void setScale(
      double value
      )
    {scale = value;}

    /**
      @see #getStrokeColor()
    */
    public void setStrokeColor(
      Color<?> value
      )
    {strokeColor = value;}

    /**
      @see #getStrokeColorSpace()
    */
    public void setStrokeColorSpace(
      ColorSpace<?> value
      )
    {strokeColorSpace = value;}

    /**
      @see #getTlm()
    */
    public void setTlm(
      AffineTransform value
      )
    {tlm = value;}

    /**
      @see #getTm()
    */
    public void setTm(
      AffineTransform value
      )
    {tm = value;}

    /**
      @see #getWordSpace()
    */
    public void setWordSpace(
      double value
      )
    {wordSpace = value;}

    /**
      Resolves the given text-space point to its equivalent device-space one [PDF:1.6:5.3.3],
      expressed in standard PDF coordinate system (lower-left origin).

      @param point Point to transform.
    */
    public Point2D textToDeviceSpace(
      Point2D point
      )
    {return textToDeviceSpace(point, false);}

    /**
      Resolves the given text-space point to its equivalent device-space one [PDF:1.6:5.3.3].

      @param point Point to transform.
      @param topDown Whether the y-axis orientation has to be adjusted to common top-down orientation
        rather than standard PDF coordinate system (bottom-up).
    */
    public Point2D textToDeviceSpace(
      Point2D point,
      boolean topDown
      )
    {
      /*
        NOTE: The text rendering matrix (trm) is obtained from the concatenation
        of the current transformation matrix (ctm) and the text matrix (tm).
      */
      AffineTransform trm = topDown
        ? new AffineTransform(1, 0, 0, -1, 0, scanner.getCanvasSize().getHeight())
        : new AffineTransform();
      trm.concatenate(ctm);
      trm.concatenate(tm);
      return trm.transform(point, null);
    }

    /**
      Resolves the given user-space point to its equivalent device-space one [PDF:1.6:4.2.3],
      expressed in standard PDF coordinate system (lower-left origin).

      @param point Point to transform.
    */
    public Point2D userToDeviceSpace(
      Point2D point
      )
    {return ctm.transform(point, null);}
    // </public>

    // <private>
    private GraphicsState clone(
      ContentScanner scanner
      )
    {
      GraphicsState state = clone();
      state.scanner = scanner;
      return state;
    }

    private void initialize(
      )
    {
      // State parameters initialization.
      blendMode = Collections.emptyList();
      charSpace = 0;
      ctm = getInitialCtm();
      fillColor = DeviceGrayColor.Default;
      fillColorSpace = DeviceGrayColorSpace.Default;
      font = null;
      fontSize = 0;
      lead = 0;
      lineCap = LineCapEnum.Butt;
      lineDash = new LineDash();
      lineJoin = LineJoinEnum.Miter;
      lineWidth = 1;
      miterLimit = 10;
      renderMode = TextRenderModeEnum.Fill;
      rise = 0;
      scale = 100;
      strokeColor = DeviceGrayColor.Default;
      strokeColorSpace = DeviceGrayColorSpace.Default;
      tlm = new AffineTransform();
      tm = new AffineTransform();
      wordSpace = 0;

      // Rendering context initialization.
      Graphics2D renderContext = getScanner().getRenderContext();
      if(renderContext != null)
      {renderContext.setTransform(ctm);}
    }
    // </private>
    // </interface>
    // </dynamic>
    // </class>
  }

  /**
    Object information.
    <h3>Remarks</h3>
    <p>This class provides derivative (higher-level) information
    about the currently scanned object.</p>
  */
  public static abstract class GraphicsObjectWrapper<TDataObject extends ContentObject>
  {
    // <static>
    private static GraphicsObjectWrapper<?> get(
      ContentScanner scanner
      )
    {
      ContentObject object = scanner.getCurrent();
      if(object instanceof ShowText)
        return new TextStringWrapper(scanner);
      else if(object instanceof Text)
        return new TextWrapper(scanner);
      else if(object instanceof XObject)
        return new XObjectWrapper(scanner);
      else if(object instanceof InlineImage)
        return new InlineImageWrapper(scanner);
      else
        return null;
    }
    // </static>

    // <dynamic>
    // <fields>
    protected Rectangle2D box;

    private final TDataObject baseDataObject;
    // </fields>

    // <constructors>
    protected GraphicsObjectWrapper(
      TDataObject baseDataObject
      )
    {this.baseDataObject = baseDataObject;}
    // </constructors>

    // <interface>
    // <public>
    /**
      Gets the underlying data object.
    */
    public TDataObject getBaseDataObject(
      )
    {return baseDataObject;}

    /**
      Gets the object's bounding box.
    */
    public Rectangle2D getBox(
      )
    {return box;}
    // </public>
    // </interface>
    // </dynamic>
  }

  /**
    Inline image information.
  */
  public static final class InlineImageWrapper
    extends GraphicsObjectWrapper<InlineImage>
  {
    private InlineImageWrapper(
      ContentScanner scanner
      )
    {
      super((InlineImage)scanner.getCurrent());
      AffineTransform ctm = scanner.getState().getCtm();
      this.box = new Rectangle2D.Double(
        ctm.getTranslateX(),
        scanner.getContentContext().getBox().getHeight() - ctm.getTranslateY(),
        ctm.getScaleX(),
        Math.abs(ctm.getScaleY())
        );
    }

    /**
      Gets the inline image.
    */
    public InlineImage getInlineImage(
      )
    {return getBaseDataObject();}
  }

  /**
    Text information.
  */
  public static final class TextWrapper
    extends GraphicsObjectWrapper<Text>
  {
    private final List<TextStringWrapper> textStrings;

    private TextWrapper(
      ContentScanner scanner
      )
    {
      super((Text)scanner.getCurrent());

      textStrings = new ArrayList<TextStringWrapper>();
      extract(scanner.getChildLevel());
    }

    @Override
    public Rectangle2D getBox(
      )
    {
      if(box == null)
      {
        for(TextStringWrapper textString : textStrings)
        {
          if(box == null)
          {box = (Rectangle2D)textString.getBox().clone();}
          else
          {box.add(textString.getBox());}
        }
      }
      return box;
    }

    /**
      Gets the text strings.
    */
    public List<TextStringWrapper> getTextStrings(
      )
    {return textStrings;}

    private void extract(
      ContentScanner level
      )
    {
      if(level == null)
        return;

      while(level.moveNext())
      {
        ContentObject content = level.getCurrent();
        if(content instanceof ShowText)
        {textStrings.add((TextStringWrapper)level.getCurrentWrapper());}
        else if(content instanceof ContainerObject)
        {extract(level.getChildLevel());}
      }
    }
  }

  /**
    Text string information.
  */
  public static final class TextStringWrapper
    extends GraphicsObjectWrapper<ShowText>
    implements ITextString
  {
    private TextStyle style;
    private final List<TextChar> textChars;

    TextStringWrapper(
      ContentScanner scanner
      )
    {
      super((ShowText)scanner.getCurrent());

      textChars = new ArrayList<TextChar>();
      {
        GraphicsState state = scanner.getState();
        style = new TextStyle(
          state.getFont(),
          state.getFontSize() * state.getTm().getScaleY(),
          state.getRenderMode(),
          state.getStrokeColor(),
          state.getStrokeColorSpace(),
          state.getFillColor(),
          state.getFillColorSpace()
          );
        getBaseDataObject().scan(
          state,
          new ShowText.IScanner()
          {
            @Override
            public void scanChar(
              char textChar,
              Rectangle2D textCharBox
              )
            {
              textChars.add(
                new TextChar(
                  textChar,
                  textCharBox,
                  style,
                  false
                  )
                );
            }
          }
          );
      }
    }

    @Override
    public Rectangle2D getBox(
      )
    {
      if(box == null)
      {
        for(TextChar textChar : textChars)
        {
          if(box == null)
          {box = (Rectangle2D)textChar.getBox().clone();}
          else
          {box.add(textChar.getBox());}
        }
      }
      return box;
    }

    /**
      Gets the text style.
     */
    public TextStyle getStyle(
      )
    {return style;}

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
  }

  /**
    External object information.
  */
  public static final class XObjectWrapper
    extends GraphicsObjectWrapper<XObject>
  {
    private final PdfName name;
    private final org.pdfclown.documents.contents.xObjects.XObject xObject;

    private XObjectWrapper(
      ContentScanner scanner
      )
    {
      super((XObject)scanner.getCurrent());

      IContentContext context = scanner.getContentContext();
      AffineTransform ctm = scanner.getState().getCtm();
      this.box = new Rectangle2D.Double(
        ctm.getTranslateX(),
        context.getBox().getHeight() - ctm.getTranslateY(),
        ctm.getScaleX(),
        Math.abs(ctm.getScaleY())
        );
      this.name = getBaseDataObject().getName();
      this.xObject = getBaseDataObject().getResource(context);
    }

    /**
      Gets the corresponding resource key.
    */
    public PdfName getName(
      )
    {return name;}

    /**
      Gets the external object.
    */
    public org.pdfclown.documents.contents.xObjects.XObject getXObject(
      )
    {return xObject;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final int StartIndex = -1;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  /**
    Child level.
  */
  private ContentScanner childLevel;
  /**
    Content objects collection.
  */
  private Contents contents;
  /**
    Current object index at this level.
  */
  private int index;
  /**
    Object collection at this level.
  */
  private final List<ContentObject> objects;
  /**
    Parent level.
  */
  private final ContentScanner parentLevel;
  /**
    Current graphics state.
  */
  private GraphicsState state;

  /**
    Rendering context.
  */
  private Graphics2D renderContext;
  /**
    Rendering object.
  */
  private Shape renderObject;
  /**
    Device-space size of the rendering canvas.
  */
  private Dimension2D renderSize;

  /**
    Scan listeners.
  */
  List<IListener> listeners = new ArrayList<IListener>();
  // </fields>

  // <constructors>
  /**
    Instantiates a top-level content scanner.

    @param contents Content objects collection to scan.
  */
  public ContentScanner(
    Contents contents
    )
  {
    this.parentLevel = null;
    this.objects = this.contents = contents;

    moveStart();
  }

  /**
    Instantiates a top-level content scanner.

    @param contentContext Content context containing the content objects collection to scan.
  */
  public ContentScanner(
    IContentContext contentContext
    )
  {this(contentContext.getContents());}

  /**
    Instantiates a child-level content scanner for {@link FormXObject external form}.

    @param formXObject External form.
    @param parentLevel Parent scan level.
  */
  public ContentScanner(
    final FormXObject formXObject,
    ContentScanner parentLevel
    )
  {
    this.parentLevel = parentLevel;
    this.objects = this.contents = formXObject.getContents();

    addListener(new IListener()
      {
        @Override
        public void onStart(
          ContentScanner scanner
          )
        {
          // Adjust the initial graphics state to the external form context!
          scanner.getState().getCtm().concatenate(formXObject.getMatrix());
          /*
            TODO: On rendering, clip according to the form dictionary's BBox entry!
          */
        }
      });
    moveStart();
  }

  /**
    Instantiates a child-level content scanner.

    @param parentLevel Parent scan level.
  */
  private ContentScanner(
    ContentScanner parentLevel
    )
  {
    this.parentLevel = parentLevel;
    this.contents = parentLevel.contents;
    this.objects = ((CompositeObject)parentLevel.getCurrent()).getObjects();

    moveStart();
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Adds the specified listener.

    @param listener Listener to add.
  */
  public void addListener(
    IListener listener
    )
  {listeners.add(listener);}

  /**
    Gets the size of the current imageable area.
    <p>It can be either the <i>user-space area</i> (dry scanning)
    or the <i>device-space area</i> (wet scanning).</p>
  */
  public Dimension2D getCanvasSize(
    )
  {
    return renderSize == null
      ? Dimension.get(getContentContext().getBox()) // Device-independent (user-space) area.
      : renderSize; // Device-dependent (device-space) area.
  }

  /**
    Gets the current child scan level.

    @see #getParentLevel()
    @see #getRootLevel()
  */
  public ContentScanner getChildLevel(
    )
  {return childLevel;}

  /**
    Gets the content context associated to the content objects collection.
  */
  public IContentContext getContentContext(
    )
  {return contents.getContentContext();}

  /**
    Gets the content objects collection this scanner is inspecting.
  */
  public Contents getContents(
    )
  {return contents;}

  /**
    Gets the current content object.

    @see #getIndex()
    @see #getParent()
  */
  public ContentObject getCurrent(
    )
  {
    if(index < 0 || index >= objects.size())
      return null;

    return objects.get(index);
  }

  /**
    Gets the current content object's information.

    @see #getCurrent()
  */
  public GraphicsObjectWrapper<?> getCurrentWrapper(
    )
  {return GraphicsObjectWrapper.get(this);}

  /**
    Gets the current position.

    @see #getCurrent()
  */
  public int getIndex(
    )
  {return index;}

  /**
    Gets the current parent object.

    @see #getCurrent()
  */
  public CompositeObject getParent(
    )
  {return (parentLevel == null ? null : (CompositeObject)parentLevel.getCurrent());}

  /**
    Gets the parent scan level.

    @see #getChildLevel()
    @see #getRootLevel()
  */
  public ContentScanner getParentLevel(
    )
  {return parentLevel;}

  /**
    Gets the rendering context.

    @return <code>null</code> in case of dry scanning.
  */
  public Graphics2D getRenderContext(
    )
  {return renderContext;}

  /**
    Gets the rendering object.

    @return <code>null</code> in case of scanning outside a shape.
  */
  public Shape getRenderObject(
    )
  {return renderObject;}

  /**
    Gets the root scan level.

    @see #getChildLevel()
    @see #getParentLevel()
  */
  public ContentScanner getRootLevel(
    )
  {
    ContentScanner level = this;
    while(true)
    {
      ContentScanner parentLevel = level.getParentLevel();
      if(parentLevel == null)
        return level;

      level = parentLevel;
    }
  }

  /**
    Gets the graphics state applied to the current content object.
  */
  public GraphicsState getState(
    )
  {return state;}

  /**
    Inserts a content object at the current position.
  */
  public void insert(
    ContentObject object
    )
  {
    if(index == -1)
    {index = 0;}

    objects.add(index,object);
    refresh();
  }

  /**
    Inserts content objects at the current position.
    <p>After the insertion is complete, the lastly-inserted content object is at the current position.</p>
  */
  public void insert(
    Collection<? extends ContentObject> objects
    )
  {
    int index = 0;
    int size = objects.size();
    for(ContentObject object : objects)
    {
      insert(object);

      if(++index < size)
      {moveNext();}
    }
  }

  /**
    Gets whether this level is the root of the hierarchy.
  */
  public boolean isRootLevel(
    )
  {return parentLevel == null;}

  /**
    Moves to the object at the given position.

    @param index New position.
    @return Whether the object was successfully reached.
  */
  public boolean move(
    int index
    )
  {
    if(this.index > index)
    {moveStart();}

    while(this.index < index
      && moveNext());

    return getCurrent() != null;
  }

  /**
    Moves after the last object.
  */
  public void moveEnd(
    )
  {moveLast(); moveNext();}

  /**
    Moves to the first object.

    @return Whether the first object was successfully reached.
  */
  public boolean moveFirst(
    )
  {moveStart(); return moveNext();}

  /**
    Moves to the last object.

    @return Whether the last object was successfully reached.
  */
  public boolean moveLast(
    )
  {
    int lastIndex = objects.size()-1;
    while(index < lastIndex)
    {moveNext();}

    return getCurrent() != null;
  }

  /**
    Moves to the next object.

    @return Whether the next object was successfully reached.
  */
  public boolean moveNext(
    )
  {
    // Scanning the current graphics object...
    ContentObject currentObject = getCurrent();
    if(currentObject != null)
    {currentObject.scan(state);}

    // Moving to the next object...
    if(index < objects.size())
    {index++; refresh();}

    return getCurrent() != null;
  }

  /**
    Moves before the first object.
  */
  public void moveStart(
    )
  {
    index = StartIndex;
    if(state == null)
    {
      if(parentLevel == null)
      {state = new GraphicsState(this);}
      else
      {state = parentLevel.state.clone(this);}
    }
    else
    {
      if(parentLevel == null)
      {state.initialize();}
      else
      {parentLevel.state.copyTo(state);}
    }

    notifyStart();

    refresh();
  }

  /**
    Removes the content object at the current position.

    @return Removed object.
  */
  public ContentObject remove(
    )
  {
    ContentObject removedObject = objects.remove(index);
    refresh();

    return removedObject;
  }

  /**
    Removes the specified listener.

    @param listener Listener to remove.
    @return Whether the specified listener has been removed.
  */
  public boolean removeListener(
    IListener listener
    )
  {return listeners.remove(listener);}

  /**
    Renders the contents into the specified context.

    @param renderContext Rendering context.
    @param renderSize Rendering canvas size.
    @since 0.1.0
  */
  public void render(
    Graphics2D renderContext,
    Dimension2D renderSize
    )
  {render(renderContext, renderSize, null);}

  /**
    Renders the contents into the specified object.

    @param renderContext Rendering context.
    @param renderSize Rendering canvas size.
    @param renderObject Rendering object.
    @since 0.1.0
  */
  public void render(
    Graphics2D renderContext,
    Dimension2D renderSize,
    Shape renderObject
    )
  {
    if(isRootLevel())
    {
      // Initialize the context!
      renderContext.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
        );
      renderContext.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

      // Paint the canvas background!
      renderContext.setColor(java.awt.Color.WHITE);
      renderContext.fillRect(0,0,(int)renderSize.getWidth(),(int)renderSize.getHeight());
    }

    try
    {
      this.renderContext = renderContext;
      this.renderSize = renderSize;
      this.renderObject = renderObject;

      // Scan this level for rendering!
      moveStart();
      while(moveNext());
    }
    finally
    {
      this.renderContext = null;
      this.renderSize = null;
      this.renderObject = null;
    }
  }

  /**
    Replaces the content object at the current position.

    @return Replaced object.
  */
  public ContentObject setCurrent(
    ContentObject value
    )
  {
    ContentObject replacedObject = objects.set(index,value);
    refresh();

    return replacedObject;
  }
  // </public>

  // <protected>
  /**
    Notifies the scan start to listeners.
  */
  protected void notifyStart(
    )
  {
    for(IListener listener : listeners)
    {listener.onStart(this);}
  }
  // </protected>

  // <private>
  /**
    Synchronizes the scanner state.
  */
  private void refresh(
    )
  {
    if(getCurrent() instanceof CompositeObject)
    {childLevel = new ContentScanner(this);}
    else
    {childLevel = null;}
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}