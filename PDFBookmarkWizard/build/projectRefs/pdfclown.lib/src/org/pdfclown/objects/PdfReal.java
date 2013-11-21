/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;

/**
  PDF real number object [PDF:1.6:3.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
public final class PdfReal
  extends PdfNumber<Double>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets the object equivalent to the given value.
  */
  public static PdfReal get(
    Number value
    )
  {
    if(value == null)
      return null;

    double doubleValue = value.doubleValue();
    if(Double.isNaN(doubleValue))
      return null;

    return new PdfReal(doubleValue);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public PdfReal(
    double value
    )
  {setRawValue(value);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  @Override
  public double getDoubleValue(
    )
  {return getRawValue();}

  @Override
  public float getFloatValue(
    )
  {return getRawValue().floatValue();}

  @Override
  public int getIntValue(
    )
  {return (int)Math.round(getRawValue());}

  @Override
  public Double getValue(
    )
  {return super.getValue().doubleValue();}

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {stream.write(context.getConfiguration().getRealFormat().format(getRawValue()));}
  // </public>

  // <protected>
  @Override
  protected void setValue(
    Object value
    )
  {super.setValue(((Number)value).doubleValue());}
  // </protected>
  // </interface>
  // </class>
}