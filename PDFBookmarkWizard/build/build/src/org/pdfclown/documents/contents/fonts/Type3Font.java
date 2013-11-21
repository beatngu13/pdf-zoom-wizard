/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;

/**
  Type 3 font [PDF:1.6:5.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Type3Font
  extends SimpleFont
{
  // <class>
  // <dynamic>
  // <constructors>
  Type3Font(
    Document context
    )
  {super(context);}

  Type3Font(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Type3Font clone(
    Document context
    )
  {return (Type3Font)super.clone(context);}

  @Override
  public double getAscent(
    )
  {return 0;}

  @Override
  public double getDescent(
    )
  {return 0;}
  // </public>

  // <protected>
  @Override
  protected void loadEncoding(
    )
  {
    //FIXME: consolidate with Type1Font and TrueTypeFont!
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

  // <private>
  private Map<ByteArray,Integer> getNativeEncoding(
    )
  {
    //FIXME: consolidate with Type1Font and TrueTypeFont!
    return Encoding.get(PdfName.StandardEncoding).getCodes();
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
