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
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Selector rendition [PDF:1.7:9.1.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class SelectorRendition
  extends Rendition
{
  // <class>
  // <static>
  // <fields>
  private static final Array.IWrapper<Rendition> ArrayWrapper = new Array.IWrapper<Rendition>()
  {
    @Override
    public Rendition wrap(
      PdfDirectObject baseObject
      )
    {return Rendition.wrap(baseObject);}
  };
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public SelectorRendition(
    Document context
    )
  {super(context, PdfName.SR);}

  SelectorRendition(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public SelectorRendition clone(
    Document context
    )
  {return (SelectorRendition)super.clone(context);}

  /**
    Gets an ordered collection of renditions. The first viable media rendition found in the array,
    or nested within a selector rendition in the array, should be used.
  */
  public Array<Rendition> getRenditions(
    )
  {return Array.wrap(ArrayWrapper, getBaseDataObject().get(PdfName.R, PdfArray.class));}

  /**
    @see #getRenditions()
  */
  public void setRenditions(
    Array<Rendition> value
    )
  {getBaseDataObject().put(PdfName.R, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}