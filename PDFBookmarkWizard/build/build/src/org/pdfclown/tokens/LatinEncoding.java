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

package org.pdfclown.tokens;

import org.pdfclown.util.BiMap;

/**
  Adobe standard Latin character set [PDF:1.7:D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 02/20/12
*/
public abstract class LatinEncoding
  extends Encoding
{
  // <dynamic>
  // <fields>
  /**
    Code-to-Unicode map.
  */
  protected BiMap<Integer,Character> chars;
  // </fields>

  // <interface>
  @Override
  public String decode(
    byte[] value
    )
  {return decode(value, 0, value.length);}

  @Override
  public String decode(
    byte[] value,
    int index,
    int length
    )
  {
    char[] stringChars = new char[length];
    for(int decodeIndex = index, decodeLength = length + index; decodeIndex < decodeLength; decodeIndex++)
    {stringChars[decodeIndex - index] = chars.get(value[decodeIndex] & 0xff);}
    return new String(stringChars);
  }

  @Override
  public byte[] encode(
    String value
    )
  {
    char[] stringChars = value.toCharArray();
    byte[] stringBytes = new byte[stringChars.length];
    for(int index = 0, length = stringChars.length; index < length; index++)
    {
      Integer code = chars.getKey(stringChars[index]);
      if(code == null)
        return null;

      stringBytes[index] = code.byteValue();
    }
    return stringBytes;
  }
  // </interface>
  // </dynamic>
}
