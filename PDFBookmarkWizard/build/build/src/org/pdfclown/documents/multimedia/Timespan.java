/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.multimedia;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Timespan [PDF:1.7:9.1.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
final class Timespan
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <constructors>
  public Timespan(
    double time
    )
  {
    super(
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.S
        },
        new PdfDirectObject[]
        {
          PdfName.Timespan,
          PdfName.S
        }
        )
      );
    setTime(time);
  }

  Timespan(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Timespan clone(
    Document context
    )
  {return (Timespan)super.clone(context);}

  /**
    Gets the temporal offset (in seconds).
  */
  public double getTime(
    )
  {return ((PdfNumber<?>)getBaseDataObject().get(PdfName.V)).getDoubleValue();}

  /**
    @see #getTime()
  */
  public void setTime(
    double value
    )
  {getBaseDataObject().put(PdfName.V, PdfReal.get(value));}
  // </public>
  // </interface>
  // </class>
}
