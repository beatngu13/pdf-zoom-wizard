/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.objects.PdfDirectObject;

/**
  Font resources collection [PDF:1.6:3.7.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class FontResources
  extends ResourceItems<Font>
{
  // <class>
  // <dynamic>
  // <constructors>
  public FontResources(
    Document context
    )
  {super(context);}

  FontResources(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public FontResources clone(
    Document context
    )
  {return (FontResources)super.clone(context);}
  // </public>

  // <protected>
  @Override
  protected Font wrap(
    PdfDirectObject baseObject
    )
  {return Font.wrap(baseObject);}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}