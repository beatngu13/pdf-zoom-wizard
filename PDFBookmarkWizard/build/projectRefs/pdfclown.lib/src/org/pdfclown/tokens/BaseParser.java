/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Date;

import org.pdfclown.bytes.IInputStream;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDate;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.parsers.ParseException;
import org.pdfclown.util.parsers.PostScriptParser;

/**
  Base PDF parser [PDF:1.7:3.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 08/23/12
*/
public class BaseParser
  extends PostScriptParser
{
  // <class>
  // <dynamic>
  // <constructors>
  protected BaseParser(
    IInputStream stream
    )
  {super(stream);}

  protected BaseParser(
    byte[] data
    )
  {super(data);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public boolean moveNext(
    )
  {
    boolean moved;
    while(moved = super.moveNext())
    {
      TokenTypeEnum tokenType = getTokenType();
      if(tokenType == TokenTypeEnum.Comment)
        continue; // Comments are ignored.

      if(tokenType == TokenTypeEnum.Literal)
      {
        String literalToken = (String)getToken();
        if(literalToken.startsWith(Keyword.DatePrefix)) // Date.
        {
          /*
            NOTE: Dates are a weak extension to the PostScript language.
          */
          try
          {setToken(PdfDate.toDate(literalToken));}
          catch(ParseException e)
          {/* NOOP: gently degrade to a common literal. */}
        }
      }
      break;
    }
    return moved;
  }

  /**
    Parses the current PDF object [PDF:1.6:3.2].
  */
  public PdfDataObject parsePdfObject(
    )
  {
    switch(getTokenType())
    {
      case Integer:
        return PdfInteger.get((Integer)getToken());
      case Name:
        return new PdfName((String)getToken(),true);
      case DictionaryBegin:
      {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.setUpdateable(false);
        while(true)
        {
          // Key.
          moveNext(); if(getTokenType() == TokenTypeEnum.DictionaryEnd) break;
          PdfName key = (PdfName)parsePdfObject();
          // Value.
          moveNext();
          PdfDirectObject value = (PdfDirectObject)parsePdfObject();
          // Add the current entry to the dictionary!
          dictionary.put(key,value);
        }
        dictionary.setUpdateable(true);
        return dictionary;
      }
      case ArrayBegin:
      {
        PdfArray array = new PdfArray();
        array.setUpdateable(false);
        while(true)
        {
          // Value.
          moveNext(); if(getTokenType() == TokenTypeEnum.ArrayEnd) break;
          // Add the current item to the array!
          array.add((PdfDirectObject)parsePdfObject());
        }
        array.setUpdateable(true);
        return array;
      }
      case Literal:
        if(getToken() instanceof Date)
          return PdfDate.get((Date)getToken());
        else
          return new PdfTextString(
            Encoding.Pdf.encode((String)getToken())
            );
      case Hex:
        return new PdfTextString(
          (String)getToken(),
          PdfString.SerializationModeEnum.Hex
          );
      case Real:
        return PdfReal.get((Double)getToken());
      case Boolean:
        return PdfBoolean.get((Boolean)getToken());
      case Null:
        return null;
      default:
        throw new UnsupportedOperationException("Unknown type: " + getToken());
    }
  }

  /**
    Parses a PDF object after moving to the given token offset.

    @param offset Number of tokens to skip before reaching the intended one.
    @see #parsePdfObject()
  */
  public PdfDataObject parsePdfObject(
    int offset
    )
  {
    moveNext(offset);
    return parsePdfObject();
  }
  // </public>
  // </dynamic>
  // </class>
}
