/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Paint;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.NotImplementedException;

/**
  ICC-based color space [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 04/10/11
*/
// TODO:IMPL improve profile support (see ICC.1:2003-09 spec)!!!
@PDF(VersionEnum.PDF13)
public final class ICCBasedColorSpace
  extends ColorSpace<PdfArray>
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  ICCBasedColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ICCBasedColorSpace clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public Color<?> getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {
    return new DeviceRGBColor(components); // FIXME:temporary hack...
  }

  @Override
  public int getComponentCount()
  {
    // FIXME: Auto-generated method stub
    return 0;
  }

  @Override
  public Color<?> getDefaultColor(
    )
  {return DeviceGrayColor.Default;} // FIXME:temporary hack...

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    // FIXME: temporary hack
    return new java.awt.Color(0,0,0);
  }

  public PdfStream getProfile(
    )
  {return (PdfStream)getBaseDataObject().resolve(1);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}