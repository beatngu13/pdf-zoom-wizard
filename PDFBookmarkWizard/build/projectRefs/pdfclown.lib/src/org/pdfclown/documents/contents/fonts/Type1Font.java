/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;
import org.pdfclown.util.NotImplementedException;

/**
  Type 1 font [PDF:1.6:5.5.1;AFM:4.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
/*
  NOTE: Type 1 fonts encompass several formats:
  * AFM+PFB;
  * CFF;
  * OpenFont/CFF (in case "CFF" table's Top DICT has no CIDFont operators).
*/
@PDF(VersionEnum.PDF10)
public class Type1Font
  extends SimpleFont
{
  // <class>
  // <dynamic>
  // <fields>
  protected AfmParser.FontMetrics metrics;
  // </fields>

  // <constructors>
  Type1Font(
    Document context
    )
  {super(context);}

  Type1Font(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Type1Font clone(
    Document context
    )
  {return (Type1Font)super.clone(context);}
  // </public>

  // <protected>
  protected Map<ByteArray,Integer> getNativeEncoding(
    )
  {
    PdfDictionary descriptor = getDescriptor();
    if(descriptor.containsKey(PdfName.FontFile)) // Embedded noncompact Type 1 font.
    {
      PdfStream fontFileStream = (PdfStream)descriptor.resolve(PdfName.FontFile);
      PfbParser parser = new PfbParser(fontFileStream.getBody());
      return parser.parse();
    }
    else if(descriptor.containsKey(PdfName.FontFile3)) // Embedded compact Type 1 font.
    {
      PdfStream fontFileStream = (PdfStream)descriptor.resolve(PdfName.FontFile3);
      PdfName fontFileSubtype = (PdfName)fontFileStream.getHeader().get(PdfName.Subtype);
      if(fontFileSubtype.equals(PdfName.Type1C)) // CFF.
      {
        CffParser parser = new CffParser(fontFileStream.getBody());
        Map<ByteArray,Integer> codes = new Hashtable<ByteArray,Integer>();
        for(Map.Entry<Integer,Integer> glyphIndexEntry : parser.glyphIndexes.entrySet())
        {
          /*
            FIXME: Custom (non-unicode) encodings require name handling to match encoding differences;
            this method (getNativeEncoding) should therefore return a glyphindex-to-character-name map
            instead.
            Constraining native codes into target byte-arrayed encodings is wrong -- that should be
            only the final stage.
           */
          codes.put(
            new ByteArray(new byte[]{ConvertUtils.intToByteArray(glyphIndexEntry.getValue())[3]}),
            glyphIndexEntry.getKey()
            );
        }
        return codes;
      }
      else if(fontFileSubtype.equals(PdfName.OpenType)) // OpenFont/CFF.
      {throw new NotImplementedException("Embedded OpenFont/CFF font file.");}
      else
      {throw new UnsupportedOperationException("Unsupported embedded font file format: " + fontFileSubtype);}
    }
    else // Non-embedded font.
    {return Encoding.get(PdfName.StandardEncoding).getCodes();}
  }

  @Override
  protected void loadEncoding(
    )
  {//TODO: set symbolic = true/false; depending on the actual encoding!!!
    // Encoding.
    if(codes == null)
    {
      Map<ByteArray,Integer> codes;
      PdfDataObject encodingObject = getBaseDataObject().resolve(PdfName.Encoding);
      if(encodingObject == null) // Native encoding.
      {codes = getNativeEncoding();}
      else if(encodingObject instanceof PdfName) // Predefined encoding.
      {codes = Encoding.get((PdfName)encodingObject).getCodes();}
      else // Custom encoding.
      {
        PdfDictionary encodingDictionary = (PdfDictionary)encodingObject;

        // 1. Base encoding.
        PdfName baseEncodingName = (PdfName)encodingDictionary.get(PdfName.BaseEncoding);
        if(baseEncodingName == null) // Native base encoding.
        {codes = getNativeEncoding();}
        else // Predefined base encoding.
        {codes = Encoding.get(baseEncodingName).getCodes();}

        // 2. Differences.
        loadEncodingDifferences(encodingDictionary, codes);
      }
      this.codes = new BiMap<ByteArray,Integer>(codes);
    }

    // Glyph indexes.
    if(glyphIndexes == null)
    {
      glyphIndexes = new Hashtable<Integer,Integer>();
      for(Map.Entry<ByteArray,Integer> codeEntry : codes.entrySet())
      {glyphIndexes.put(codeEntry.getValue(),ConvertUtils.byteArrayToInt(codeEntry.getKey().data));}
    }
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}