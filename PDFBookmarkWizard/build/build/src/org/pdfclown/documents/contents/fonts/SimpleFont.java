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

import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.util.ByteArray;

/**
  Simple font [PDF:1.6:5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.1, 11/14/11
*/
@PDF(VersionEnum.PDF10)
public abstract class SimpleFont
  extends Font
{
  // <constructors>
  protected SimpleFont(
    Document context
    )
  {super(context);}

  protected SimpleFont(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  @Override
  protected PdfDictionary getDescriptor(
    )
  {return (PdfDictionary)getBaseDataObject().resolve(PdfName.FontDescriptor);}

  protected abstract void loadEncoding();

  /**
    Loads the encoding differences into the given collection.

    @param encodingDictionary Encoding dictionary.
    @param codes Encoding to alter applying differences.
   */
  protected void loadEncodingDifferences(
    PdfDictionary encodingDictionary,
    Map<ByteArray,Integer> codes
    )
  {
    PdfArray differenceObjects = (PdfArray)encodingDictionary.resolve(PdfName.Differences);
    if(differenceObjects == null)
      return;

    /*
      NOTE: Each code is the first index in a sequence of character codes to be changed.
      The first character name after the code becomes the name corresponding to that code.
      Subsequent names replace consecutive code indices until the next code appears
      in the array or the array ends.
    */
    byte[] charCodeData = new byte[1];
    for(PdfDirectObject differenceObject : differenceObjects)
    {
      if(differenceObject instanceof PdfInteger)
      {charCodeData[0] = (byte)((((PdfInteger)differenceObject).getValue().intValue()) & 0xFF);} //TODO:verify whether it can be directly cast to byte (.byteValue())!
      else // NOTE: MUST be PdfName.
      {
        ByteArray charCode = new ByteArray(charCodeData);
        String charName = (String)((PdfName)differenceObject).getValue();
        if(charName.equals(".notdef"))
        {codes.remove(charCode);}
        else
        {
          Integer code = GlyphMapping.nameToCode(charName);
          codes.put(charCode, code != null ? code : charCodeData[0]);
        }
        charCodeData[0]++;
      }
    }
  }

  @Override
  protected void onLoad(
    )
  {
    loadEncoding();

    // Glyph widths.
    if(glyphWidths == null)
    {
      glyphWidths = new Hashtable<Integer,Integer>();
      PdfArray glyphWidthObjects = (PdfArray)getBaseDataObject().resolve(PdfName.Widths);
      if(glyphWidthObjects != null)
      {
        ByteArray charCode = new ByteArray(
          new byte[]
          {(byte)((PdfInteger)getBaseDataObject().get(PdfName.FirstChar)).getIntValue()}
          );
        for(PdfDirectObject glyphWidthObject : glyphWidthObjects)
        {
          int glyphWidth = ((PdfNumber<?>)glyphWidthObject).getIntValue();
          if(glyphWidth > 0)
          {
            Integer code = codes.get(charCode);
            if(code != null)
            {
              glyphWidths.put(
                glyphIndexes.get(code),
                glyphWidth
                );
            }
          }
          charCode.data[0]++;
        }
      }
    }
    // Default glyph width.
    {
      PdfDictionary descriptor = getDescriptor();
      if(descriptor != null)
      {
        PdfNumber<?> defaultGlyphWidthObject = (PdfNumber<?>)descriptor.get(PdfName.MissingWidth);
        defaultGlyphWidth = (defaultGlyphWidthObject != null ? defaultGlyphWidthObject.getIntValue() : 0);
      }
    }
  }
}