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
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.util.NotImplementedException;

/**
  Remote interaction target [PDF:1.6:8.2.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class RemoteDestination
  extends Destination
{
  // <class>
  // <dynamic>
  // <constructors>
  public RemoteDestination(
    Document context,
    int pageIndex
    )
  {
    this(
      context,
      pageIndex,
      ModeEnum.Fit,
      null,
      null
      );
  }

  public RemoteDestination(
    Document context,
    int pageIndex,
    ModeEnum mode,
    Object location,
    Double zoom
    )
  {
    super(
      context,
      pageIndex,
      mode,
      location,
      zoom
      );
  }

  RemoteDestination(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public RemoteDestination clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Gets the index of the target page.
  */
  @Override
  public Integer getPage(
    )
  {return ((PdfInteger)getBaseDataObject().get(0)).getRawValue();}

  @Override
  public void setPage(
    Object value
    )
  {
    if(!(value instanceof Integer))
      throw new IllegalArgumentException("It MUST be an integer number.");

    getBaseDataObject().set(0, PdfInteger.get((Integer)value));
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}