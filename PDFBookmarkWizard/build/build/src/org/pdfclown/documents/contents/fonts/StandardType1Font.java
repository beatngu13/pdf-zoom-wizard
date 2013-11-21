/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.ByteArray;
import org.pdfclown.util.ConvertUtils;

/**
  Standard Type 1 font [PDF:1.6:5.5.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class StandardType1Font
  extends Type1Font
{
  // <class>
  // <classes>
  /**
    Standard Type 1 font families [PDF:1.6:5.5.1].
  */
  public enum FamilyEnum
  {
    Courier(false),
    Helvetica(false),
    Times(false),
    Symbol(true),
    ZapfDingbats(true);

    private boolean symbolic;

    FamilyEnum(
      boolean symbolic
      )
    {this.symbolic = symbolic;}

    public boolean isSymbolic(
      )
    {return symbolic;}
  };
  // </classes>

  // <dynamic>
  // <constructors>
  public StandardType1Font(
    Document context,
    FamilyEnum family,
    boolean bold,
    boolean italic
    )
  {
    super(context);

    String fontName = family.name();
    switch(family)
    {
      case Symbol:
      case ZapfDingbats:
        break;
      case Times:
        if(bold)
        {
          fontName += "-Bold";
          if(italic)
          {fontName += "Italic";}
        }
        else if(italic)
        {fontName += "-Italic";}
        else
        {fontName += "-Roman";}
        break;
      default:
        if(bold)
        {
          fontName += "-Bold";
          if(italic)
          {fontName += "Oblique";}
        }
        else if(italic)
        {fontName += "-Oblique";}
        break;
    }
    PdfName encodingName = (family.isSymbolic() ? null : PdfName.WinAnsiEncoding);

    create(fontName,encodingName);
  }

  StandardType1Font(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public StandardType1Font clone(
    Document context
    )
  {return (StandardType1Font)super.clone(context);}

  @Override
  public double getAscent(
    )
  {return metrics.ascender;}

  @Override
  public double getDescent(
    )
  {return metrics.descender;}

  @Override
  public EnumSet<FlagsEnum> getFlags(
    )
  {
    //TODO:IMPL!!!
    return EnumSet.noneOf(FlagsEnum.class);
  }
  // </public>

  // <protected>
  @Override
  protected Map<ByteArray,Integer> getNativeEncoding(
    )
  {
    if(symbolic) // Symbolic font.
    {
      Map<ByteArray,Integer> codes = new Hashtable<ByteArray,Integer>();
      for(Map.Entry<Integer,Integer> glyphIndexEntry : glyphIndexes.entrySet())
      {
        codes.put(
          new ByteArray(new byte[]{ConvertUtils.intToByteArray(glyphIndexEntry.getValue())[3]}),
          glyphIndexEntry.getKey()
          );
      }
      return codes;
    }
    else // Nonsymbolic font.
      return Encoding.get(PdfName.StandardEncoding).getCodes();
  }

  @Override
  protected void onLoad(
    )
  {
    /*
      NOTE: Standard Type 1 fonts ordinarily omit their descriptor;
      otherwise, when overridden they degrade to a common Type 1 font.
      Metrics of non-overridden Standard Type 1 fonts MUST be loaded from resources.
    */
    load(((PdfName)getBaseDataObject().get(PdfName.BaseFont)).getValue());

    super.onLoad();
  }
  // </protected>

  // <private>
  /**
    Creates the font structures.
  */
  private void create(
    String fontName,
    PdfName encodingName
    )
  {
    /*
      NOTE: Standard Type 1 fonts SHOULD omit extended font descriptions [PDF:1.6:5.5.1].
    */
    // Subtype.
    getBaseDataObject().put(
      PdfName.Subtype,
      PdfName.Type1
      );
    // BaseFont.
    getBaseDataObject().put(
      PdfName.BaseFont,
      new PdfName(fontName)
      );
    // Encoding.
    if(encodingName != null)
    {
      getBaseDataObject().put(
        PdfName.Encoding,
        encodingName
        );
    }

    load();
  }

  /**
    Loads the font metrics.
  */
  private void load(
    String fontName
    )
  {
    BufferedReader fontMetricsStream = null;
    try
    {
      fontMetricsStream = new BufferedReader(
        new InputStreamReader(
          getClass().getResourceAsStream("/fonts/afm/" + fontName + ".afm")
          )
        );

      AfmParser parser = new AfmParser(fontMetricsStream);
      metrics = parser.metrics;
      symbolic = metrics.isCustomEncoding;
      glyphIndexes = parser.glyphIndexes;
      glyphKernings = parser.glyphKernings;
      glyphWidths = parser.glyphWidths;
    }
    catch(Exception e)
    {throw new RuntimeException("Failed to load '" + fontName + "'.",e);}
    finally
    {
      try
      {
        if(fontMetricsStream != null)
        {fontMetricsStream.close();}
      }
      catch(Exception e)
      {/* NOOP */}
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}