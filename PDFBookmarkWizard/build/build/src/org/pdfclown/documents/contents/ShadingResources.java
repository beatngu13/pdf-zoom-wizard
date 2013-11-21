/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.Shading;
import org.pdfclown.objects.PdfDirectObject;

/**
  Shading resources collection [PDF:1.6:3.7.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class ShadingResources
  extends ResourceItems<Shading<?>>
{
  // <class>
  // <dynamic>
  // <constructors>
  public ShadingResources(
    Document context
    )
  {super(context);}

  ShadingResources(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ShadingResources clone(
    Document context
    )
  {return (ShadingResources)super.clone(context);}
  // </public>

  // <protected>
  @Override
  protected Shading<?> wrap(
    PdfDirectObject baseObject
    )
  {return Shading.wrap(baseObject);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}
