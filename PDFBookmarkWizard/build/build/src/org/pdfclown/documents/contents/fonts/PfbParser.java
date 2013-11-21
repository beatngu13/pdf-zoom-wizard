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

import java.io.EOFException;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pdfclown.bytes.IInputStream;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.parsers.ParseException;

/**
  Type 1 font parser.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 04/25/11
*/
final class PfbParser
{
  private final IInputStream stream;

  PfbParser(
    IInputStream stream
    )
  {this.stream = stream;}

  /**
    Parses the character-code-to-unicode mapping [PDF:1.6:5.9.1].
  */
  public Map<ByteArray,Integer> parse(
    )
  {
    Hashtable<ByteArray,Integer> codes = new Hashtable<ByteArray,Integer>();

    String line;
    Pattern linePattern = Pattern.compile("(\\S+)\\s+(.+)");
    while(true)
    {
      try
      {line = stream.readLine();}
      catch(EOFException e)
      {throw new ParseException("Encoding section not found.", e);}

      Matcher lineMatcher = linePattern.matcher(line);
      if(!lineMatcher.find())
        continue;

      String key = lineMatcher.group(1);
      if(key.equals("/Encoding"))
      {
        // Skip to the encoding array entries!
        try
        {stream.readLine();}
        catch(EOFException e)
        {throw new ParseException(e);}

        String encodingLine;
        Pattern encodingLinePattern = Pattern.compile("dup (\\S+) (\\S+) put");
        while(true)
        {
          try
          {encodingLine = stream.readLine();}
          catch(EOFException e)
          {break;}

          Matcher encodingLineMatcher = encodingLinePattern.matcher(encodingLine);
          if(!encodingLineMatcher.find())
            break;

          byte[] inputCode = new byte[]{(byte)Integer.parseInt(encodingLineMatcher.group(1))};
          String name = encodingLineMatcher.group(2).substring(1);
          codes.put(new ByteArray(inputCode),GlyphMapping.nameToCode(name));
        }
        break;
      }
    }
    return codes;
  }
}