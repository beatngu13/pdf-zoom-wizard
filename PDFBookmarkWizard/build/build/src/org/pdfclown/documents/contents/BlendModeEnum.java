/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfName;

/**
  Blend mode to be used in the transparent imaging model [PDF:1.7:7.2.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 08/23/12
*/
@PDF(VersionEnum.PDF14)
public enum BlendModeEnum
{
  // <class>
  // <static>
  // <fields>
  /**
    Select the source color, ignoring the backdrop.
  */
  Normal(PdfName.Normal),
  /**
    Multiply the backdrop and source color values.
  */
  Multiply(PdfName.Multiply),
  /**
    Multiply the complements of the backdrop and source color values, then complement the result.
  */
  Screen(PdfName.Screen),
  /**
    Multiply or screen the colors, depending on the backdrop color value.
  */
  Overlay(PdfName.Overlay),
  /**
    Select the darker of the backdrop and source colors.
  */
  Darken(PdfName.Darken),
  /**
    Select the lighter of the backdrop and source colors.
  */
  Lighten(PdfName.Lighten),
  /**
    Brighten the backdrop color to reflect the source color.
  */
  ColorDodge(PdfName.ColorDodge),
  /**
    Darken the backdrop color to reflect the source color.
  */
  ColorBurn(PdfName.ColorBurn),
  /**
    Multiply or screen the colors, depending on the source color value.
  */
  HardLight(PdfName.HardLight),
  /**
    Darken or lighten the colors, depending on the source color value.
  */
  SoftLight(PdfName.SoftLight),
  /**
    Subtract the darker of the two constituent colors from the lighter color.
  */
  Difference(PdfName.Difference),
  /**
    Produce an effect similar to that of the Difference mode but lower in contrast.
  */
  Exclusion(PdfName.Exclusion),
  /**
    Create a color with the hue of the source color and the saturation and luminosity of the
    backdrop color.
  */
  Hue(PdfName.Hue),
  /**
    Create a color with the saturation of the source color and the hue and luminosity of the
    backdrop color.
  */
  Saturation(PdfName.Saturation),
  /**
    Create a color with the hue and saturation of the source color and the luminosity of the
    backdrop color.
  */
  Color(PdfName.Color),
  /**
    Create a color with the luminosity of the source color and the hue and saturation of the
    backdrop color.
  */
  Luminosity(PdfName.Luminosity);
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the blend mode corresponding to the given value.
  */
  public static BlendModeEnum get(
    PdfName value
    )
  {
    for(BlendModeEnum mode : BlendModeEnum.values())
    {
      if(mode.getCode().equals(value))
        return mode;
    }
    return null;
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final PdfName code;
  // </fields>

  // <constructors>
  private BlendModeEnum(
    PdfName code
    )
  {this.code = code;}
  // </constructors>

  // <interface>
  // <public>
  public PdfName getCode(
    )
  {return code;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
