/*
  Copyright 2009-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;

/**
  TrueType font [PDF:1.6:5;OFF:2009].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class TrueTypeFont
  extends SimpleFont
{
  // <class>
  // <classes>
  // </classes>

  // <dynamic>
  // <fields>
  // </fields>

  // <constructors>
  TrueTypeFont(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public TrueTypeFont clone(
    Document context
    )
  {return (TrueTypeFont)super.clone(context);}
  // </public>

  // <protected>
  @Override
  protected void loadEncoding(
    )
  {
    OpenFontParser parser;
    {
      PdfDictionary descriptor = getDescriptor();
      if(descriptor.containsKey(PdfName.FontFile2)) // Embedded TrueType font file (without 'glyf' table).
      {
        PdfStream fontFileStream = (PdfStream)descriptor.resolve(PdfName.FontFile2);
        parser = new OpenFontParser(fontFileStream.getBody());
      }
      else if(descriptor.containsKey(PdfName.FontFile3))
      {
        PdfStream fontFileStream = (PdfStream)descriptor.resolve(PdfName.FontFile3);
        PdfName fontFileSubtype = (PdfName)fontFileStream.getHeader().get(PdfName.Subtype);
        if(fontFileSubtype.equals(PdfName.OpenType)) // Embedded OpenFont/TrueType font file (with 'glyf' table).
        {parser = new OpenFontParser(fontFileStream.getBody());}
        else // Unknown.
          throw new UnsupportedOperationException("Unknown embedded font file format: " + fontFileSubtype);
      }
      else
      {parser = null;}
    }
    if(parser != null) // Embedded font file.
    {
      // Glyph indexes.
      glyphIndexes = parser.glyphIndexes;
      if(codes != null
        && parser.metrics.isCustomEncoding)
      {
        /*
          NOTE: In case of symbolic font,
          glyph indices are natively mapped to character codes,
          so they must be remapped to Unicode whenever possible
          (i.e. when ToUnicode stream is available).
        */
        Map<Integer,Integer> unicodeGlyphIndexes = new Hashtable<Integer,Integer>();
        for(Map.Entry<Integer,Integer> glyphIndexEntry : glyphIndexes.entrySet())
        {
          Integer code = codes.get(new ByteArray(new byte[]{(byte)(int)glyphIndexEntry.getKey()}));
          if(code == null)
            continue;

          unicodeGlyphIndexes.put(code,glyphIndexEntry.getValue());
        }
        glyphIndexes = unicodeGlyphIndexes;
      }
    }

    PdfDataObject encodingObject = getBaseDataObject().resolve(PdfName.Encoding);
    EnumSet<FlagsEnum> flags = getFlags();
    if(flags.contains(FlagsEnum.Symbolic)
      || (!flags.contains(FlagsEnum.Nonsymbolic) && encodingObject == null)) // Symbolic.
    {
      symbolic = true;

      if(glyphIndexes == null)
      {
        /*
          NOTE: In case no font file is available, we have to synthesize its metrics
          from existing entries.
        */
        glyphIndexes = new Hashtable<Integer,Integer>();
        PdfArray glyphWidthObjects = (PdfArray)getBaseDataObject().resolve(PdfName.Widths);
        if(glyphWidthObjects != null)
        {
          int code = ((PdfInteger)getBaseDataObject().get(PdfName.FirstChar)).getRawValue();
          for(PdfDirectObject glyphWidthObject : glyphWidthObjects)
          {
            if(((PdfInteger)glyphWidthObject).getRawValue() > 0)
            {glyphIndexes.put(code,code);}

            code++;
          }
        }
      }

      if(codes == null)
      {
        Map<ByteArray,Integer> codes = new HashMap<ByteArray,Integer>();
        for(Map.Entry<Integer,Integer> glyphIndexEntry : glyphIndexes.entrySet())
        {
          if(glyphIndexEntry.getValue() > 0)
          {
            int glyphCharCode = glyphIndexEntry.getKey();
            byte[] charCode = new byte[]{(byte)glyphCharCode};
            codes.put(new ByteArray(charCode),glyphCharCode);
          }
        }
        this.codes = new BiMap<ByteArray,Integer>(codes);
      }
    }
    else // Nonsymbolic.
    {
      symbolic = false;

      if(codes == null)
      {
        Map<ByteArray,Integer> codes;
        if(encodingObject == null) // Default encoding.
        {codes = Encoding.get(PdfName.StandardEncoding).getCodes();}
        else if(encodingObject instanceof PdfName) // Predefined encoding.
        {codes = Encoding.get((PdfName)encodingObject).getCodes();}
        else // Custom encoding.
        {
          PdfDictionary encodingDictionary = (PdfDictionary)encodingObject;

          // 1. Base encoding.
          PdfName baseEncodingName = (PdfName)encodingDictionary.get(PdfName.BaseEncoding);
          if(baseEncodingName == null) // Default base encoding.
          {codes = Encoding.get(PdfName.StandardEncoding).getCodes();}
          else // Predefined base encoding.
          {codes = Encoding.get(baseEncodingName).getCodes();}

          // 2. Differences.
          loadEncodingDifferences(encodingDictionary, codes);
        }
        this.codes = new BiMap<ByteArray,Integer>(codes);
      }

      if(glyphIndexes == null)
      {
        /*
          NOTE: In case no font file is available, we have to synthesize its metrics
          from existing entries.
        */
        glyphIndexes = new Hashtable<Integer,Integer>();
        PdfArray glyphWidthObjects = (PdfArray)getBaseDataObject().resolve(PdfName.Widths);
        if(glyphWidthObjects != null)
        {
          ByteArray charCode = new ByteArray(
            new byte[]
            {(byte)(int)((PdfInteger)getBaseDataObject().get(PdfName.FirstChar)).getRawValue()}
            );
          for(PdfDirectObject glyphWidthObject : glyphWidthObjects)
          {
            if(((PdfInteger)glyphWidthObject).getRawValue() > 0)
            {
              Integer code = codes.get(charCode);
              if(code != null)
              {glyphIndexes.put(code,(int)charCode.data[0]);}
            }
            charCode.data[0]++;
          }
        }
      }
    }
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}