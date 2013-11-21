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

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;
import org.pdfclown.util.math.OperationUtils;
import org.pdfclown.util.parsers.PostScriptParser;

/**
  CMap parser [PDF:1.6:5.6.4;CMAP].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 04/25/11
*/
final class CMapParser
  extends PostScriptParser
{
  // <class>
  // <static>
  // <fields>
  private static final String BeginBaseFontCharOperator = "beginbfchar";
  private static final String BeginBaseFontRangeOperator = "beginbfrange";
  private static final String BeginCIDCharOperator = "begincidchar";
  private static final String BeginCIDRangeOperator = "begincidrange";
   // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public CMapParser(
    java.io.BufferedReader stream
    )
  {this(new Buffer(stream));}

  public CMapParser(
    InputStream stream
    )
  {this(new Buffer(stream));}

  public CMapParser(
    IInputStream stream
    )
  {super(stream);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Parses the character-code-to-unicode mapping [PDF:1.6:5.9.1].
  */
  public Map<ByteArray,Integer> parse(
    )
  {
    getStream().setPosition(0);
    Hashtable<ByteArray,Integer> codes = new Hashtable<ByteArray,Integer>();
    {
      int itemCount = 0;
      while(moveNext())
      {
        switch(getTokenType())
        {
          case Keyword:
          {
            String operator = (String)getToken();
            if(operator.equals(BeginBaseFontCharOperator)
              || operator.equals(BeginCIDCharOperator))
            {
              /*
                NOTE: The first element on each line is the input code of the template font;
                the second element is the code or name of the character.
              */
              for(
                int itemIndex = 0;
                itemIndex < itemCount;
                itemIndex++
                )
              {
                moveNext();
                ByteArray inputCode = new ByteArray(parseInputCode());
                moveNext();
                codes.put(inputCode, parseUnicode());
              }
            }
            else if(operator.equals(BeginBaseFontRangeOperator)
              || operator.equals(BeginCIDRangeOperator))
            {
              /*
                NOTE: The first and second elements in each line are the beginning and
                ending valid input codes for the template font; the third element is
                the beginning character code for the range.
              */
              for(
                int itemIndex = 0;
                itemIndex < itemCount;
                itemIndex++
                )
              {
                // 1. Beginning input code.
                moveNext();
                byte[] beginInputCode = parseInputCode();
                // 2. Ending input code.
                moveNext();
                byte[] endInputCode = parseInputCode();
                // 3. Character codes.
                moveNext();
                switch(getTokenType())
                {
                  case ArrayBegin:
                  {
                    byte[] inputCode = beginInputCode;
                    while(moveNext()
                      && getTokenType() != TokenTypeEnum.ArrayEnd)
                    {
                      codes.put(new ByteArray(inputCode), parseUnicode());
                      OperationUtils.increment(inputCode);
                    }
                    break;
                  }
                  default:
                  {
                    byte[] inputCode = beginInputCode;
                    int charCode = parseUnicode();
                    int endCharCode = charCode + (ConvertUtils.byteArrayToInt(endInputCode) - ConvertUtils.byteArrayToInt(beginInputCode));
                    while(true)
                    {
                      codes.put(new ByteArray(inputCode), charCode);
                      if(charCode == endCharCode)
                        break;

                      OperationUtils.increment(inputCode);
                      charCode++;
                    }
                    break;
                  }
                }
              }
            }
            break;
          }
          case Integer:
          {
            itemCount = (Integer)getToken();
            break;
          }
          default:
          {
            /* NOOP */
          }
        }
      }
    }
    return codes;
  }
  // </public>

  // <private>
  /**
    Converts the current token into its input code value.
  */
  private byte[] parseInputCode(
    )
  {return ConvertUtils.hexToByteArray((String)getToken());}

  /**
    Converts the current token into its Unicode value.
  */
  private int parseUnicode(
    )
  {
    switch(getTokenType())
    {
      case Hex: // Character code in hexadecimal format.
        return Integer.parseInt((String)getToken(), 16);
      case Integer: // Character code in plain format.
        return (Integer)getToken();
      case Name: // Character name.
        return GlyphMapping.nameToCode((String)getToken());
      default:
        throw new RuntimeException("Hex string, integer or name expected instead of " + getTokenType());
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}