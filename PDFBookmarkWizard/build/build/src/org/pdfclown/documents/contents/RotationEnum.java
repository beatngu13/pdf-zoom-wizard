/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.geom.Dimension2D;

import org.pdfclown.objects.PdfInteger;
import org.pdfclown.util.math.geom.Dimension;

/**
  Rotation (clockwise) [PDF:1.6:3.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 08/23/12
*/
public enum RotationEnum
{
  // <class>
  // <static>
  // <fields>
  /**
    Downward (0° clockwise).
  */
  Downward(PdfInteger.get(0)),
  /**
    Leftward (90° clockwise).
  */
  Leftward(PdfInteger.get(90)),
  /**
    Upward (180° clockwise).
  */
  Upward(PdfInteger.get(180)),
  /**
    Rightward (270° clockwise).
  */
  Rightward(PdfInteger.get(270));
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the direction corresponding to the given value.
  */
  public static RotationEnum valueOf(
    PdfInteger value
    )
  {
    if(value == null)
      return RotationEnum.Downward;

    int normalizedValue = (Math.round(value.getRawValue() / 90) % 4) * 90;
    if(normalizedValue < 0)
    {normalizedValue += 360 * Math.ceil(-normalizedValue / 360d);}
    for(RotationEnum rotation : RotationEnum.values())
    {
      if(rotation.getCode().getRawValue().equals(normalizedValue))
        return rotation;
    }
    return null;
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final PdfInteger code;
  // </fields>

  // <constructors>
  private RotationEnum(
    PdfInteger code
    )
  {this.code = code;}
  // </constructors>

  // <interface>
  // <public>
  public PdfInteger getCode(
    )
  {return code;}

  public Dimension2D transform(
    Dimension2D size
    )
  {
    if(getCode().getRawValue() % 180 == 0)
      return new Dimension(size.getWidth(), size.getHeight());
    else
      return new Dimension(size.getHeight(), size.getWidth());
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
