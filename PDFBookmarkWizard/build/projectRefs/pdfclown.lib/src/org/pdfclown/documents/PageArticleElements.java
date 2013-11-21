/*
  Copyright 2012-2013 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.interaction.navigation.page.ArticleElement;
import org.pdfclown.objects.PdfDirectObject;

/**
  Page article beads [PDF:1.7:3.6.2].
  <p>The beads are listed in natural reading order.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 01/04/13
*/
@PDF(VersionEnum.PDF11)
public final class PageArticleElements
  extends PageElements<ArticleElement>
{
  // <class>
  // <dynamic>
  // <constructors>
  PageArticleElements(
    PdfDirectObject baseObject,
    Page page
    )
  {super(ArticleElement.class, baseObject, page);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PageArticleElements clone(
    Document context
    )
  {return (PageArticleElements)super.clone(context);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
