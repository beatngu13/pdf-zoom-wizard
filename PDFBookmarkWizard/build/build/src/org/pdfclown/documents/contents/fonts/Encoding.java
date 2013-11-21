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

package org.pdfclown.documents.contents.fonts;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.objects.PdfName;
import org.pdfclown.util.ByteArray;

/**
  Predefined encodings [PDF:1.6:5.5.5,D].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/01/11
*/
// TODO: This hierarchy is going to be superseded by org.pdfclown.tokens.Encoding.
class Encoding
{
  // <static>
  // <fields>
  private static final Map<PdfName,Encoding> Encodings = new HashMap<PdfName,Encoding>();
  // </fields>

  // <constructors>
  static
  {
  //TODO:this collection MUST be automatically populated looking for Encoding subclasses!
    Encodings.put(PdfName.StandardEncoding, new StandardEncoding());
    Encodings.put(PdfName.MacRomanEncoding, new MacRomanEncoding());
    Encodings.put(PdfName.WinAnsiEncoding, new WinAnsiEncoding());
  }
  // </constructors>

  // <interface>
  public static Encoding get(
    PdfName name
    )
  {return Encodings.get(name);}
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final Hashtable<ByteArray,Integer> codes = new Hashtable<ByteArray,Integer>();
  // </fields>

  // <interface>
  // <public>
  public Map<ByteArray,Integer> getCodes(
    )
  {return new Hashtable<ByteArray,Integer>(codes);}
  // </public>

  // <protected>
  protected void put(
    int charCode,
    String charName
    )
  {codes.put(new ByteArray(new byte[]{(byte)charCode}),GlyphMapping.nameToCode(charName));}
  // </protected>
  // </interface>
  // </dynamic>
}