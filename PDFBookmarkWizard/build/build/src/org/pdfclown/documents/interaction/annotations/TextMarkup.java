/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.BlendModeEnum;
import org.pdfclown.documents.contents.ExtGState;
import org.pdfclown.documents.contents.ExtGStateResources;
import org.pdfclown.documents.contents.LineCapEnum;
import org.pdfclown.documents.contents.LineJoinEnum;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.geom.Quad;

/**
  Text markup annotation [PDF:1.6:8.4.5].
  <p>It displays highlights, underlines, strikeouts, or jagged ("squiggly") underlines in the text
  of a document.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class TextMarkup
  extends Annotation
{
  // <class>
  // <classes>
  /**
    Markup type [PDF:1.6:8.4.5].
  */
  public enum MarkupTypeEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Highlight.
    */
    @PDF(VersionEnum.PDF13)
    Highlight(PdfName.Highlight),
    /**
      Squiggly.
    */
    @PDF(VersionEnum.PDF14)
    Squiggly(PdfName.Squiggly),
    /**
      StrikeOut.
    */
    @PDF(VersionEnum.PDF13)
    StrikeOut(PdfName.StrikeOut),
    /**
      Underline.
    */
    @PDF(VersionEnum.PDF13)
    Underline(PdfName.Underline);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the markup type corresponding to the given value.
    */
    public static MarkupTypeEnum get(
      PdfName value
      )
    {
      for(MarkupTypeEnum markupType : MarkupTypeEnum.values())
      {
        if(markupType.getCode().equals(value))
          return markupType;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfName code;
    // </fields>

    // <constructors>
    private MarkupTypeEnum(
      PdfName code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfName getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <static>
  // <fields>
  private static final PdfName HighlightExtGStateName = new PdfName("highlight");
  // </fields>

  // <interface>
  private static double getMarkupBoxMargin(
    double boxHeight
    )
  {return boxHeight * .25;}
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new text markup on the specified page, making it printable by default.

    @param page Page to annotate.
    @param text Annotation text.
    @param markupType Markup type.
    @param markupBox Quadrilateral encompassing a word or group of contiguous words in the text
      underlying the annotation.
  */
  public TextMarkup(
    Page page,
    String text,
    MarkupTypeEnum markupType,
    Quad markupBox
    )
  {this(page, text, markupType, Arrays.asList(markupBox));}

  /**
    Creates a new text markup on the specified page, making it printable by default.

    @param page Page to annotate.
    @param text Annotation text.
    @param markupType Markup type.
    @param markupBoxes Quadrilaterals encompassing a word or group of contiguous words in the text
      underlying the annotation.
  */
  public TextMarkup(
    Page page,
    String text,
    MarkupTypeEnum markupType,
    List<Quad> markupBoxes
    )
  {
    super(
      page,
      markupType.getCode(),
      markupBoxes.get(0).getBounds2D(),
      text
      );
    setMarkupType(markupType);
    setMarkupBoxes(markupBoxes);
    setPrintable(true);
  }

  TextMarkup(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public TextMarkup clone(
    Document context
    )
  {return (TextMarkup)super.clone(context);}

  /**
    Gets the quadrilaterals encompassing a word or group of contiguous words in the text underlying
    the annotation.
  */
  public List<Quad> getMarkupBoxes(
    )
  {
    List<Quad> markupBoxes = new ArrayList<Quad>();
    PdfArray quadPointsObject = (PdfArray)getBaseDataObject().get(PdfName.QuadPoints);
    if(quadPointsObject != null)
    {
      double pageHeight = getPage().getBox().getHeight();
      for(
        int index = 0,
          length = quadPointsObject.size();
        index < length;
        index += 8
        )
      {
        /*
          NOTE: Despite the spec prescription, Point 3 and Point 4 MUST be inverted.
        */
        markupBoxes.add(
          new Quad(
            new Point2D.Double(
              ((PdfNumber<?>)quadPointsObject.get(index)).getDoubleValue(),
              pageHeight - ((PdfNumber<?>)quadPointsObject.get(index + 1)).getDoubleValue()
              ),
            new Point2D.Double(
              ((PdfNumber<?>)quadPointsObject.get(index + 2)).getDoubleValue(),
              pageHeight - ((PdfNumber<?>)quadPointsObject.get(index + 3)).getDoubleValue()
              ),
            new Point2D.Double(
              ((PdfNumber<?>)quadPointsObject.get(index + 6)).getDoubleValue(),
              pageHeight - ((PdfNumber<?>)quadPointsObject.get(index + 7)).getDoubleValue()
              ),
            new Point2D.Double(
              ((PdfNumber<?>)quadPointsObject.get(index + 4)).getDoubleValue(),
              pageHeight - ((PdfNumber<?>)quadPointsObject.get(index + 5)).getDoubleValue()
              )
            )
          );
      }
    }
    return markupBoxes;
  }

  /**
    Gets the markup type.
  */
  public MarkupTypeEnum getMarkupType(
    )
  {return MarkupTypeEnum.get((PdfName)getBaseDataObject().get(PdfName.Subtype));}

  /**
    @see #getMarkupBoxes()
  */
  public void setMarkupBoxes(
    List<Quad> value
    )
  {
    PdfArray quadPointsObject = new PdfArray();
    double pageHeight = getPage().getBox().getHeight();
    Rectangle2D box = null;
    for(Quad markupBox : value)
    {
      /*
        NOTE: Despite the spec prescription, Point 3 and Point 4 MUST be inverted.
      */
      Point2D[] markupBoxPoints = markupBox.getPoints();
      quadPointsObject.add(PdfReal.get(markupBoxPoints[0].getX())); // x1.
      quadPointsObject.add(PdfReal.get(pageHeight - markupBoxPoints[0].getY())); // y1.
      quadPointsObject.add(PdfReal.get(markupBoxPoints[1].getX())); // x2.
      quadPointsObject.add(PdfReal.get(pageHeight - markupBoxPoints[1].getY())); // y2.
      quadPointsObject.add(PdfReal.get(markupBoxPoints[3].getX())); // x4.
      quadPointsObject.add(PdfReal.get(pageHeight - markupBoxPoints[3].getY())); // y4.
      quadPointsObject.add(PdfReal.get(markupBoxPoints[2].getX())); // x3.
      quadPointsObject.add(PdfReal.get(pageHeight - markupBoxPoints[2].getY())); // y3.
      if(box == null)
      {box = markupBox.getBounds2D();}
      else
      {box.add(markupBox.getBounds2D());}
    }
    getBaseDataObject().put(PdfName.QuadPoints, quadPointsObject);

    /*
      NOTE: Box width is expanded to make room for end decorations (e.g. rounded highlight caps).
    */
    double markupBoxMargin = getMarkupBoxMargin(box.getHeight());
    box.setRect(box.getX() - markupBoxMargin, box.getY(), box.getWidth() + markupBoxMargin * 2, box.getHeight());
    setBox(box);

    refreshAppearance();
  }

  /**
    @see #getMarkupType()
  */
  public void setMarkupType(
    MarkupTypeEnum value
    )
  {
    getBaseDataObject().put(PdfName.Subtype, value.getCode());
    switch(value)
    {
      case Highlight:
        setColor(new DeviceRGBColor(1, 1, 0));
        break;
      case Squiggly:
        setColor(new DeviceRGBColor(1, 0, 0));
        break;
      default:
        setColor(new DeviceRGBColor(0, 0, 0));
        break;
    }
    refreshAppearance();
  }
  // </public>

  // <private>
  /*
    TODO: refresh should happen just before serialization, on document event (e.g. onWrite())
  */
  private void refreshAppearance(
    )
  {
    FormXObject normalAppearance;
    Rectangle2D box = org.pdfclown.objects.Rectangle.wrap(getBaseDataObject().get(PdfName.Rect)).toRectangle2D();
    {
      AppearanceStates normalAppearances = getAppearance().getNormal();
      normalAppearance = normalAppearances.get(null);
      if(normalAppearance != null)
      {
        normalAppearance.setBox(box);
        normalAppearance.getBaseDataObject().getBody().setLength(0);
      }
      else
      {normalAppearances.put(null, normalAppearance = new FormXObject(getDocument(), box));}
    }

    PrimitiveComposer composer = new PrimitiveComposer(normalAppearance);
    {
      double yOffset = box.getHeight() - getPage().getBox().getHeight();
      MarkupTypeEnum markupType = getMarkupType();
      switch(markupType)
      {
        case Highlight:
        {
          ExtGState defaultExtGState;
          {
            ExtGStateResources extGStates = normalAppearance.getResources().getExtGStates();
            defaultExtGState = extGStates.get(HighlightExtGStateName);
            if(defaultExtGState == null)
            {
              if(!extGStates.isEmpty())
              {extGStates.clear();}

              extGStates.put(HighlightExtGStateName, defaultExtGState = new ExtGState(getDocument()));
              defaultExtGState.setAlphaShape(false);
              defaultExtGState.setBlendMode(Arrays.asList(BlendModeEnum.Multiply));
            }
          }

          composer.applyState(defaultExtGState);
          composer.setFillColor(getColor());
          {
            for(Quad markupBox : getMarkupBoxes())
            {
              Point2D[] points = markupBox.getPoints();
              double markupBoxHeight = points[3].getY() - points[0].getY();
              double markupBoxMargin = getMarkupBoxMargin(markupBoxHeight);
              composer.drawCurve(
                new Point2D.Double(points[3].getX(), points[3].getY() + yOffset),
                new Point2D.Double(points[0].getX(), points[0].getY() + yOffset),
                new Point2D.Double(points[3].getX() - markupBoxMargin, points[3].getY() - markupBoxMargin + yOffset),
                new Point2D.Double(points[0].getX() - markupBoxMargin, points[0].getY() + markupBoxMargin + yOffset)
                );
              composer.drawLine(
                new Point2D.Double(points[1].getX(), points[1].getY() + yOffset)
                );
              composer.drawCurve(
                new Point2D.Double(points[2].getX(), points[2].getY() + yOffset),
                new Point2D.Double(points[1].getX() + markupBoxMargin, points[1].getY() + markupBoxMargin + yOffset),
                new Point2D.Double(points[2].getX() + markupBoxMargin, points[2].getY() - markupBoxMargin + yOffset)
                );
              composer.fill();
            }
          }
        }
          break;
        case Squiggly:
        {
          composer.setStrokeColor(getColor());
          composer.setLineCap(LineCapEnum.Round);
          composer.setLineJoin(LineJoinEnum.Round);
          {
            for(Quad markupBox : getMarkupBoxes())
            {
              Point2D[] points = markupBox.getPoints();
              double markupBoxHeight = points[3].getY() - points[0].getY();
              double lineWidth = markupBoxHeight * .02;
              double step = markupBoxHeight * .125;
              double boxXOffset = points[3].getX();
              double boxYOffset = points[3].getY() + yOffset - lineWidth;
              boolean phase = false;
              composer.setLineWidth(lineWidth);
              for(double x = 0, xEnd = points[2].getX() - boxXOffset; x < xEnd || !phase; x += step)
              {
                Point2D point = new Point2D.Double(x + boxXOffset, (phase ? -step : 0) + boxYOffset);
                if(x == 0)
                {composer.startPath(point);}
                else
                {composer.drawLine(point);}
                phase = !phase;
              }
            }
            composer.stroke();
          }
        }
          break;
        case StrikeOut:
        case Underline:
        {
          composer.setStrokeColor(getColor());
          {
            double lineYRatio;
            switch(markupType)
            {
              case StrikeOut:
                lineYRatio = .575;
                break;
              case Underline:
                lineYRatio = .85;
                break;
              default:
                throw new NotImplementedException();
            }
            for(Quad markupBox : getMarkupBoxes())
            {
              Point2D[] points = markupBox.getPoints();
              double markupBoxHeight = points[3].getY() - points[0].getY();
              double boxYOffset = markupBoxHeight * lineYRatio + yOffset;
              composer.setLineWidth(markupBoxHeight * .065);
              composer.drawLine(
                new Point2D.Double(points[3].getX(), points[0].getY() + boxYOffset),
                new Point2D.Double(points[2].getX(), points[1].getY() + boxYOffset)
                );
            }
            composer.stroke();
          }
        }
          break;
        default:
          throw new NotImplementedException();
      }
    }
    composer.flush();
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}