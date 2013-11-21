/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.colorSpaces;

import java.awt.Paint;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.util.NotImplementedException;

/**
  CIE-based A single-transformation-stage color space, where A represents a calibrated achromatic
  single-component color value [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/09/11
*/
@PDF(VersionEnum.PDF11)
public final class CalGrayColorSpace
  extends CalColorSpace
{
  // <class>
  // <dynamic>
  // <constructors>
  // TODO:IMPL new element constructor!

  CalGrayColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public CalGrayColorSpace clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public CalGrayColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new CalGrayColor(components);}

  @Override
  public int getComponentCount(
    )
  {return 1;}

  @Override
  public CalGrayColor getDefaultColor(
    )
  {return CalGrayColor.Default;}

  @Override
  public double[] getGamma(
    )
  {
    PdfNumber<?> gammaObject = (PdfNumber<?>)getDictionary().get(PdfName.Gamma);

    return (gammaObject == null
      ? new double[]{1}
      : new double[]{gammaObject.getDoubleValue()}
      );
  }

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    // FIXME: temporary hack
    return new java.awt.Color(0,0,0);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}