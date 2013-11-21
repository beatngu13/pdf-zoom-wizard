/*
  Copyright 2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.objects.PdfName;

/**
  List mode specifying which layers should be displayed to the user.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.1, 06/08/11
*/
public enum ListModeEnum
{
  /**
    All the layers are displayed.
  */
  AllPages(PdfName.AllPages),
  /**
    Only the layers referenced by one or more visible pages are displayed.
  */
  VisiblePages(PdfName.VisiblePages);

  public static ListModeEnum valueOf(
    PdfName name
    )
  {
    if(name == null)
      return ListModeEnum.AllPages;

    for(ListModeEnum value : values())
    {
      if(value.getName().equals(name))
        return value;
    }
    throw new UnsupportedOperationException("List mode unknown: " + name);
  }

  private PdfName name;

  private ListModeEnum(
    PdfName name
    )
  {this.name = name;}

  public PdfName getName(
    )
  {return name;}
}