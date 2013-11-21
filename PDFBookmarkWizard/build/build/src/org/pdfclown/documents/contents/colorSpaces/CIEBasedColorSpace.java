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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;

/**
  Abstract CIE-based color space [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/09/11
*/
@PDF(VersionEnum.PDF11)
public abstract class CIEBasedColorSpace
  extends ColorSpace<PdfArray>
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  protected CIEBasedColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the tristimulus value, in the CIE 1931 XYZ space, of the diffuse black point.
  */
  public double[] getBlackPoint(
    )
  {
    PdfArray blackPointObject = (PdfArray)getDictionary().get(PdfName.BlackPoint);
    return (blackPointObject == null
      ? new double[]
        {
          0,
          0,
          0
        }
      : new double[]
        {
          ((PdfNumber<?>)blackPointObject.get(0)).getDoubleValue(),
          ((PdfNumber<?>)blackPointObject.get(1)).getDoubleValue(),
          ((PdfNumber<?>)blackPointObject.get(2)).getDoubleValue()
        });
  }

  /**
    Gets the tristimulus value, in the CIE 1931 XYZ space, of the diffuse white point.
  */
  public double[] getWhitePoint(
    )
  {
    PdfArray whitePointObject = (PdfArray)getDictionary().get(PdfName.WhitePoint);
    return new double[]
      {
        ((PdfNumber<?>)whitePointObject.get(0)).getDoubleValue(),
        ((PdfNumber<?>)whitePointObject.get(1)).getDoubleValue(),
        ((PdfNumber<?>)whitePointObject.get(2)).getDoubleValue()
      };
  }
  // </public>

  // <protected>
  /**
    Gets this color space's dictionary.
  */
  protected final PdfDictionary getDictionary(
    )
  {return (PdfDictionary)getBaseDataObject().get(1);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}