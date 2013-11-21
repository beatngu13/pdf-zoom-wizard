/*
  Copyright 2008-2013 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.objects.PdfDirectObject;

/**
  Page annotations [PDF:1.6:3.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 01/04/13
*/
@PDF(VersionEnum.PDF10)
public final class PageAnnotations
  extends PageElements<Annotation>
{
  // <class>
  // <dynamic>
  // <constructors>
  PageAnnotations(
    PdfDirectObject baseObject,
    Page page
    )
  {super(Annotation.class, baseObject, page);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PageAnnotations clone(
    Document context
    )
  {return (PageAnnotations)super.clone(context);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}