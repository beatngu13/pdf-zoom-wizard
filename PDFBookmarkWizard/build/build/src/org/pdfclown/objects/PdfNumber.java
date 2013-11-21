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

package org.pdfclown.objects;

/**
  PDF number object [PDF:1.6:3.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/14/11
*/
public abstract class PdfNumber<TValue extends Number>
  extends PdfSimpleObject<TValue>
{
  // <class>
  // <dynamic>
  // <interface>
  // <public>
  @Override
  public final int compareTo(
    PdfDirectObject obj
    )
  {return ((Double)getDoubleValue()).compareTo(((PdfNumber<?>)obj).getDoubleValue());}

  @Override
  public final boolean equals(
    Object object
    )
  {
    return object != null
      && (object instanceof PdfNumber<?>) // NOTE: This condition allows equality across numeric subtypes.
      && ((PdfNumber<?>)object).getRawValue().equals(getRawValue());
  }

  /**
    Gets the double-precision floating-point representation of the value.
  */
  public abstract double getDoubleValue(
    );

  /**
    Gets the floating-point representation of the value.
  */
  public abstract float getFloatValue(
    );

  /**
    Gets the integer representation of the value.
  */
  public abstract int getIntValue(
    );

  @Override
  public Number getValue(
    )
  {return (Number)super.getValue();}

  @Override
  public int hashCode(
    )
  {
    Number value = getValue();
    int intValue = value.intValue();

    return value.doubleValue() == intValue ? intValue : value.hashCode();
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}