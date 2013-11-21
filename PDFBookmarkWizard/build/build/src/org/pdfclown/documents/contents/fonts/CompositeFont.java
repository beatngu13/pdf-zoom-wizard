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

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.objects.Rectangle;
import org.pdfclown.util.BiMap;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;
import org.pdfclown.util.NotImplementedException;

/**
  Composite font, also called Type 0 font [PDF:1.6:5.6].
  <p>Do not confuse it with {@link Type0Font Type 0 CIDFont}: the latter is
  a composite font descendant describing glyphs based on Adobe Type 1 font format.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public abstract class CompositeFont
  extends Font
{
  // <class>
  // <static>
  // <fields>
  private static final String HexPadding = "0000";
  // </fields>

  // <interface>
  // <public>
  public static CompositeFont get(
    Document context,
    IInputStream fontData
    )
  {
    OpenFontParser parser = new OpenFontParser(fontData);
    switch(parser.outlineFormat)
    {
      case CFF:
        return new Type0Font(context,parser);
      case TrueType:
        return new Type2Font(context,parser);
    }
    throw new UnsupportedOperationException("Unknown composite font format.");
  }
  // </public>

  // <private>
  private static String getHex(
    int value
    )
  {
    String hex = Integer.toHexString(value);

    return HexPadding.substring(hex.length()) + hex;
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  // </fields>

  // <constructors>
  protected CompositeFont(
    Document context,
    OpenFontParser parser
    )
  {
    super(context);

    load(parser);
  }

  protected CompositeFont(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  // </public>

  // <protected>
  /**
    Gets the CIDFont dictionary that is the descendant of this composite font.
  */
  protected PdfDictionary getCIDFontDictionary(
    )
  {return (PdfDictionary)((PdfArray)getBaseDataObject().resolve(PdfName.DescendantFonts)).resolve(0);}

  @Override
  protected PdfDictionary getDescriptor(
    )
  {return (PdfDictionary)getCIDFontDictionary().resolve(PdfName.FontDescriptor);}

  protected void loadEncoding(
    )
  {
    PdfDataObject encodingObject = getBaseDataObject().resolve(PdfName.Encoding);

    // CMap [PDF:1.6:5.6.4].
    Map<ByteArray,Integer> cmap = CMap.get(encodingObject);

    // 1. Unicode.
    if(codes == null)
    {
      codes = new BiMap<ByteArray,Integer>();
      if(encodingObject instanceof PdfName
        && !(encodingObject.equals(PdfName.IdentityH)
          || encodingObject.equals(PdfName.IdentityV)))
      {
        /*
          NOTE: According to [PDF:1.6:5.9.1], the fallback method to retrieve
          the character-code-to-Unicode mapping implies getting the UCS2 CMap
          (Unicode value to CID) corresponding to the font's one (character code to CID);
          CIDs are the bridge from character codes to Unicode values.
        */
        BiMap<ByteArray,Integer> ucs2CMap;
        {
          PdfDictionary cidSystemInfo = (PdfDictionary)getCIDFontDictionary().resolve(PdfName.CIDSystemInfo);
          String registry = ((PdfTextString)cidSystemInfo.get(PdfName.Registry)).getValue();
          String ordering = ((PdfTextString)cidSystemInfo.get(PdfName.Ordering)).getValue();
          String ucs2CMapName = registry + "-" + ordering + "-" + "UCS2";
          ucs2CMap = new BiMap<ByteArray,Integer>(CMap.get(ucs2CMapName));
        }
        if(!ucs2CMap.isEmpty())
        {
          for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
          {codes.put(cmapEntry.getKey(),ConvertUtils.byteArrayToInt(ucs2CMap.getKey(cmapEntry.getValue()).data));}
        }
      }
      if(codes.isEmpty())
      {
        /*
          NOTE: In case no clue is available to determine the Unicode resolution map,
          the font is considered symbolic and an identity map is synthesized instead.
         */
        symbolic = true;
        for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
        {codes.put(cmapEntry.getKey(),ConvertUtils.byteArrayToInt(cmapEntry.getKey().data));}
      }
    }

    // 2. Glyph indexes.
    /*
    TODO: gids map for glyph indexes as glyphIndexes is used to map cids!!!
    */
    // Character-code-to-CID mapping [PDF:1.6:5.6.4,5].
    glyphIndexes = new Hashtable<Integer,Integer>();
    for(Map.Entry<ByteArray,Integer> cmapEntry : cmap.entrySet())
    {
      if(!codes.containsKey(cmapEntry.getKey()))
        continue;

      glyphIndexes.put(codes.get(cmapEntry.getKey()),cmapEntry.getValue());
    }
  }

  @Override
  protected void onLoad(
    )
  {
    loadEncoding();

    // Glyph widths.
    {
      glyphWidths = new Hashtable<Integer,Integer>();
      PdfArray glyphWidthObjects = (PdfArray)getCIDFontDictionary().resolve(PdfName.W);
      if(glyphWidthObjects != null)
      {
        for(Iterator<PdfDirectObject> iterator = glyphWidthObjects.iterator(); iterator.hasNext();)
        {
          //TODO: this algorithm is valid only in case cid-to-gid mapping is identity (see cidtogid map)!!
          /*
            NOTE: Font widths are grouped in one of the following formats [PDF:1.6:5.6.3]:
              1. startCID [glyphWidth1 glyphWidth2 ... glyphWidthn]
              2. startCID endCID glyphWidth
          */
          int startCID = ((PdfInteger)iterator.next()).getRawValue();
          PdfDirectObject glyphWidthObject2 = iterator.next();
          if(glyphWidthObject2 instanceof PdfArray) // Format 1: startCID [glyphWidth1 glyphWidth2 ... glyphWidthn].
          {
            int cID = startCID;
            for(PdfDirectObject glyphWidthObject : (PdfArray)glyphWidthObject2)
            {glyphWidths.put(cID++,((PdfInteger)glyphWidthObject).getRawValue());}
          }
          else // Format 2: startCID endCID glyphWidth.
          {
            int endCID = ((PdfInteger)glyphWidthObject2).getRawValue();
            int glyphWidth = ((PdfInteger)iterator.next()).getRawValue();
            for(int cID = startCID; cID <= endCID; cID++)
            {glyphWidths.put(cID,glyphWidth);}
          }
        }
      }
    }
    // Default glyph width.
    {
      PdfInteger defaultGlyphWidthObject = (PdfInteger)getBaseDataObject().get(PdfName.W);
      defaultGlyphWidth = (defaultGlyphWidthObject == null ? 0 : defaultGlyphWidthObject.getRawValue());
    }
  }
  // </protected>

  // <private>
  /**
    Loads the font data.
  */
  private void load(
    OpenFontParser parser
    )
  {
    glyphIndexes = parser.glyphIndexes;
    glyphKernings = parser.glyphKernings;
    glyphWidths = parser.glyphWidths;

    PdfDictionary baseDataObject = getBaseDataObject();

    // BaseFont.
    baseDataObject.put(PdfName.BaseFont,new PdfName(parser.fontName));

    // Subtype.
    baseDataObject.put(PdfName.Subtype, PdfName.Type0);

    // Encoding.
    baseDataObject.put(PdfName.Encoding, PdfName.IdentityH); //TODO: this is a simplification (to refine later).

    // Descendant font.
    PdfDictionary cidFontDictionary = new PdfDictionary(
      new PdfName[]{PdfName.Type},
      new PdfDirectObject[]{PdfName.Font}
      ); // CIDFont dictionary [PDF:1.6:5.6.3].
    {
      // Subtype.
      PdfName subType;
      switch(parser.outlineFormat)
      {
        case TrueType: subType = PdfName.CIDFontType2; break;
        case CFF: subType = PdfName.CIDFontType0; break;
        default: throw new NotImplementedException();
      }
      cidFontDictionary.put(PdfName.Subtype,subType);

      // BaseFont.
      cidFontDictionary.put(
        PdfName.BaseFont,
        new PdfName(parser.fontName)
        );

      // CIDSystemInfo.
      cidFontDictionary.put(
        PdfName.CIDSystemInfo,
        new PdfDictionary(
          new PdfName[]
          {
            PdfName.Registry,
            PdfName.Ordering,
            PdfName.Supplement
          },
          new PdfDirectObject[]
          {
            PdfTextString.get("Adobe"),
            PdfTextString.get("Identity"),
            PdfInteger.get(0)
          }
          )
        ); // Generic predefined CMap (Identity-H/V (Adobe-Identity-0)) [PDF:1.6:5.6.4].

      // FontDescriptor.
      cidFontDictionary.put(
        PdfName.FontDescriptor,
        load_createFontDescriptor(parser)
        );

      // Encoding.
      load_createEncoding(baseDataObject,cidFontDictionary);
    }
    baseDataObject.put(
      PdfName.DescendantFonts,
      new PdfArray(new PdfDirectObject[]{getFile().register(cidFontDictionary)})
      );

    load();
  }

  /**
    Creates the character code mapping for composite fonts.
  */
  private void load_createEncoding(
    PdfDictionary font,
    PdfDictionary cidFont
    )
  {
    // CMap [PDF:1.6:5.6.4].
    Buffer cmapBuffer = new Buffer();
    cmapBuffer.append(
      "%!PS-Adobe-3.0 Resource-CMap\n"
        + "%%DocumentNeededResources: ProcSet (CIDInit)\n"
        + "%%IncludeResource: ProcSet (CIDInit)\n"
        + "%%BeginResource: CMap (Adobe-Identity-UCS)\n"
        + "%%Title: (Adobe-Identity-UCS Adobe Identity 0)\n"
        + "%%Version: 1\n"
        + "%%EndComments\n"
        + "/CIDInit /ProcSet findresource begin\n"
        + "12 dict begin\n"
        + "begincmap\n"
        + "/CIDSystemInfo\n"
        + "3 dict dup begin\n"
        + "/Registry (Adobe) def\n"
        + "/Ordering (Identity) def\n"
        + "/Supplement 0 def\n"
        + "end def\n"
        + "/CMapName /Adobe-Identity-UCS def\n"
        + "/CMapVersion 1 def\n"
        + "/CMapType 0 def\n"
        + "/WMode 0 def\n"
        + "2 begincodespacerange\n"
        + "<20> <20>\n"
        + "<0000> <19FF>\n"
        + "endcodespacerange\n"
        + glyphIndexes.size() + " begincidchar\n"
      );
    // ToUnicode [PDF:1.6:5.9.2].
    Buffer toUnicodeBuffer = new Buffer();
    toUnicodeBuffer.append(
      "/CIDInit /ProcSet findresource begin\n"
        + "12 dict begin\n"
        + "begincmap\n"
        + "/CIDSystemInfo\n"
        + "<< /Registry (Adobe)\n"
        + "/Ordering (UCS)\n"
        + "/Supplement 0\n"
        + ">> def\n"
        + "/CMapName /Adobe-Identity-UCS def\n"
        + "/CMapVersion 10.001 def\n"
        + "/CMapType 2 def\n"
        + "2 begincodespacerange\n"
        + "<20> <20>\n"
        + "<0000> <19FF>\n"
        + "endcodespacerange\n"
        + glyphIndexes.size() + " beginbfchar\n"
      );
    // CIDToGIDMap [PDF:1.6:5.6.3].
    Buffer gIdBuffer = new Buffer();
    gIdBuffer.append((byte)0);
    gIdBuffer.append((byte)0);
    int code = 0;
    codes = new BiMap<ByteArray,Integer>(glyphIndexes.size());
    PdfArray widthsObject = new PdfArray(glyphWidths.size());
    for(Map.Entry<Integer,Integer> glyphIndexEntry : glyphIndexes.entrySet())
    {
      // Character code (codepoint to unicode) entry.
      code++;
      byte[] charCode = (glyphIndexEntry.getKey() == 32
        ? new byte[]{32}
        : new byte[]
          {
            (byte)((code >> 8) & 0xFF),
            (byte)(code & 0xFF)
          });
      codes.put(new ByteArray(charCode),glyphIndexEntry.getKey());

      // CMap entry.
      cmapBuffer.append("<");
      toUnicodeBuffer.append("<");
      for(int charCodeBytesIndex = 0,
          charCodeBytesLength = charCode.length;
        charCodeBytesIndex < charCodeBytesLength;
        charCodeBytesIndex++
        )
      {
        String hex = Integer.toHexString(charCode[charCodeBytesIndex]);
        //TODO:improve hex padding!!!
        if(hex.length() == 1)
        {hex = "0" + hex;}
        else
        {hex = hex.substring(hex.length()-2,hex.length());}
        cmapBuffer.append(hex);
        toUnicodeBuffer.append(hex);
      }
      cmapBuffer.append("> " + code + "\n");
      toUnicodeBuffer.append("> <" + getHex(glyphIndexEntry.getKey()) + ">\n");

      // CID-to-GID entry.
      int glyphIndex = glyphIndexEntry.getValue();
      gIdBuffer.append((byte)((glyphIndex >> 8) & 0xFF));
      gIdBuffer.append((byte)(glyphIndex & 0xFF));

      // Width.
      Integer width = glyphWidths.get(glyphIndex);
      if(width == null)
      {width = 0;}
      else if(width > 1000)
      {width=1000;}
      widthsObject.add(PdfInteger.get(width));
    }
    cmapBuffer.append(
      "endcidchar\n"
        + "endcmap\n"
        + "CMapName currentdict /CMap defineresource pop\n"
        + "end\n"
        + "end\n"
        + "%%EndResource\n"
        + "%%EOF"
      );
    PdfStream cmapStream = new PdfStream(cmapBuffer);
    PdfDictionary cmapHead = cmapStream.getHeader();
    cmapHead.put(
      PdfName.Type,
      PdfName.CMap
      );
    cmapHead.put(
      PdfName.CMapName,
      new PdfName("Adobe-Identity-UCS")
      );
    cmapHead.put(
      PdfName.CIDSystemInfo,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Registry,
          PdfName.Ordering,
          PdfName.Supplement
        },
        new PdfDirectObject[]
        {
          PdfTextString.get("Adobe"),
          PdfTextString.get("Identity"),
          PdfInteger.get(0)
        }
        )
      ); // Generic predefined CMap (Identity-H/V (Adobe-Identity-0)) [PDF:1.6:5.6.4].
    font.put(
      PdfName.Encoding,
      getFile().register(cmapStream)
      );

    PdfStream gIdStream = new PdfStream(gIdBuffer);
    cidFont.put(
      PdfName.CIDToGIDMap,
      getFile().register(gIdStream)
      );

    cidFont.put(
      PdfName.W,
      new PdfArray(new PdfDirectObject[]{PdfInteger.get(1), widthsObject})
      );

    toUnicodeBuffer.append(
      "endbfchar\n"
        + "endcmap\n"
        + "CMapName currentdict /CMap defineresource pop\n"
        + "end\n"
        + "end\n"
      );
    PdfStream toUnicodeStream = new PdfStream(toUnicodeBuffer);
    font.put(
      PdfName.ToUnicode,
      getFile().register(toUnicodeStream)
      );
  }

  /**
    Creates the font descriptor.
  */
  private PdfReference load_createFontDescriptor(
    OpenFontParser parser
    )
  {
    PdfDictionary fontDescriptor = new PdfDictionary();
    {
      OpenFontParser.FontMetrics metrics = parser.metrics;

      // Type.
      fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);

      // FontName.
      fontDescriptor.put(PdfName.FontName, getBaseDataObject().get(PdfName.BaseFont));

      // Flags [PDF:1.6:5.7.1].
      int flags = 0;
      if(metrics.isFixedPitch)
      {flags |= FlagsEnum.FixedPitch.getCode();}
      if(metrics.isCustomEncoding)
      {flags |= FlagsEnum.Symbolic.getCode();}
      else
      {flags |= FlagsEnum.Nonsymbolic.getCode();}
      fontDescriptor.put(PdfName.Flags, PdfInteger.get(flags));

      // FontBBox.
      fontDescriptor.put(
        PdfName.FontBBox,
        new Rectangle(
          new Point2D.Double(metrics.xMin * metrics.unitNorm, metrics.yMin * metrics.unitNorm),
          new Point2D.Double(metrics.xMax * metrics.unitNorm, metrics.yMax * metrics.unitNorm)
          ).getBaseDataObject()
        );

      // ItalicAngle.
      fontDescriptor.put(PdfName.ItalicAngle, PdfReal.get(metrics.italicAngle));

      // Ascent.
      fontDescriptor.put(
        PdfName.Ascent,
        PdfReal.get(
          metrics.ascender == 0
            ? metrics.sTypoAscender * metrics.unitNorm
            : metrics.ascender * metrics.unitNorm
          )
        );

      // Descent.
      fontDescriptor.put(
        PdfName.Descent,
        PdfReal.get(
          metrics.descender == 0
            ? metrics.sTypoDescender * metrics.unitNorm
            : metrics.descender * metrics.unitNorm
          )
        );

      // Leading.
      fontDescriptor.put(
        PdfName.Leading,
        PdfReal.get(metrics.sTypoLineGap * metrics.unitNorm)
        );

      // CapHeight.
      fontDescriptor.put(
        PdfName.CapHeight,
        PdfReal.get(metrics.sCapHeight * metrics.unitNorm)
        );

      // StemV.
      /*
        NOTE: '100' is just a rule-of-thumb value, 'cause I've still to solve the
        'cvt' table puzzle (such a harsh headache!) for TrueType fonts...
        TODO:IMPL TrueType and CFF stemv real value to extract!!!
      */
      fontDescriptor.put(PdfName.StemV, PdfInteger.get(100));

      // FontFile.
      fontDescriptor.put(
        PdfName.FontFile2,
        getFile().register(
          new PdfStream(new Buffer(parser.fontData.toByteArray()))
          )
        );
    }
    return getFile().register(fontDescriptor);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}