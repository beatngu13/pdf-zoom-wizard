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

package org.pdfclown.documents.contents;

import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;

/**
  Glyph-outlines appearance on text showing [PDF:1.6:5.2.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public enum TextRenderModeEnum
{
  // <class>
  // <static>
  // <fields>
  /**
    Fill text glyphs.
  */
  Fill(0),
  /**
    Stroke text glyphs.
  */
  Stroke(1),
  /**
    Fill, then stroke text glyphs.
  */
  FillStroke(2),
  /**
    Do nothing (invisible text glyphs).
  */
  Invisible(3),
  /**
    Fill text glyphs, then apply to path for clipping.
  */
  FillClip(4),
  /**
    Stroke text glyphs, then apply to path for clipping.
  */
  StrokeClip(5),
  /**
    Fill, then stroke text glyphs, then apply to path for clipping.
  */
  FillStrokeClip(6),
  /**
    Apply text glyphs to path for clipping.
  */
  Clip(7);

  private static Map<Integer, TextRenderModeEnum> map = new HashMap<Integer, TextRenderModeEnum>();
  // </fields>

  // <constructors>
  static
  {
    for (TextRenderModeEnum value : TextRenderModeEnum.values())
    {map.put(value.getCode(), value);}
  }
  // </constructors>

  // <interface>
  // <public>
  public static TextRenderModeEnum valueOf(
    int code
    )
  {return map.get(code);}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  /**
    <h3>Remarks</h3>
    <p>Code MUST be explicitly distinct from the ordinal position of the enum constant
    as they coincide by chance only.</p>
  */
  private final int code;
  // </fields>

  // <constructors>
  private TextRenderModeEnum(
    int code
    )
  {this.code = code;}
  // </constructors>

  // <interface>
  // <public>
  public int getCode(
    )
  {return code;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}