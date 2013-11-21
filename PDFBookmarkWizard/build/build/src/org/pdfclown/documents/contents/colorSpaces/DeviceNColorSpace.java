/*
  Copyright 2010-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  Special color space that can contain an arbitrary number of color components [PDF:1.6:4.5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF13)
public final class DeviceNColorSpace
  extends SpecialDeviceColorSpace
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  DeviceNColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public DeviceNColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new DeviceNColor(components);}

  @Override
  public DeviceNColor getDefaultColor(
    )
  {
    double[] components = new double[getComponentCount()];
    for(
      int index = 0,
        length = components.length;
      index < length;
      index++
      )
    {components[index] = 1;}

    return new DeviceNColor(components);
  }

  @Override
  public int getComponentCount(
    )
  {return ((PdfArray)getBaseDataObject().get(1)).size();}

  @Override
  public List<String> getComponentNames(
    )
  {
    List<String> componentNames = new ArrayList<String>();
    {
      PdfArray namesObject = (PdfArray)getBaseDataObject().get(1);
      for(PdfDirectObject nameObject : namesObject)
      {componentNames.add((String)((PdfName)nameObject).getValue());}
    }
    return componentNames;
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
