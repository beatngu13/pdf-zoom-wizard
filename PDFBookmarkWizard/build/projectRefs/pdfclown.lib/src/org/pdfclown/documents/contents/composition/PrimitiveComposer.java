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

package org.pdfclown.documents.contents.composition;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.ExtGState;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.LineCapEnum;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.LineJoinEnum;
import org.pdfclown.documents.contents.PropertyList;
import org.pdfclown.documents.contents.ResourceItems;
import org.pdfclown.documents.contents.TextRenderModeEnum;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceCMYKColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceGrayColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColorSpace;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.layers.LayerEntity;
import org.pdfclown.documents.contents.objects.ApplyExtGState;
import org.pdfclown.documents.contents.objects.BeginMarkedContent;
import org.pdfclown.documents.contents.objects.BeginSubpath;
import org.pdfclown.documents.contents.objects.CloseSubpath;
import org.pdfclown.documents.contents.objects.CompositeObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.DrawCurve;
import org.pdfclown.documents.contents.objects.DrawLine;
import org.pdfclown.documents.contents.objects.DrawRectangle;
import org.pdfclown.documents.contents.objects.LocalGraphicsState;
import org.pdfclown.documents.contents.objects.MarkedContent;
import org.pdfclown.documents.contents.objects.ModifyCTM;
import org.pdfclown.documents.contents.objects.ModifyClipPath;
import org.pdfclown.documents.contents.objects.PaintPath;
import org.pdfclown.documents.contents.objects.PaintXObject;
import org.pdfclown.documents.contents.objects.SetCharSpace;
import org.pdfclown.documents.contents.objects.SetFillColor;
import org.pdfclown.documents.contents.objects.SetFillColorSpace;
import org.pdfclown.documents.contents.objects.SetFont;
import org.pdfclown.documents.contents.objects.SetLineCap;
import org.pdfclown.documents.contents.objects.SetLineDash;
import org.pdfclown.documents.contents.objects.SetLineJoin;
import org.pdfclown.documents.contents.objects.SetLineWidth;
import org.pdfclown.documents.contents.objects.SetMiterLimit;
import org.pdfclown.documents.contents.objects.SetStrokeColor;
import org.pdfclown.documents.contents.objects.SetStrokeColorSpace;
import org.pdfclown.documents.contents.objects.SetTextLead;
import org.pdfclown.documents.contents.objects.SetTextMatrix;
import org.pdfclown.documents.contents.objects.SetTextRenderMode;
import org.pdfclown.documents.contents.objects.SetTextRise;
import org.pdfclown.documents.contents.objects.SetTextScale;
import org.pdfclown.documents.contents.objects.SetWordSpace;
import org.pdfclown.documents.contents.objects.ShowSimpleText;
import org.pdfclown.documents.contents.objects.Text;
import org.pdfclown.documents.contents.objects.TranslateTextRelative;
import org.pdfclown.documents.contents.objects.TranslateTextToNextLine;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.annotations.Link;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.math.geom.Quad;

/**
  Content stream primitive composer.
  <p>It provides the basic (primitive) operations described by the PDF specification for graphics
  content composition.</p>
  <p>This class leverages the object-oriented content stream modelling infrastructure, which
  encompasses 1st-level content stream objects (operations), 2nd-level content stream objects
  (graphics objects) and full graphics state support.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 09/24/12
*/
public final class PrimitiveComposer
{
  // <class>
  // <dynamic>
  // <fields>
  private ContentScanner scanner;
  // </fields>

  // <constructors>
  public PrimitiveComposer(
    ContentScanner scanner
    )
  {setScanner(scanner);}

  public PrimitiveComposer(
    IContentContext context
    )
  {
    this(
      new ContentScanner(context.getContents())
      );
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Adds a content object.

    @return The added content object.
  */
  public ContentObject add(
    ContentObject object
    )
  {
    scanner.insert(object);
    scanner.moveNext();

    return object;
  }

  /**
    Applies a transformation to the coordinate system from user space to device space [PDF:1.6:4.3.3].
    <p>The transformation is applied to the current transformation matrix (CTM) by concatenation,
    i.e. it doesn't replace it.</p>

    @param a Item 0,0 of the matrix.
    @param b Item 0,1 of the matrix.
    @param c Item 1,0 of the matrix.
    @param d Item 1,1 of the matrix.
    @param e Item 2,0 of the matrix.
    @param f Item 2,1 of the matrix.
    @see #setMatrix(double,double,double,double,double,double)
  */
  public void applyMatrix(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f
    )
  {add(new ModifyCTM(a,b,c,d,e,f));}

  /**
    Applies the specified state parameters [PDF:1.6:4.3.4].

    @param name Resource identifier of the state parameters object.
  */
  public void applyState(
    PdfName name
    )
  {
    // Doesn't the state exist in the context resources?
    if(!scanner.getContentContext().getResources().getExtGStates().containsKey(name))
      throw new IllegalArgumentException("No state resource associated to the given argument (name:'name'; value:'" + name + "';)");

    applyState_(name);
  }

  /**
    Applies the specified state parameters [PDF:1.6:4.3.4].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #applyState(PdfName) #applyState(PdfName)}.</p>

    @param state State parameters object.
  */
  public void applyState(
    ExtGState state
    )
  {applyState_(getResourceName(state));}

  /**
    Adds a composite object beginning it.

    @return Added composite object.
    @see #end()
  */
  public CompositeObject begin(
    CompositeObject object
    )
  {
    // Insert the new object at the current level!
    scanner.insert(object);
    // The new object's children level is the new current level!
    scanner = scanner.getChildLevel();

    return object;
  }

  /**
    Begins a new layered-content sequence [PDF:1.6:4.10.2].

    @param layer Layer entity enclosing the layered content.
    @return Added layered-content sequence.
    @see #end()
  */
  public MarkedContent beginLayer(
    LayerEntity layer
    )
  {return beginLayer(getResourceName(layer.getMembership()));}

  /**
    Begins a new layered-content sequence [PDF:1.6:4.10.2].

    @param layerName Resource identifier of the {@link LayerEntity} enclosing the layered content.
    @return Added layered-content sequence.
    @see #end()
  */
  public MarkedContent beginLayer(
    PdfName layerName
    )
  {return beginMarkedContent(PdfName.OC, layerName);}

  /**
    Begins a new nested graphics state context [PDF:1.6:4.3.1].

    @return Added local graphics state object.
    @see #end()
  */
  public LocalGraphicsState beginLocalState(
    )
  {return (LocalGraphicsState)begin(new LocalGraphicsState());}

  /**
    Begins a new marked-content sequence [PDF:1.6:10.5].

    @param tag Marker indicating the role or significance of the marked content.
    @return Added marked-content sequence.
    @see #end()
  */
  public MarkedContent beginMarkedContent(
    PdfName tag
    )
  {return beginMarkedContent(tag, (PdfName)null);}

  /**
    Begins a new marked-content sequence [PDF:1.6:10.5].

    @param tag Marker indicating the role or significance of the marked content.
    @param propertyList {@link PropertyList} describing the marked content.
    @return Added marked-content sequence.
    @see #end()
  */
  public MarkedContent beginMarkedContent(
    PdfName tag,
    PropertyList propertyList
    )
  {return beginMarkedContent_(tag, getResourceName(propertyList));}

  /**
    Begins a new marked-content sequence [PDF:1.6:10.5].

    @param tag Marker indicating the role or significance of the marked content.
    @param propertyListName Resource identifier of the {@link PropertyList} describing the marked
      content.
    @return Added marked-content sequence.
    @see #end()
  */
  public MarkedContent beginMarkedContent(
    PdfName tag,
    PdfName propertyListName
    )
  {
    // Doesn't the property list exist in the context resources?
    if(propertyListName != null && !scanner.getContentContext().getResources().getPropertyLists().containsKey(propertyListName))
      throw new IllegalArgumentException("No property list resource associated to the given argument (name:'name'; value:'" + propertyListName + "';)");

    return beginMarkedContent_(tag, propertyListName);
  }

  /**
    Modifies the current clipping path by intersecting it with the current path [PDF:1.6:4.4.1].
    <p>It can be validly called only just before painting the current path.</p>
  */
  public void clip(
    )
  {
    add(ModifyClipPath.NonZero);
    add(PaintPath.EndPathNoOp);
  }

  /**
    Closes the current subpath by appending a straight line segment from the current point to the
    starting point of the subpath [PDF:1.6:4.4.1].
  */
  public void closePath(
    )
  {add(CloseSubpath.Value);}

  /**
    Draws a circular arc.

    @param location Arc location.
    @param startAngle Starting angle.
    @param endAngle Ending angle.
    @see #stroke()
  */
  public void drawArc(
    RectangularShape location,
    double startAngle,
    double endAngle
    )
  {drawArc(location,startAngle,endAngle,0,1);}

  /**
    Draws an arc.

    @param location Arc location.
    @param startAngle Starting angle.
    @param endAngle Ending angle.
    @param branchWidth Distance between the spiral branches. '0' value degrades to a circular arc.
    @param branchRatio Linear coefficient applied to the branch width. '1' value degrades to a
      constant branch width.
    @see #stroke()
  */
  public void drawArc(
    RectangularShape location,
    double startAngle,
    double endAngle,
    double branchWidth,
    double branchRatio
    )
  {drawArc(location,startAngle,endAngle,branchWidth,branchRatio,true);}

  /**
    Draws a cubic Bezier curve from the current point [PDF:1.6:4.4.1].

    @param endPoint Ending point.
    @param startControl Starting control point.
    @param endControl Ending control point.
    @see #stroke()
  */
  public void drawCurve(
    Point2D endPoint,
    Point2D startControl,
    Point2D endControl
    )
  {
    double contextHeight = scanner.getContentContext().getBox().getHeight();
    add(
      new DrawCurve(
        endPoint.getX(),
        contextHeight - endPoint.getY(),
        startControl.getX(),
        contextHeight - startControl.getY(),
        endControl.getX(),
        contextHeight - endControl.getY()
        )
      );
  }

  /**
    Draws a cubic Bezier curve [PDF:1.6:4.4.1].

    @param startPoint Starting point.
    @param endPoint Ending point.
    @param startControl Starting control point.
    @param endControl Ending control point.
    @see #stroke()
  */
  public void drawCurve(
    Point2D startPoint,
    Point2D endPoint,
    Point2D startControl,
    Point2D endControl
    )
  {
    startPath(startPoint);
    drawCurve(endPoint,startControl,endControl);
  }

  /**
    Draws an ellipse.

    @param location Ellipse location.
    @see #fill()
    @see #fillStroke()
    @see #stroke()
  */
  public void drawEllipse(
    RectangularShape location
    )
  {drawArc(location,0,360);}

  /**
    Draws a line from the current point [PDF:1.6:4.4.1].

    @param endPoint Ending point.
    @see #stroke()
  */
  public void drawLine(
    Point2D endPoint
    )
  {
    add(
      new DrawLine(
        endPoint.getX(),
        scanner.getContentContext().getBox().getHeight() - endPoint.getY()
        )
      );
  }

  /**
    Draws a line [PDF:1.6:4.4.1].

    @param startPoint Starting point.
    @param endPoint Ending point.
    @see #stroke()
  */
  public void drawLine(
    Point2D startPoint,
    Point2D endPoint
    )
  {
    startPath(startPoint);
    drawLine(endPoint);
  }

  /**
    Draws a polygon.
    <p>A polygon is the same as a multiple line except that it's a closed path.</p>

    @param points Points.
    @see #fill()
    @see #fillStroke()
    @see #stroke()
  */
  public void drawPolygon(
    Point2D[] points
    )
  {
    drawPolyline(points);
    closePath();
  }

  /**
    Draws a multiple line.

    @param points Points.
    @see #stroke()
  */
  public void drawPolyline(
    Point2D[] points
    )
  {
    startPath(points[0]);
    for(
      int index = 1,
        length = points.length;
      index < length;
      index++
      )
    {drawLine(points[index]);}
  }

  /**
    Draws a rectangle [PDF:1.6:4.4.1].

    @param location Rectangle location.
    @see #fill()
    @see #fillStroke()
    @see #stroke()
  */
  public void drawRectangle(
    RectangularShape location
    )
  {drawRectangle(location,0);}

  /**
    Draws a rounded rectangle.

    @param location Rectangle location.
    @param radius Vertex radius, '0' value degrades to squared vertices.
    @see #fill()
    @see #fillStroke()
    @see #stroke()
  */
  public void drawRectangle(
    RectangularShape location,
    double radius
    )
  {
    if(radius == 0)
    {
      add(
        new DrawRectangle(
          location.getX(),
          scanner.getContentContext().getBox().getHeight() - location.getY() - location.getHeight(),
          location.getWidth(),
          location.getHeight()
          )
        );
    }
    else
    {
      final double endRadians = Math.PI * 2;
      final double quadrantRadians = Math.PI / 2;
      double radians = 0;
      while(radians < endRadians)
      {
        double radians2 = radians + quadrantRadians;
        int sin2 = (int)Math.sin(radians2);
        int cos2 = (int)Math.cos(radians2);
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        double xArc = 0, yArc = 0;
        if(cos2 == 0)
        {
          if(sin2 == 1)
          {
            x1 = x2 = location.getX() + location.getWidth();
            y1 = location.getY() + location.getHeight() - radius;
            y2 = location.getY() + radius;

            xArc =- radius * 2;
            yArc =- radius;

            startPath(new Point2D.Double(x1,y1));
          }
          else
          {
            x1 = x2 = location.getX();
            y1 = location.getY() + radius;
            y2 = location.getY() + location.getHeight() - radius;

            yArc =- radius;
          }
        }
        else if(cos2 == 1)
        {
          x1 = location.getX() + radius;
          x2 = location.getX() + location.getWidth() - radius;
          y1 = y2 = location.getY() + location.getHeight();

          xArc =- radius;
          yArc =- radius*2;
        }
        else if(cos2 == -1)
        {
          x1 = location.getX() + location.getWidth() - radius;
          x2 = location.getX() + radius;
          y1 = y2 = location.getY();

          xArc=-radius;
        }
        drawLine(
          new Point2D.Double(x2,y2)
          );
        drawArc(
          new Rectangle2D.Double(x2+xArc, y2+yArc, radius*2, radius*2),
          Math.toDegrees(radians),
          Math.toDegrees(radians2),
          0,
          1,
          false
          );

        radians = radians2;
      }
    }
  }

  /**
    Draws a spiral.

    @param center Spiral center.
    @param startAngle Starting angle.
    @param endAngle Ending angle.
    @param branchWidth Distance between the spiral branches.
    @param branchRatio Linear coefficient applied to the branch width.
    @see #stroke()
  */
  public void drawSpiral(
    Point2D center,
    double startAngle,
    double endAngle,
    double branchWidth,
    double branchRatio
    )
  {
    drawArc(
      new Rectangle2D.Double(center.getX(),center.getY(),0.0001,0.0001),
      startAngle,
      endAngle,
      branchWidth,
      branchRatio
      );
  }

  /**
    Ends the current (innermostly-nested) composite object.

    @see #begin(CompositeObject)
  */
  public void end(
    )
  {
    scanner = scanner.getParentLevel();
    scanner.moveNext();
  }

  /**
    Fills the path using the current color [PDF:1.6:4.4.2].

    @see #setFillColor(Color)
  */
  public void fill(
    )
  {add(PaintPath.Fill);}

  /**
    Fills and then strokes the path using the current colors [PDF:1.6:4.4.2].

    @see #setFillColor(Color)
    @see #setStrokeColor(Color)
  */
  public void fillStroke(
    )
  {add(PaintPath.FillStroke);}

  /**
    Serializes the contents into the content stream.
  */
  public void flush(
    )
  {scanner.getContents().flush();}

  /**
    Gets the content stream scanner.
  */
  public ContentScanner getScanner(
    )
  {return scanner;}

  /**
    Gets the current graphics state [PDF:1.6:4.3].
  */
  public ContentScanner.GraphicsState getState(
    )
  {return scanner.getState();}

  /**
    Applies a rotation to the coordinate system from user space to device space [PDF:1.6:4.2.2].

    @param angle Rotational counterclockwise angle.
    @see #applyMatrix(double,double,double,double,double,double)
  */
  public void rotate(
    double angle
    )
  {
    double rad = angle * Math.PI / 180;
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);
    applyMatrix(cos, sin, -sin, cos, 0, 0);
  }

  /**
    Applies a rotation to the coordinate system from user space to device space [PDF:1.6:4.2.2].

    @param angle Rotational counterclockwise angle.
    @param origin Rotational pivot point; it becomes the new coordinates origin.
    @see #applyMatrix(double,double,double,double,double,double)
  */
  public void rotate(
    double angle,
    Point2D origin
    )
  {
    // Center to the new origin!
    translate(
      origin.getX(),
      scanner.getContentContext().getBox().getHeight() - origin.getY()
      );
    // Rotate on the new origin!
    rotate(angle);
    // Restore the standard vertical coordinates system!
    translate(
      0,
      -scanner.getContentContext().getBox().getHeight()
      );
  }

  /**
    Applies a scaling to the coordinate system from user space to device space [PDF:1.6:4.2.2].

    @param ratioX Horizontal scaling ratio.
    @param ratioY Vertical scaling ratio.
    @see #applyMatrix(double,double,double,double,double,double)
  */
  public void scale(
    double ratioX,
    double ratioY
    )
  {applyMatrix(ratioX, 0, 0, ratioY, 0, 0);}

  /**
    Sets the character spacing parameter [PDF:1.6:5.2.1].
  */
  public void setCharSpace(
    double value
    )
  {add(new SetCharSpace(value));}

  /**
    Sets the nonstroking color value [PDF:1.6:4.5.7].

    @see #setStrokeColor(Color)
  */
  public void setFillColor(
    Color<?> value
    )
  {
    if(!scanner.getState().getFillColorSpace().equals(value.getColorSpace()))
    {
      // Set filling color space!
      add(new SetFillColorSpace(getResourceName(value.getColorSpace())));
    }
    add(new SetFillColor(value));
  }

  /**
    Sets the font [PDF:1.6:5.2].

    @param name Resource identifier of the font.
    @param size Scaling factor (points).
  */
  public void setFont(
    PdfName name,
    double size
    )
  {
    // Doesn't the font exist in the context resources?
    if(!scanner.getContentContext().getResources().getFonts().containsKey(name))
      throw new IllegalArgumentException("No font resource associated to the given argument (name:'name'; value:'" + name + "';)");

    setFont_(name,size);
  }

  /**
    Sets the font [PDF:1.6:5.2].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #setFont(PdfName,double) setFont(PdfName,double)}.</p>

    @param value Font.
    @param size Scaling factor (points).
  */
  public void setFont(
    Font value,
    double size
    )
  {setFont_(getResourceName(value),size);}

  /**
    Sets the text horizontal scaling [PDF:1.6:5.2.3].
  */
  public void setTextScale(
    double value
    )
  {add(new SetTextScale(value));}

  /**
    Sets the text leading [PDF:1.6:5.2.4].
  */
  public void setTextLead(
    double value
    )
  {add(new SetTextLead(value));}

  /**
    Sets the line cap style [PDF:1.6:4.3.2].
  */
  public void setLineCap(
    LineCapEnum value
    )
  {add(new SetLineCap(value));}

  /**
    Sets the line dash pattern [PDF:1.6:4.3.2].
  */
  public void setLineDash(
    LineDash value
    )
  {add(new SetLineDash(value));}

  /**
    Sets the line join style [PDF:1.6:4.3.2].
  */
  public void setLineJoin(
    LineJoinEnum value
    )
  {add(new SetLineJoin(value));}

  /**
    Sets the line width [PDF:1.6:4.3.2].
  */
  public void setLineWidth(
    double value
    )
  {add(new SetLineWidth(value));}

  /**
    Sets the transformation of the coordinate system from user space to device space [PDF:1.6:4.3.3].
    <p>The transformation replaces the current transformation matrix (CTM).</p>

    @param a Item 0,0 of the matrix.
    @param b Item 0,1 of the matrix.
    @param c Item 1,0 of the matrix.
    @param d Item 1,1 of the matrix.
    @param e Item 2,0 of the matrix.
    @param f Item 2,1 of the matrix.
    @see #applyMatrix(double,double,double,double,double,double)
  */
  public void setMatrix(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f
    )
  {
    // Reset the CTM!
    add(ModifyCTM.getResetCTM(scanner.getState()));
    // Apply the transformation!
    add(new ModifyCTM(a,b,c,d,e,f));
  }

  /**
    Sets the miter limit [PDF:1.6:4.3.2].
  */
  public void setMiterLimit(
    double value
    )
  {add(new SetMiterLimit(value));}

  /**
    @see #getScanner()
  */
  public void setScanner(
    ContentScanner value
    )
  {scanner = value;}

  /**
    Sets the stroking color value [PDF:1.6:4.5.7].

    @see #setFillColor(Color)
  */
  public void setStrokeColor(
    Color<?> value
    )
  {
    if(!scanner.getState().getStrokeColorSpace().equals(value.getColorSpace()))
    {
      // Set stroking color space!
      add(new SetStrokeColorSpace(getResourceName(value.getColorSpace())));
    }

    add(new SetStrokeColor(value));
  }

  /**
    Sets the text rendering mode [PDF:1.6:5.2.5].
  */
  public void setTextRenderMode(
    TextRenderModeEnum value
    )
  {add(new SetTextRenderMode(value));}

  /**
    Sets the text rise [PDF:1.6:5.2.6].
  */
  public void setTextRise(
    double value
    )
  {add(new SetTextRise(value));}

  /**
    Sets the word spacing [PDF:1.6:5.2.2].
  */
  public void setWordSpace(
    double value
    )
  {add(new SetWordSpace(value));}

  /**
    Shows the specified text on the page at the current location [PDF:1.6:5.3.2].

    @param value Text to show.
    @return Bounding box vertices in default user space units.
  */
  public Quad showText(
    String value
    )
  {
    return showText(
      value,
      new Point2D.Double(0,0)
      );
  }

  /**
    Shows the link associated to the specified text on the page at the current location.

    @param value Text to show.
    @param action Action to apply when the link is activated.
    @return Link.
  */
  public Link showText(
    String value,
    Action action
    )
  {
    return showText(
      value,
      new Point2D.Double(0,0),
      action
      );
  }

  /**
    Shows the specified text on the page at the specified location [PDF:1.6:5.3.2].

    @param value Text to show.
    @param location Position at which showing the text.
    @return Bounding box vertices in default user space units.
  */
  public Quad showText(
    String value,
    Point2D location
    )
  {
    return showText(
      value,
      location,
      XAlignmentEnum.Left,
      YAlignmentEnum.Top,
      0
      );
  }

  /**
    Shows the link associated to the specified text on the page at the specified location.

    @param value Text to show.
    @param location Position at which showing the text.
    @param action Action to apply when the link is activated.
    @return Link.
  */
  public Link showText(
    String value,
    Point2D location,
    Action action
    )
  {
    return showText(
      value,
      location,
      XAlignmentEnum.Left,
      YAlignmentEnum.Top,
      0,
      action
      );
  }

  /**
    Shows the specified text on the page at the specified location [PDF:1.6:5.3.2].

    @param value Text to show.
    @param location Anchor position at which showing the text.
    @param xAlignment Horizontal alignment.
    @param yAlignment Vertical alignment.
    @param rotation Rotational counterclockwise angle.
    @return Bounding box vertices in default user space units.
  */
  public Quad showText(
    String value,
    Point2D location,
    XAlignmentEnum xAlignment,
    YAlignmentEnum yAlignment,
    double rotation
    )
  {
    ContentScanner.GraphicsState state = scanner.getState();
    Font font = state.getFont();
    double fontSize = state.getFontSize();
    double x = location.getX();
    double y = location.getY();
    double width = font.getKernedWidth(value,fontSize);
    double height = font.getLineHeight(fontSize);
    double descent = font.getDescent(fontSize);
    Quad frame;
    if(xAlignment == XAlignmentEnum.Left
      && yAlignment == YAlignmentEnum.Top)
    {
      beginText();
      try
      {
        if(rotation == 0)
        {
          translateText(
            x,
            scanner.getContentContext().getBox().getHeight() - y - font.getAscent(fontSize)
            );
        }
        else
        {
          double rad = rotation * Math.PI / 180.0;
          double cos = Math.cos(rad);
          double sin = Math.sin(rad);

          setTextMatrix(
            cos,
            sin,
            -sin,
            cos,
            x,
            scanner.getContentContext().getBox().getHeight() - y - font.getAscent(fontSize)
            );
        }

        state = scanner.getState();
        frame = new Quad(
          state.textToDeviceSpace(new Point2D.Double(0, descent), true),
          state.textToDeviceSpace(new Point2D.Double(width, descent), true),
          state.textToDeviceSpace(new Point2D.Double(width, height + descent), true),
          state.textToDeviceSpace(new Point2D.Double(0, height + descent), true)
          );

        // Add the text!
        add(new ShowSimpleText(font.encode(value)));
      }
      finally
      {end();} // Ends the text object.
    }
    else
    {
      beginLocalState();
      try
      {
        // Coordinates transformation.
        double cos, sin;
        if(rotation == 0)
        {
          cos = 1;
          sin = 0;
        }
        else
        {
          double rad = rotation * Math.PI / 180.0;
          cos = Math.cos(rad);
          sin = Math.sin(rad);
        }
        // Apply the transformation!
        applyMatrix(
          cos,
          sin,
          -sin,
          cos,
          x,
          scanner.getContentContext().getBox().getHeight() - y
          );

        beginText();
        try
        {
          // Text coordinates adjustment.
          switch(xAlignment)
          {
            case Left:
              x = 0;
              break;
            case Right:
              x = -width;
              break;
            case Center:
            case Justify:
              x = -width / 2;
              break;
          }
          switch(yAlignment)
          {
            case Top:
              y = -font.getAscent(fontSize);
              break;
            case Bottom:
              y = height - font.getAscent(fontSize);
              break;
            case Middle:
              y = height / 2 - font.getAscent(fontSize);
              break;
          }
          // Apply the text coordinates adjustment!
          translateText(x,y);

          state = scanner.getState();
          frame = new Quad(
            state.textToDeviceSpace(new Point2D.Double(0, descent), true),
            state.textToDeviceSpace(new Point2D.Double(width, descent), true),
            state.textToDeviceSpace(new Point2D.Double(width, height + descent), true),
            state.textToDeviceSpace(new Point2D.Double(0, height + descent), true)
            );

          // Add the text!
          add(new ShowSimpleText(font.encode(value)));
        }
        finally
        {end();} // Ends the text object.
      }
      finally
      {end();} // Ends the local state.
    }
    return frame;
  }

  /**
    Shows the link associated to the specified text on the page at the specified location.

    @param value Text to show.
    @param location Anchor position at which showing the text.
    @param xAlignment Horizontal alignment.
    @param yAlignment Vertical alignment.
    @param rotation Rotational counterclockwise angle.
    @param action Action to apply when the link is activated.
    @return Link.
  */
  public Link showText(
    String value,
    Point2D location,
    XAlignmentEnum xAlignment,
    YAlignmentEnum yAlignment,
    double rotation,
    Action action
    )
  {
    IContentContext contentContext = scanner.getContentContext();
    if(!(contentContext instanceof Page))
      throw new RuntimeException("Links can be shown only on page contexts.");

    Rectangle2D linkBox = showText(
      value,
      location,
      xAlignment,
      yAlignment,
      rotation
      ).getBounds2D();

    return new Link(
      (Page)contentContext,
      linkBox,
      null,
      action
      );
  }

  /**
    Shows the specified external object [PDF:1.6:4.7].

    @param name Resource identifier of the external object.
  */
  public void showXObject(
    PdfName name
    )
  {add(new PaintXObject(name));}

  /**
    Shows the specified external object [PDF:1.6:4.7].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #showXObject(PdfName) #showXObject(PdfName)}.</p>

    @param value External object.
  */
  public void showXObject(
    XObject value
    )
  {showXObject(getResourceName(value));}

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].

    @param name Resource identifier of the external object.
    @param location Position at which showing the external object.
  */
  public void showXObject(
    PdfName name,
    Point2D location
    )
  {
    showXObject(
      name,
      location,
      null
      );
  }

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #showXObject(PdfName,Point2D) #showXObject(PdfName,Point2D)}.</p>

    @param value External object.
    @param location Position at which showing the external object.
  */
  public void showXObject(
    XObject value,
    Point2D location
    )
  {
    showXObject(
      getResourceName(value),
      location
      );
  }

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].

    @param name Resource identifier of the external object.
    @param location Position at which showing the external object.
    @param size Size of the external object.
  */
  public void showXObject(
    PdfName name,
    Point2D location,
    Dimension2D size
    )
  {
    showXObject(
      name,
      location,
      size,
      XAlignmentEnum.Left,
      YAlignmentEnum.Top,
      0
      );
  }

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #showXObject(PdfName,Point2D,Dimension2D)
    showXObject(PdfName,Point2D,Dimension2D)}.</p>

    @param value External object.
    @param location Position at which showing the external object.
    @param size Size of the external object.
  */
  public void showXObject(
    XObject value,
    Point2D location,
    Dimension2D size
    )
  {
    showXObject(
      getResourceName(value),
      location,
      size
      );
  }

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].

    @param name Resource identifier of the external object.
    @param location Position at which showing the external object.
    @param size Size of the external object.
    @param xAlignment Horizontal alignment.
    @param yAlignment Vertical alignment.
    @param rotation Rotational counterclockwise angle.
  */
  public void showXObject(
    PdfName name,
    Point2D location,
    Dimension2D size,
    XAlignmentEnum xAlignment,
    YAlignmentEnum yAlignment,
    double rotation
    )
  {
    XObject xObject = scanner.getContentContext().getResources().getXObjects().get(name);
    Dimension2D xObjectSize = xObject.getSize();

    if(size == null)
    {size = xObjectSize;}

    // Scaling.
    AffineTransform matrix = xObject.getMatrix();
    double scaleX, scaleY;
    scaleX = size.getWidth() / (xObjectSize.getWidth() * matrix.getScaleX());
    scaleY = size.getHeight() / (xObjectSize.getHeight() * matrix.getScaleY());

    // Alignment.
    double locationOffsetX, locationOffsetY;
    switch(xAlignment)
    {
      case Left:
        locationOffsetX = 0;
        break;
      case Right:
        locationOffsetX = size.getWidth();
        break;
      case Center:
      case Justify:
      default:
        locationOffsetX = size.getWidth() / 2;
        break;
    }
    switch(yAlignment)
    {
      case Top:
        locationOffsetY = size.getHeight();
        break;
      case Bottom:
        locationOffsetY = 0;
        break;
      case Middle:
      default:
        locationOffsetY = size.getHeight() / 2;
        break;
    }

    beginLocalState();
    try
    {
      translate(
        location.getX(),
        scanner.getContentContext().getBox().getHeight() - location.getY()
        );
      if(rotation != 0)
      {rotate(rotation);}
      applyMatrix(
        scaleX, 0, 0,
        scaleY,
        -locationOffsetX,
        -locationOffsetY
        );
      showXObject(name);
    }
    finally
    {end();} // Ends the local state.
  }

  /**
    Shows the specified external object at the specified position [PDF:1.6:4.7].
    <p>The <code>value</code> is checked for presence in the current resource
    dictionary: if it isn't available, it's automatically added. If you need to
    avoid such a behavior, use {@link #showXObject(PdfName,Point2D,Dimension2D,XAlignmentEnum,
    YAlignmentEnum,double) showXObject(PdfName,...)}.</p>

    @param value External object.
    @param location Position at which showing the external object.
    @param size Size of the external object.
    @param xAlignment Horizontal alignment.
    @param yAlignment Vertical alignment.
    @param rotation Rotational counterclockwise angle.
  */
  public void showXObject(
    XObject value,
    Point2D location,
    Dimension2D size,
    XAlignmentEnum xAlignment,
    YAlignmentEnum yAlignment,
    double rotation
    )
  {
    showXObject(
      getResourceName(value),
      location,
      size,
      xAlignment,
      yAlignment,
      rotation
      );
  }

  /**
    Begins a subpath [PDF:1.6:4.4.1].

    @param startPoint Starting point.
  */
  public void startPath(
    Point2D startPoint
    )
  {
    add(
      new BeginSubpath(
        startPoint.getX(),
        scanner.getContentContext().getBox().getHeight() - startPoint.getY()
        )
      );
  }

  /**
    Strokes the path using the current color [PDF:1.6:4.4.2].

    @see #setStrokeColor(Color)
  */
  public void stroke(
    )
  {add(PaintPath.Stroke);}

  /**
    Applies a translation to the coordinate system from user space
    to device space [PDF:1.6:4.2.2].

    @param distanceX Horizontal distance.
    @param distanceY Vertical distance.
    @see #applyMatrix(double,double,double,double,double,double)
  */
  public void translate(
    double distanceX,
    double distanceY
    )
  {applyMatrix(1, 0, 0, 1, distanceX, distanceY);}
  // </public>

  // <private>
  private void applyState_(
    PdfName name
    )
  {add(new ApplyExtGState(name));}

  private MarkedContent beginMarkedContent_(
    PdfName tag,
    PdfName propertyListName
    )
  {
    return (MarkedContent)begin(
      new MarkedContent(
        new BeginMarkedContent(tag, propertyListName)
        )
      );
  }

  /**
    Begins a text object [PDF:1.6:5.3].

    @see #end()
  */
  private Text beginText(
    )
  {return (Text)begin(new Text());}

  //TODO: drawArc MUST seamlessly manage already-begun paths.
  private void drawArc(
    RectangularShape location,
    double startAngle,
    double endAngle,
    double branchWidth,
    double branchRatio,
    boolean beginPath
    )
  {
    /*
      NOTE: Strictly speaking, arc drawing is NOT a PDF primitive;
      it leverages the cubic bezier curve operator (thanks to
      G. Adam Stanislav, whose article was greatly inspirational:
      see http://www.whizkidtech.redprince.net/bezier/circle/).
    */

    if(startAngle > endAngle)
    {
      double swap = startAngle;
      startAngle = endAngle;
      endAngle = swap;
    }

    double radiusX = location.getWidth() / 2;
    double radiusY = location.getHeight() / 2;

    final Point2D center = new Point2D.Double(
      location.getX() + radiusX,
      location.getY() + radiusY
      );

    double radians1 = Math.toRadians(startAngle);
    Point2D point1 = new Point2D.Double(
      center.getX() + Math.cos(radians1) * radiusX,
      center.getY() - Math.sin(radians1) * radiusY
      );

    if(beginPath)
    {startPath(point1);}

    final double endRadians = Math.toRadians(endAngle);
    final double quadrantRadians = Math.PI / 2;
    double radians2 = Math.min(
      radians1 + quadrantRadians - radians1 % quadrantRadians,
      endRadians
      );
    final double kappa = 0.5522847498;
    while(true)
    {
      double segmentX = radiusX * kappa;
      double segmentY = radiusY * kappa;

      // Endpoint 2.
      Point2D point2 = new Point2D.Double(
        center.getX() + Math.cos(radians2) * radiusX,
        center.getY() - Math.sin(radians2) * radiusY
        );

      // Control point 1.
      double tangentialRadians1 = Math.atan(
        -(Math.pow(radiusY,2) * (point1.getX()-center.getX()))
          / (Math.pow(radiusX,2) * (point1.getY()-center.getY()))
        );
      double segment1 = (
        segmentY * (1 - Math.abs(Math.sin(radians1)))
          + segmentX * (1 - Math.abs(Math.cos(radians1)))
        ) * (radians2-radians1) / quadrantRadians; // TODO: control segment calculation is still not so accurate as it should -- verify how to improve it!!!
      Point2D control1 = new Point2D.Double(
        point1.getX() + Math.abs(Math.cos(tangentialRadians1) * segment1) * Math.signum(-Math.sin(radians1)),
        point1.getY() + Math.abs(Math.sin(tangentialRadians1) * segment1) * Math.signum(-Math.cos(radians1))
        );

      // Control point 2.
      double tangentialRadians2 = Math.atan(
        -(Math.pow(radiusY,2) * (point2.getX()-center.getX()))
          / (Math.pow(radiusX,2) * (point2.getY()-center.getY()))
        );
      double segment2 = (
        segmentY * (1 - Math.abs(Math.sin(radians2)))
          + segmentX * (1 - Math.abs(Math.cos(radians2)))
        ) * (radians2-radians1) / quadrantRadians; // TODO: control segment calculation is still not so accurate as it should -- verify how to improve it!!!
      Point2D control2 = new Point2D.Double(
        point2.getX() + Math.abs(Math.cos(tangentialRadians2) * segment2) * Math.signum(Math.sin(radians2)),
        point2.getY() + Math.abs(Math.sin(tangentialRadians2) * segment2) * Math.signum(Math.cos(radians2))
        );

      // Draw the current quadrant curve!
      drawCurve(
        point2,
        control1,
        control2
        );

      // Last arc quadrant?
      if(radians2 == endRadians)
        break;

      // Preparing the next quadrant iteration...
      point1 = point2;
      radians1 = radians2;
      radians2 += quadrantRadians;
      if(radians2 > endRadians)
      {radians2 = endRadians;}

      double quadrantRatio = (radians2 - radians1) / quadrantRadians;
      radiusX += branchWidth * quadrantRatio;
      radiusY += branchWidth * quadrantRatio;

      branchWidth *= branchRatio;
    }
  }

  private <T extends PdfObjectWrapper<?>> PdfName getResourceName(
    T value
    )
  {
    if(value instanceof DeviceGrayColorSpace)
      return PdfName.DeviceGray;
    else if(value instanceof DeviceRGBColorSpace)
      return PdfName.DeviceRGB;
    else if(value instanceof DeviceCMYKColorSpace)
      return PdfName.DeviceCMYK;
    else
    {
      // Ensuring that the resource exists within the context resources...
      @SuppressWarnings("unchecked")
      ResourceItems<T> resourceItems = (ResourceItems<T>)scanner.getContentContext().getResources().get(value.getClass());
      // Get the key associated to the resource!
      PdfName name = resourceItems.getBaseDataObject().getKey(value.getBaseObject());
      // No key found?
      if(name == null)
      {
        // Insert the resource within the collection!
        int resourceIndex = resourceItems.size();
        do
        {name = new PdfName(String.valueOf(++resourceIndex));}
        while(resourceItems.containsKey(name));
        resourceItems.put(name,value);
      }
      return name;
    }
  }

  /**
    Applies a rotation to the coordinate system from text space to user space [PDF:1.6:4.2.2].

    @param angle Rotational counterclockwise angle.
  */
  @SuppressWarnings("unused")
  private void rotateText(
    double angle
    )
  {
    double rad = angle * Math.PI / 180;
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);

    setTextMatrix(cos, sin, -sin, cos, 0, 0);
  }

  /**
    Applies a scaling to the coordinate system from text space to user space [PDF:1.6:4.2.2].

    @param ratioX Horizontal scaling ratio.
    @param ratioY Vertical scaling ratio.
  */
  @SuppressWarnings("unused")
  private void scaleText(
    double ratioX,
    double ratioY
    )
  {setTextMatrix(ratioX, 0, 0, ratioY, 0, 0);}

  private void setFont_(
    PdfName name,
    double size
    )
  {add(new SetFont(name,size));}

  /**
    Sets the transformation of the coordinate system from text space to user space [PDF:1.6:5.3.1].
    <p>The transformation replaces the current text matrix.</p>

    @param a Item 0,0 of the matrix.
    @param b Item 0,1 of the matrix.
    @param c Item 1,0 of the matrix.
    @param d Item 1,1 of the matrix.
    @param e Item 2,0 of the matrix.
    @param f Item 2,1 of the matrix.
  */
  private void setTextMatrix(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f
    )
  {add(new SetTextMatrix(a,b,c,d,e,f));}

  /**
    Applies a translation to the coordinate system from text space to user space [PDF:1.6:4.2.2].

    @param distanceX Horizontal distance.
    @param distanceY Vertical distance.
  */
  private void translateText(
    double distanceX,
    double distanceY
    )
  {setTextMatrix(1, 0, 0, 1, distanceX, distanceY);}

  /**
    Applies a translation to the coordinate system from text space to user space, relative to the
    start of the current line [PDF:1.6:5.3.1].

    @param offsetX Horizontal offset.
    @param offsetY Vertical offset.
  */
  @SuppressWarnings("unused")
  private void translateTextRelative(
    double offsetX,
    double offsetY
    )
  {
    add(
      new TranslateTextRelative(
        offsetX,
        -offsetY
        )
      );
  }

  /**
    Applies a translation to the coordinate system from text space to user space, moving to the
    start of the next line [PDF:1.6:5.3.1].
  */
  @SuppressWarnings("unused")
  private void translateTextToNextLine(
    )
  {add(TranslateTextToNextLine.Value);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}