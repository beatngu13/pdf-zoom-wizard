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
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  Device Gray color space [PDF:1.6:4.5.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF11)
public final class DeviceGrayColorSpace
  extends DeviceColorSpace
{
  // <class>
  // <static>
  // <fields>
  /*
    NOTE: It may be specified directly (i.e. without being defined in the ColorSpace subdictionary
    of the contextual resource dictionary) [PDF:1.6:4.5.7].
  */
  public static final DeviceGrayColorSpace Default = new DeviceGrayColorSpace(PdfName.DeviceGray);
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public DeviceGrayColorSpace(
    Document context
    )
  {super(context, PdfName.DeviceGray);}

  DeviceGrayColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public DeviceGrayColorSpace clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public DeviceGrayColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new DeviceGrayColor(components);}

  @Override
  public int getComponentCount(
    )
  {return 1;}

  @Override
  public DeviceGrayColor getDefaultColor(
    )
  {return DeviceGrayColor.Default;}

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    DeviceGrayColor spaceColor = (DeviceGrayColor)color;
    float g = (float)spaceColor.getG();
    return new java.awt.Color(g, g, g);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}