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

package org.pdfclown.tokens;

/**
  PDF keywords.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 03/17/11
*/
public final class Keyword
{
  /**
    PDF array opening delimiter.
  */
  public static final String BeginArray = "" + Symbol.OpenSquareBracket;
  /**
    PDF comment opening delimiter.
  */
  public static final String BeginComment = "" + Symbol.Percent;
  /**
    PDF dictionary opening delimiter.
  */
  public static final String BeginDictionary = "" + Symbol.OpenAngleBracket + Symbol.OpenAngleBracket;
  /**
    PDF indirect object begin.
  */
  public static final String BeginIndirectObject = "obj";
  /**
    PDF literal string opening delimiter.
  */
  public static final String BeginLiteralString = "" + Symbol.OpenRoundBracket;
  /**
    PDF stream data begin.
  */
  public static final String BeginStream = "stream";
  /**
    PDF file begin.
  */
  public static final String BOF = "%PDF-";
  /**
    PDF date marker.
  */
  public static final String DatePrefix = "D:";
  /**
    PDF array closing delimiter.
  */
  public static final String EndArray = "" + Symbol.CloseSquareBracket;
  /**
    PDF dictionary closing delimiter.
  */
  public static final String EndDictionary = "" + Symbol.CloseAngleBracket + Symbol.CloseAngleBracket;
  /**
    PDF indirect object end.
  */
  public static final String EndIndirectObject = "endobj";
  /**
    PDF literal string closing delimiter.
  */
  public static final String EndLiteralString = "" + Symbol.CloseRoundBracket;
  /**
    PDF stream data end.
  */
  public static final String EndStream = "endstream";
  /**
    PDF file end.
  */
  public static final String EOF = "%%EOF";
  /**
    PDF boolean false.
  */
  public static final String False = "false";
  /**
    PDF free xref entry marker.
  */
  public static final String FreeXrefEntry = "f";
  /**
    PDF in-use xref entry marker.
  */
  public static final String InUseXrefEntry = "n";
  /**
    PDF name marker.
  */
  public static final String NamePrefix = "" + Symbol.Slash;
  /**
    PDF null object.
  */
  public static final String Null = "null";
  /**
    PDF indirect reference marker.
  */
  public static final String Reference = "" + Symbol.CapitalR;
  /**
    PDF xref start offset.
  */
  public static final String StartXRef = "startxref";
  /**
    PDF trailer begin.
  */
  public static final String Trailer = "trailer";
  /**
    PDF boolean true.
  */
  public static final String True = "true";
  /**
    PDF xref begin.
  */
  public static final String XRef = "xref";
}
