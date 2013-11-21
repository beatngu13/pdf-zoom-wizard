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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
  Adobe standard glyph mapping (unicode-encoding against glyph-naming) [PDF:1.6:D;AGL:2.0].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/01/11
*/
final class GlyphMapping
{
  private static Hashtable<String,Integer> codes = new Hashtable<String,Integer>();

  static
  {load();}

  public static Integer nameToCode(
    String name
    )
  {return codes.get(name);}

  /**
    Loads the glyph list mapping character names to character codes (unicode encoding).
  */
  private static void load(
    )
  {
    BufferedReader glyphListStream = null;
    try
    {
      // Open the glyph list!
      /*
        NOTE: The Adobe Glyph List [AGL:2.0] represents the reference name-to-unicode map
        for consumer applications.
      */
      glyphListStream = new BufferedReader(
        new InputStreamReader(
          GlyphMapping.class.getResourceAsStream("/fonts/AGL20.scsv")
          )
        );

      // Parsing the glyph list...
      String line;
      Pattern linePattern = Pattern.compile("^(\\w+);([A-F0-9]+)$");
      while((line = glyphListStream.readLine()) != null)
      {
        Matcher lineMatcher = linePattern.matcher(line);
        if(!lineMatcher.find())
          continue;

        String name = lineMatcher.group(1);
        int code = Integer.parseInt(lineMatcher.group(2),16);

        // Associate the character name with its corresponding character code!
        codes.put(name,code);
      }
    }
    catch(IOException e)
    {throw new RuntimeException(e);}
    finally
    {
      try
      {
        if(glyphListStream != null)
        {glyphListStream.close();}
      }
      catch(IOException e)
      {throw new RuntimeException(e);}
    }
  }
}