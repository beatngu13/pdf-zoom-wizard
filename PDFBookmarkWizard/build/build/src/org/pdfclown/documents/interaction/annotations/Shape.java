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

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;

/**
  Abstract shape annotation.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public abstract class Shape
  extends Annotation
{
  // <class>
  // <dynamic>
  // <constructors>
  protected Shape(
    Page page,
    Rectangle2D box,
    String text,
    PdfName subtype
    )
  {super(page, subtype, box, text);}

  protected Shape(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the color with which to fill the interior of the annotation's shape.
  */
  public DeviceRGBColor getFillColor(
    )
  {
    PdfArray fillColorObject = (PdfArray)getBaseDataObject().get(PdfName.IC);
//TODO:use baseObject constructor!!!
    return fillColorObject != null
      ? new DeviceRGBColor(
        ((PdfNumber<?>)fillColorObject.get(0)).getDoubleValue(),
        ((PdfNumber<?>)fillColorObject.get(1)).getDoubleValue(),
        ((PdfNumber<?>)fillColorObject.get(2)).getDoubleValue()
        )
      : null;
  }

  /**
    @see #getFillColor()
  */
  public void setFillColor(
     DeviceRGBColor value
    )
  {getBaseDataObject().put(PdfName.IC, value.getBaseDataObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}