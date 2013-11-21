/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction;

import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.objects.PdfInteger;

/**
  Text justification [PDF:1.7:8.4.5,8.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 08/23/12
*/
public enum JustificationEnum
{
  // <class>
  // <static>
  // <fields>
  /**
    Left.
  */
  Left(PdfInteger.get(0)),
  /**
    Center.
  */
  Center(PdfInteger.get(1)),
  /**
    Right.
  */
  Right(PdfInteger.get(2));
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the justification corresponding to the given value.
  */
  public static JustificationEnum valueOf(
    PdfInteger value
    )
  {
    if(value == null)
      return JustificationEnum.Left;

    for(JustificationEnum justification : JustificationEnum.values())
    {
      if(justification.getCode().equals(value))
        return justification;
    }
    throw new UnsupportedOperationException("Justification unknown: " + value);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final PdfInteger code;
  // </fields>

  // <constructors>
  private JustificationEnum(
    PdfInteger code
    )
  {this.code = code;}
  // </constructors>

  // <interface>
  // <public>
  public PdfInteger getCode(
    )
  {return code;}

  public XAlignmentEnum toXAlignment(
    )
  {
    switch(this)
    {
      case Left:
        return XAlignmentEnum.Left;
      case Center:
        return XAlignmentEnum.Center;
      case Right:
        return XAlignmentEnum.Right;
      default:
        throw new UnsupportedOperationException();
    }
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
