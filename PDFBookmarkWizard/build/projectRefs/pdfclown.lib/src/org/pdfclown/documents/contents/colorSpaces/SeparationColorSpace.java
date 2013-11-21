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

import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  Special color space that provides a means for specifying the use of additional colorants
  or for isolating the control of individual color components of a device color space for
  a subtractive device [PDF:1.6:4.5.5].
  <p>When such a space is the current color  space, the current color is a single-component value,
  called a <b>tint</b>, that controls the application of the given colorant or color components only.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 11/14/11
*/
@PDF(VersionEnum.PDF12)
public final class SeparationColorSpace
  extends SpecialDeviceColorSpace
{
  // <class>
  // <static>
  // <fields>
  /**
    Special colorant name <i>referring collectively to all components available</i> on an output
    device, including those for the standard process components.
    <p>When a separation space with this component name is the current color space, <i>painting
    operators apply tint values to all available components at once</i>.</p>
  */
  public static final String AllComponentName = (String)PdfName.All.getValue();
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  SeparationColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public SeparationColorSpace clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public SeparationColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new SeparationColor(components);}

  @Override
  public int getComponentCount(
    )
  {return 1;}

  /**
    Gets the name of the colorant that this separation color space is intended
    to represent.
    <p>Special names:</p>
    <ul>
      <li>{@link SeparationColorSpace#AllComponentName All}</li>
      <li>{@link SpecialDeviceColorSpace#NoneComponentName None}</li>
    </ul>
  */
  @Override
  public List<String> getComponentNames(
    )
  {return Arrays.asList((String)((PdfName)getBaseDataObject().get(1)).getValue());}

  @Override
  public SeparationColor getDefaultColor(
    )
  {return SeparationColor.Default;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
