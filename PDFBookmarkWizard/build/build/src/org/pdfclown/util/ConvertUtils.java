/*
  Copyright 2009-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util;

import java.nio.ByteOrder;

/**
  Data convertion utility.
  <h3>Remarks</h3>
  <p>This class is a specialized adaptation from the original <a href="http://commons.apache.org/codec/">
  Apache Commons Codec</a> project, licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">
  Apache License, Version 2.0</a>.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/01/11
*/
public final class ConvertUtils
{
  // <class>
  // <static>
  // <fields>
  private static final char[] HexDigits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
  // </fields>

  // <interface>
  // <public>
  public static String byteArrayToHex(
    byte[] data
    )
  {
    char[] result;
    {
      int dataLength = data.length;
      result = new char[dataLength * 2];
      for(
        int dataIndex = 0,
          resultIndex = 0;
        dataIndex < dataLength;
        dataIndex++
        )
      {
        result[resultIndex++] = HexDigits[(0xF0 & data[dataIndex]) >>> 4];
        result[resultIndex++] = HexDigits[0x0F & data[dataIndex]];
      }
    }
    return new String(result);
  }

  public static int byteArrayToInt(
    byte[] data
    )
  {return byteArrayToInt(data, 0, ByteOrder.BIG_ENDIAN);}

  public static int byteArrayToInt(
    byte[] data,
    int index,
    ByteOrder byteOrder
    ) throws ArrayIndexOutOfBoundsException
  {return byteArrayToNumber(data, index, 4, byteOrder);}

  public static int byteArrayToNumber(
    byte[] data,
    int index,
    int length,
    ByteOrder byteOrder
    ) throws ArrayIndexOutOfBoundsException
  {
    int value;
    {
      length = Math.min(length, data.length - index);
      value = 0;
      for(
        int i = index,
          endIndex = index + length;
        i < endIndex;
        i++
        )
      {value |= (data[i] & 0xff) << 8 * (byteOrder == ByteOrder.LITTLE_ENDIAN ? i-index : endIndex-i-1);}
    }
    return value;
  }

  public static byte[] hexToByteArray(
    String data
    )
  {
    byte[] result;
    {
      char[] dataChars = data.toCharArray();
      int dataLength = dataChars.length;
      if((dataLength % 2) != 0)
        throw new RuntimeException("Odd number of characters.");

      result = new byte[dataLength / 2];
      for(
        int resultIndex = 0,
          dataIndex = 0;
        dataIndex < dataLength;
        resultIndex++
        )
      {
        result[resultIndex] = (byte)((
          toHexDigit(dataChars[dataIndex++]) << 4
            | toHexDigit(dataChars[dataIndex++])
            ) & 0xFF);
      }
    }
    return result;
  }

  public static byte[] intToByteArray(
    int data
    )
  {return new byte[]{(byte)(data >> 24), (byte)(data >> 16), (byte)(data >> 8), (byte)data};}

  public static byte[] numberToByteArray(
    int data,
    int length,
    ByteOrder byteOrder
    )
  {
    byte[] result = new byte[length];
    for(
      int index = 0;
      index < length;
      index++
      )
    {result[index] = (byte)(data >> 8 * (byteOrder == ByteOrder.LITTLE_ENDIAN ? index : length-index-1));}
    return result;
  }

  public static float[] toFloatArray(
    double[] array
    )
  {
    float[] result = new float[array.length];
    for(int index = 0, length = array.length; index < length; index++)
    {result[index] = (float)array[index];}
    return result;
  }
  // </public>

  // <private>
  private static int toHexDigit(
    char dataChar
    )
  {
    int digit = Character.digit(dataChar, 16);
    if(digit == -1)
      throw new RuntimeException("Illegal hexadecimal character " + dataChar);

    return digit;
  }
  // </private>
  // </interface>
  // </static>
  // </class>
}