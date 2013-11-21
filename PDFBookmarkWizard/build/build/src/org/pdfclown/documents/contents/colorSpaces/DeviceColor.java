/*
  Copyright 2006-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;

/**
  Device color value [PDF:1.6:4.5.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public abstract class DeviceColor
  extends LeveledColor
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets the color corresponding to the specified components.

    @param components Color components to convert.
    @since 0.1.0
   */
  public static DeviceColor get(
    PdfArray components
    )
  {
    if(components == null)
      return null;

    switch(components.size())
    {
      case 1:
        return DeviceGrayColor.get(components);
      case 3:
        return DeviceRGBColor.get(components);
      case 4:
        return DeviceCMYKColor.get(components);
      default:
        return null;
    }
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected DeviceColor(
    DeviceColorSpace colorSpace,
    PdfDirectObject baseObject
    )
  {super(colorSpace, baseObject);}
  // </constructors>
  // </dynamic>
  // </class>
}