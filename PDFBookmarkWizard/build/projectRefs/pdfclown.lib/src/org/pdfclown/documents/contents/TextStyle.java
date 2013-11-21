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

package org.pdfclown.documents.contents;

import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.ColorSpace;
import org.pdfclown.documents.contents.fonts.Font;

/**
  Text style.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/01/11
*/
public final class TextStyle
{
  // <class>
  // <dynamic>
  // <fields>
  private final Color<?> fillColor;
  private final ColorSpace<?> fillColorSpace;
  private final Font font;
  private final double fontSize;
  private final TextRenderModeEnum renderMode;
  private final Color<?> strokeColor;
  private final ColorSpace<?> strokeColorSpace;
  // </fields>

  // <constructors>
  public TextStyle(
    Font font,
    double fontSize,
    TextRenderModeEnum renderMode,
    Color<?> strokeColor,
    ColorSpace<?> strokeColorSpace,
    Color<?> fillColor,
    ColorSpace<?> fillColorSpace
    )
  {
    this.font = font;
    this.fontSize = fontSize;
    this.renderMode = renderMode;
    this.strokeColor = strokeColor;
    this.strokeColorSpace = strokeColorSpace;
    this.fillColor = fillColor;
    this.fillColorSpace = fillColorSpace;
  }
  // </constructors>

  // <interface>
  // <public>
  public Color<?> getFillColor(
    )
  {return fillColor;}

  public ColorSpace<?> getFillColorSpace(
    )
  {return fillColorSpace;}

  public Font getFont(
    )
  {return font;}

  public double getFontSize(
    )
  {return fontSize;}

  public TextRenderModeEnum getRenderMode(
    )
  {return renderMode;}

  public Color<?> getStrokeColor(
    )
  {return strokeColor;}

  public ColorSpace<?> getStrokeColorSpace(
    )
  {return strokeColorSpace;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}