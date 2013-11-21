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

import java.awt.Paint;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.functions.Function;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Special device color space [PDF:1.6:4.5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/14/11
*/
@PDF(VersionEnum.PDF12)
public abstract class SpecialDeviceColorSpace
  extends SpecialColorSpace<PdfArray>
{
  // <class>
  // <static>
  // <fields>
  /**
    Special colorant name <i>never producing any visible output</i>.
    <p>When a color space with this component name is the current color space, <i>painting operators
    have no effect</i>.</p>
  */
  public static final String NoneComponentName = (String)PdfName.None.getValue();
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  protected SpecialDeviceColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the alternate color space used in case any of the {@link #getComponentNames() component names}
    in the color space do not correspond to a component available on the device.
  */
  public ColorSpace<?> getAlternateSpace(
    )
  {return ColorSpace.wrap(getBaseDataObject().get(2));}

  /**
    Gets the names of the color components.
   */
  public abstract List<String> getComponentNames(
    );

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    //TODO:enable!!!
//    List<PdfDirectObject> alternateColorComponents = getTintFunction().calculate(color.getComponents());
//    ColorSpace<?> alternateSpace = getAlternateSpace();
//
//    return alternateSpace.getPaint(
//      alternateSpace.getColor(
//        alternateColorComponents,
//        null
//        )
//      );

    //TODO: remove (temporary hack)!
    return new java.awt.Color(0,0,0);
  }

  /**
    Gets the <i>function to transform a tint value into color component values</i>
    in the {@link #getAlternateSpace() alternate color space}.
  */
  public Function<?> getTintFunction(
    )
  {return Function.wrap(getBaseDataObject().get(3));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
