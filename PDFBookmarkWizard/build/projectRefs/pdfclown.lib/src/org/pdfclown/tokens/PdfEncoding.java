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

package org.pdfclown.tokens;

import java.io.UnsupportedEncodingException;

/**
  PDF serialization encoding [PDF:1.6:3.1].
  <p>PDF can be entirely represented using byte values corresponding to the visible printable subset
  of the ASCII character set, plus white space characters such as space, tab, carriage return, and
  line feed characters. However, a PDF file is not restricted to the ASCII character set; it can
  contain arbitrary 8-bit bytes.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 02/20/12
*/
public final class PdfEncoding
  extends Encoding
{
  // <dynamic>
  // <constructors>
  PdfEncoding(
    )
  {}
  // </constructors>

  // <interface>
  @Override
  public String decode(
    byte[] value
    )
  {
    try
    {return new String(value, CharsetName.ISO88591);}
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public String decode(
    byte[] value,
    int index,
    int length
    )
  {
    try
    {return new String(value, index, length, CharsetName.ISO88591);}
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public byte[] encode(
    String value
    )
  {
    try
    {return value.getBytes(CharsetName.ISO88591);}
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException(e);}
  }
  // </interface>
  // </dynamic>
}
