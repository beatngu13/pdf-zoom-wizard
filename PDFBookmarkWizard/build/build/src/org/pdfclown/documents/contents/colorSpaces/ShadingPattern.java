/*
  Copyright 2010-2011 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.contents.ExtGState;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Pattern providing a smooth transition between colors across an area to be painted [PDF:1.6:4.6.3].
  <p>The transition is continuous and independent of the resolution of any particular output device.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 04/10/11
*/
@PDF(VersionEnum.PDF13)
public final class ShadingPattern
  extends Pattern<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  ShadingPattern(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the <b>graphics state parameters</b> to be put into effect temporarily
    while the shading pattern is painted.
    <p>Any parameters that are not so specified are inherited from the graphics state
    that was in effect at the beginning of the content stream in which the pattern
    is defined as a resource.</p>
   */
  public ExtGState getExtGState(
    )
  {return ExtGState.wrap(getBaseDataObject().get(PdfName.ExtGState));}

  /**
    Gets a <b>shading object</b> defining the shading pattern's gradient fill.
  */
  public Shading<?> getShading(
    )
  {return Shading.wrap(getBaseDataObject().get(PdfName.Shading));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
