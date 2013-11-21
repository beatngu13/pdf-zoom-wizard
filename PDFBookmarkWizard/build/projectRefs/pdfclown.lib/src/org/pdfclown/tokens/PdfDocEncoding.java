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
  Encoding for text strings in a PDF document outside the document's content streams [PDF:1.7:D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 02/20/12
*/
public final class PdfDocEncoding
  extends LatinEncoding
{
  // <static>
  // <fields>
  private static final PdfDocEncoding instance = new PdfDocEncoding();
  // </fields>

  // <interface>
  public static PdfDocEncoding get(
    )
  {return instance;}
  // <interface>
  // </static>

  // <dynamic>
  // <constructors>
  private PdfDocEncoding(
    )
  {
    chars = new BiMap<Integer,Character>()
    {
      private static final long serialVersionUID = 1L;

      private boolean isIdentity(
        int code
        )
      {return code < 128 || (code > 160 && code < 256);}

      @Override
      public synchronized Character get(
        Object key
        )
      {
        Integer code = (Integer)key;
        return isIdentity(code) 
          ? (char)code.intValue()
          : super.get(key);
      }

      @Override
      public Integer getKey(
        Character value
        )
      {
        return isIdentity(value) 
          ? (int)value 
          : super.getKey(value);
      }

      @Override
      public synchronized int size(
        )
      {return 256;}
    };
    chars.put(0x80, '\u2022');
    chars.put(0x81, '\u2020');
    chars.put(0x82, '\u2021');
    chars.put(0x84, '\u2014');
    chars.put(0x85, '\u2013');
    chars.put(0x86, '\u0192');
    chars.put(0x87, '\u2044');
    chars.put(0x88, '\u2039');
    chars.put(0x89, '\u203A');
    chars.put(0x8A, '\u2212');
    chars.put(0x8B, '\u2030');
    chars.put(0x8C, '\u201E');
    chars.put(0x8D, '\u201C');
    chars.put(0x8E, '\u201D');
    chars.put(0x8F, '\u2018');
    chars.put(0x90, '\u2019');
    chars.put(0x91, '\u201A');
    chars.put(0x92, '\u2122');
    chars.put(0x93, '\uFB01');
    chars.put(0x94, '\uFB02');
    chars.put(0x95, '\u0141');
    chars.put(0x96, '\u0152');
    chars.put(0x97, '\u0160');
    chars.put(0x98, '\u0178');
    chars.put(0x99, '\u017D');
    chars.put(0x9A, '\u0131');
    chars.put(0x9B, '\u0142');
    chars.put(0x9C, '\u0153');
    chars.put(0x9D, '\u0161');
    chars.put(0x9E, '\u017E');
    chars.put(0x9F, '\u009F');
    chars.put(0xA0, '\u20AC');
  }
  // </constructors>
  // </dynamic>
}
