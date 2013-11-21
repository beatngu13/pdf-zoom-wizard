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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.JustificationEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Free text annotation [PDF:1.6:8.4.5].
  <p>It displays text directly on the page. Unlike an ordinary text annotation,
  a free text annotation has no open or closed state;
  instead of being displayed in a pop-up window, the text is always visible.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class CalloutNote
  extends Annotation
{
  // <class>
  // <classes>
  /**
    Callout line [PDF:1.6:8.4.5].
  */
  public static class LineObject
    extends PdfObjectWrapper<PdfArray>
  {
    // <class>
    // <dynamic>
    // <fields>
    private Page page;
    // </fields>

    // <constructors>
    public LineObject(
      Page page,
      Point2D start,
      Point2D end
      )
    {this(page, start, null, end);}

    public LineObject(
      Page page,
      Point2D start,
      Point2D knee,
      Point2D end
      )
    {
      super(page.getDocument(), new PdfArray());
      this.page = page;
      PdfArray baseDataObject = getBaseDataObject();
      {
        double pageHeight = page.getBox().getHeight();
        baseDataObject.add(PdfReal.get(start.getX()));
        baseDataObject.add(PdfReal.get(pageHeight - start.getY()));
        if(knee != null)
        {
          baseDataObject.add(PdfReal.get(knee.getX()));
          baseDataObject.add(PdfReal.get(pageHeight - knee.getY()));
        }
        baseDataObject.add(PdfReal.get(end.getX()));
        baseDataObject.add(PdfReal.get(pageHeight - end.getY()));
      }
    }

    private LineObject(
      PdfDirectObject baseObject
      )
    {super(baseObject);}
    // </constructors>

    // <interface>
    // <public>
    @Override
    public LineObject clone(
      Document context
      )
    {return (LineObject)super.clone(context);}

    public Point2D getEnd(
      )
    {
      PdfArray coordinates = getBaseDataObject();
      if(coordinates.size() < 6)
        return new Point2D.Double(
          ((PdfNumber<?>)coordinates.get(2)).getDoubleValue(),
          page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(3)).getDoubleValue()
          );
      else
        return new Point2D.Double(
          ((PdfNumber<?>)coordinates.get(4)).getDoubleValue(),
          page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(5)).getDoubleValue()
          );
    }

    public Point2D getKnee(
      )
    {
      PdfArray coordinates = getBaseDataObject();
      if(coordinates.size() < 6)
        return null;

      return new Point2D.Double(
        ((PdfNumber<?>)coordinates.get(2)).getDoubleValue(),
        page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(3)).getDoubleValue()
        );
    }

    public Point2D getStart(
      )
    {
      PdfArray coordinates = getBaseDataObject();

      return new Point2D.Double(
        ((PdfNumber<?>)coordinates.get(0)).getDoubleValue(),
        page.getBox().getHeight() - ((PdfNumber<?>)coordinates.get(1)).getDoubleValue()
        );
    }
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public CalloutNote(
    Page page,
    Rectangle2D box,
    String text
    )
  {super(page, PdfName.FreeText, box, text);}

  CalloutNote(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public CalloutNote clone(
    Document context
    )
  {return (CalloutNote)super.clone(context);}

  /**
    Gets the justification to be used in displaying the annotation's text.
  */
  public JustificationEnum getJustification(
    )
  {return JustificationEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.Q));}

  /**
    Gets the callout line attached to the free text annotation.
  */
  public LineObject getLine(
    )
  {
    PdfArray calloutLineObject = (PdfArray)getBaseDataObject().get(PdfName.CL);
    return calloutLineObject != null ? new LineObject(calloutLineObject) : null;
  }

  /**
    @see #getJustification()
  */
  public void setJustification(
    JustificationEnum value
    )
  {getBaseDataObject().put(PdfName.Q, value.getCode());}

  /**
    @see #getLine()
  */
  public void setLine(
    LineObject value
    )
  {getBaseDataObject().put(PdfName.CL,value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}