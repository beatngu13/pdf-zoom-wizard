/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.io.UnsupportedEncodingException;

import org.pdfclown.tokens.CharsetName;
import org.pdfclown.tokens.PdfDocEncoding;

/**
  PDF text string object [PDF:1.6:3.8.1].
  <p>Text strings are meaningful only as part of the document hierarchy; they cannot appear within
  content streams. They represent information that is intended to be human-readable.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.6
  @version 0.1.2, 12/21/12
*/
public final class PdfTextString
  extends PdfString
{
  /*
    NOTE: Text strings are string objects encoded in either PdfDocEncoding (superset of the ISO
    Latin 1 encoding [PDF:1.6:D]) or 16-bit big-endian Unicode character encoding (see [UCS:4]).
  */
  // <class>
  // <static>
  // <fields>
  public static final PdfTextString Default = new PdfTextString("");
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the object equivalent to the given value.
  */
  public static PdfTextString get(
    String value
    )
  {return value == null ? null : new PdfTextString(value);}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private boolean unicoded;
  // </fields>

  // <constructors>
  public PdfTextString(
    byte[] rawValue
    )
  {super(rawValue);}

  public PdfTextString(
    String value
    )
  {super(value);}

  public PdfTextString(
    byte[] rawValue,
    SerializationModeEnum serializationMode
    )
  {super(rawValue, serializationMode);}

  public PdfTextString(
    String value,
    SerializationModeEnum serializationMode
    )
  {super(value, serializationMode);}
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
  public String getValue(
    )
  {
    if(getSerializationMode() == SerializationModeEnum.Literal && unicoded)
    {
      try
      {
        byte[] valueBytes = getRawValue();
        return new String(valueBytes, 2, valueBytes.length - 2, CharsetName.UTF16BE);
      }
      catch(UnsupportedEncodingException e)
      {throw new RuntimeException(e);} // NOTE: It should NEVER happen.
    }
    else
      return (String)super.getValue();
  }
  // </public>

  // <protected>
  @Override
  protected void setRawValue(
    byte[] value
    )
  {
    unicoded = (value.length >= 2 && value[0] == (byte)254 && value[1] == (byte)255);
    super.setRawValue(value);
  }

  @Override
  protected void setValue(
    Object value
    )
  {
    switch(getSerializationMode())
    {
      case Literal:
      {
        String literalValue = (String)value;
        byte[] valueBytes = PdfDocEncoding.get().encode(literalValue);
        if(valueBytes == null)
        {
          try
          {
            byte[] valueBaseBytes = literalValue.getBytes(CharsetName.UTF16BE);
            // Prepending UTF marker...
            valueBytes = new byte[valueBaseBytes.length + 2];
            valueBytes[0] = (byte)254; valueBytes[1] = (byte)255;
            System.arraycopy(valueBaseBytes, 0, valueBytes, 2, valueBaseBytes.length);
          }
          catch(UnsupportedEncodingException e)
          {throw new RuntimeException(e);}
        }
        setRawValue(valueBytes);
      }
        break;
      case Hex:
        super.setValue(value);
        break;
    }
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}