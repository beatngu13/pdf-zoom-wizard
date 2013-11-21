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

package org.pdfclown.documents.interaction.navigation.document;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDirectObject;

/**
  Local interaction target [PDF:1.6:8.2.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class LocalDestination
  extends Destination
{
  // <class>
  // <dynamic>
  // <constructors>
  public LocalDestination(
    Page page
    )
  {
    this(
      page,
      ModeEnum.Fit,
      null,
      null
      );
  }

  public LocalDestination(
    Page page,
    ModeEnum mode,
    Object location,
    Double zoom
    )
  {
    super(
      page.getDocument(),
      page,
      mode,
      location,
      zoom
      );
  }

  LocalDestination(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LocalDestination clone(
    Document context
    )
  {return (LocalDestination)super.clone(context);}

  /**
    Gets the target page.
  */
  @Override
  public Page getPage(
    )
  {return Page.wrap(getBaseDataObject().get(0));}

  @Override
  public void setPage(
    Object value
    )
  {
    if(!(value instanceof Page))
      throw new IllegalArgumentException("It MUST be a Page object.");

    getBaseDataObject().set(0, ((Page)value).getBaseObject());
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}