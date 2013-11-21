/*
  Copyright 2007-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.colorSpaces.ColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceCMYKColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceGrayColorSpace;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColorSpace;
import org.pdfclown.documents.contents.colorSpaces.PatternColorSpace;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  'Set the current color space to use for stroking operations' operation [PDF:1.6:4.5.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF11)
public final class SetStrokeColorSpace
  extends Operation
  implements IResourceReference<ColorSpace<?>>
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "CS";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public SetStrokeColorSpace(
    PdfName name
    )
  {super(Operator, name);}

  public SetStrokeColorSpace(
    List<PdfDirectObject> operands
    )
  {super(Operator, operands);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the {@link ColorSpace color space} resource to be set.

    @param context Content context.
  */
  public ColorSpace<?> getColorSpace(
    IContentContext context
    )
  {return getResource(context);}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    // 1. Color space.
    state.setStrokeColorSpace(getColorSpace(state.getScanner().getContentContext()));

    // 2. Initial color.
    /*
      NOTE: The operation also sets the current stroking color
      to its initial value, which depends on the color space [PDF:1.6:4.5.7].
    */
    state.setStrokeColor(state.getStrokeColorSpace().getDefaultColor());
  }

  // <IResourceReference>
  @Override
  public PdfName getName(
    )
  {return (PdfName)operands.get(0);}

  @Override
  public ColorSpace<?> getResource(
    IContentContext context
    )
  {
    /*
      NOTE: The names DeviceGray, DeviceRGB, DeviceCMYK, and Pattern always identify
      the corresponding color spaces directly; they never refer to resources in the
      ColorSpace subdictionary [PDF:1.6:4.5.7].
    */
    PdfName name = getName();
    if(name.equals(PdfName.DeviceGray))
      return DeviceGrayColorSpace.Default;
    else if(name.equals(PdfName.DeviceRGB))
      return DeviceRGBColorSpace.Default;
    else if(name.equals(PdfName.DeviceCMYK))
      return DeviceCMYKColorSpace.Default;
    else if(name.equals(PdfName.Pattern))
      return PatternColorSpace.Default;
    else
      return context.getResources().getColorSpaces().get(name);
  }

  @Override
  public void setName(
    PdfName value
    )
  {operands.set(0,value);}
  // </IResourceReference>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}