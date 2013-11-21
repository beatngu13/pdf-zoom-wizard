/*
  Copyright 2009-2011 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * J. James Jack, Ph.D., Senior Consultant at Symyx Technologies UK Ltd. (original
      C# code developer, james{dot}jack{at}symyx{dot}com)
    * Stefano Chizzolini (source code porting to Java, http://www.stefanochizzolini.it)

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

package org.pdfclown.bytes.filters;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDictionary;

/**
  ASCII base-85 filter [PDF:1.6:3.3.2].

  @author J. James Jack (james{dot}jack{at}symyx{dot}com)
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 04/25/11
*/
@PDF(VersionEnum.PDF10)
public final class ASCII85Filter
  extends Filter
{
  // <class>
  // <types>
  private static final class Params
  {
    int linePos;
    int tuple = 0;
  }
  // </types>

  // <static>
  // <fields>
  /**
    Prefix mark that identifies an encoded ASCII85 string.
  */
  private static final String PrefixMark = "<~";
  /**
    Suffix mark that identifies an encoded ASCII85 string.
  */
  private static final String SuffixMark = "~>";

  /**
    Add the Prefix and Suffix marks when encoding, and enforce their presence for decoding.
  */
  private static final boolean EnforceMarks = true;

  /**
    Maximum line length for encoded ASCII85 string; set to zero for one unbroken line.
  */
  private static final int LineLength = 75;

  private static final int AsciiOffset = 33;

  private static final int[] Pow85 = { 85 * 85 * 85 * 85, 85 * 85 * 85, 85 * 85, 85, 1 };

  private static final String Encoding = "US-ASCII";
  // </fields>

  // <interface>
  // <private>
  private static void appendChar(
    StringBuilder buffer,
    char data,
    Params params
    )
  {
    buffer.append(data);
    params.linePos++;
    if(LineLength > 0
      && params.linePos >= LineLength)
    {
      params.linePos = 0;
      buffer.append('\n');
    }
  }

  private static void appendString(
    StringBuilder buffer,
    String data,
    Params params
    )
  {
    if(LineLength > 0
      && params.linePos + data.length() > LineLength)
    {
      params.linePos = 0;
      buffer.append('\n');
    }
    else
    {params.linePos += data.length();}
    buffer.append(data);
  }

  private static void decodeBlock(
    byte[] decodedBlock,
    Params params
    )
  {decodeBlock(decodedBlock, decodedBlock.length, params);}

  private static void decodeBlock(
    byte[] decodedBlock,
    int count,
    Params params
    )
  {
    for(int i = 0; i < count; i++)
    {decodedBlock[i] = (byte)(params.tuple >> 24 - (i * 8));}
  }

  private static void encodeBlock(
    byte[] encodedBlock,
    StringBuilder buffer,
    Params params
    )
  {encodeBlock(encodedBlock, encodedBlock.length, buffer, params);}

  private static void encodeBlock(
    byte[] encodedBlock,
    int count,
    StringBuilder buffer,
    Params params
    )
  {
    for(int i = encodedBlock.length - 1; i >= 0; i--)
    {
      encodedBlock[i] = (byte)((params.tuple % 85) + AsciiOffset);
      params.tuple /= 85;
    }

    for(int i = 0; i < count; i++)
    {appendChar(buffer, (char)encodedBlock[i], params);}
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  ASCII85Filter(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public byte[] decode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    )
  {
    byte[] decodedBlock = new byte[4];
    byte[] encodedBlock = new byte[5];

    Params params = new Params();

    String dataString;
    try
    {dataString = new String(data,Encoding).trim();}
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException(e);}

    // Stripping prefix and suffix...
    if(dataString.startsWith(PrefixMark))
    {dataString = dataString.substring(PrefixMark.length());}
    if(dataString.endsWith(SuffixMark))
    {dataString = dataString.substring(0, dataString.length() - SuffixMark.length());}

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    int count = 0;
    boolean processChar = false;
    for(char dataChar : dataString.toCharArray())
    {
      switch(dataChar)
      {
        case 'z':
          if(count != 0)
            throw new RuntimeException("The character 'z' is invalid inside an ASCII85 block.");

          decodedBlock[0] = 0;
          decodedBlock[1] = 0;
          decodedBlock[2] = 0;
          decodedBlock[3] = 0;
          stream.write(decodedBlock, 0,decodedBlock.length);
          processChar = false;
          break;
        case '\n':
        case '\r':
        case '\t':
        case '\0':
        case '\f':
        case '\b':
          processChar = false;
          break;
        default:
          if(dataChar < '!' || dataChar > 'u')
            throw new RuntimeException("Bad character '" + dataChar + "' found. ASCII85 only allows characters '!' to 'u'.");

          processChar = true;
          break;
      }

      if(processChar)
      {
        params.tuple += ((dataChar - AsciiOffset) * Pow85[count]);
        count++;
        if(count == encodedBlock.length)
        {
          decodeBlock(decodedBlock, params);
          stream.write(decodedBlock, 0,decodedBlock.length);
          params.tuple = 0;
          count = 0;
        }
      }
    }

    // Bytes left over at the end?
    if(count != 0)
    {
      if(count == 1)
        throw new RuntimeException("The last block of ASCII85 data cannot be a single byte.");

      count--;
      params.tuple += Pow85[count];
      decodeBlock(decodedBlock, count, params);
      for(int i = 0; i < count; i++)
      {stream.write(decodedBlock[i]);}
    }

    return stream.toByteArray();
  }

  @Override
  public byte[] encode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    )
  {
    byte[] decodedBlock = new byte[4];
    byte[] encodedBlock = new byte[5];

    Params params = new Params();

    StringBuilder buffer = new StringBuilder((data.length * (encodedBlock.length / decodedBlock.length)));

    if(EnforceMarks)
    {appendString(buffer, PrefixMark, params);}

    int count = 0;
    for(byte dataByte : data)
    {
      if(count >= decodedBlock.length - 1)
      {
        params.tuple |= dataByte;
        if(params.tuple == 0)
        {appendChar(buffer, 'z', params);}
        else
        {encodeBlock(encodedBlock, buffer, params);}
        params.tuple = 0;
        count = 0;
      }
      else
      {
        params.tuple |= (dataByte << (24 - (count * 8)));
        count++;
      }
    }

    // if we have some bytes left over at the end..
    if(count > 0)
    {encodeBlock(encodedBlock, count + 1, buffer, params);}

    if(EnforceMarks)
    {appendString(buffer, SuffixMark, params);}

    try
    {return buffer.toString().getBytes(Encoding);}
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException(e);}
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}