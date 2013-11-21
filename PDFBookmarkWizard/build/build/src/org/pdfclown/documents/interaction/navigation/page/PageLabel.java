/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.navigation.page;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfTextString;

/**
  Page label range [PDF:1.7:8.3.1].
  <p>It represents a series of consecutive pages' visual identifiers using the same numbering system.
  </p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class PageLabel
  extends PdfObjectWrapper<PdfDictionary>
{
  // <classes>
  public enum NumberStyleEnum
  {
    /**
      Decimal arabic numerals.
    */
    ArabicNumber(PdfName.D),
    /**
      Upper-case roman numerals.
    */
    UCaseRomanNumber(PdfName.R),
    /**
      Lower-case roman numerals.
    */
    LCaseRomanNumber(PdfName.r),
    /**
      Upper-case letters (A to Z for the first 26 pages, AA to ZZ for the next 26, and so on).
    */
    UCaseLetter(PdfName.A),
    /**
      Lower-case letters (a to z for the first 26 pages, aa to zz for the next 26, and so on).
    */
    LCaseLetter(PdfName.a);

    /**
      Gets the number style corresponding to the given value.
    */
    public static NumberStyleEnum get(
      PdfName value
      )
    {
      for(NumberStyleEnum numberStyle : NumberStyleEnum.values())
      {
        if(numberStyle.getCode().equals(value))
          return numberStyle;
      }
      return null;
    }

    private final PdfName code;

    private NumberStyleEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final int DefaultNumberBase = 1;
  // </fields>

  // <interface>
  /**
    Gets an existing page label range.

    @param baseObject Base object to wrap.
  */
  public static PageLabel wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new PageLabel(baseObject) : null;}
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public PageLabel(
    Document context,
    NumberStyleEnum numberStyle
    )
  {this(context, null, numberStyle, DefaultNumberBase);}

  public PageLabel(
    Document context,
    String prefix,
    NumberStyleEnum numberStyle,
    int numberBase
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.PageLabel}
        )
      );
    setPrefix(prefix);
    setNumberStyle(numberStyle);
    setNumberBase(numberBase);
  }

  private PageLabel(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  @Override
  public PageLabel clone(
    Document context
    )
  {return (PageLabel)super.clone(context);}

  /**
    Gets the value of the numeric suffix for the first page label in this range. Subsequent pages
    are numbered sequentially from this value.
  */
  public int getNumberBase(
    )
  {return (Integer)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.St), DefaultNumberBase);}

  /**
    Gets the numbering style to be used for the numeric suffix of each page label in this range.
    <p>If no style is defined, the numeric suffix isn't displayed at all.</p>
  */
  public NumberStyleEnum getNumberStyle(
    )
  {return NumberStyleEnum.get((PdfName)getBaseDataObject().get(PdfName.S));}

  /**
    Gets the label prefix for page labels in this range.
  */
  public String getPrefix(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.P));}

  /**
    @see #getNumberBase()
  */
  public void setNumberBase(
    int value
    )
  {getBaseDataObject().put(PdfName.St, value <= DefaultNumberBase ? null : PdfInteger.get(value));}

  /**
    @see #getNumberStyle()
  */
  public void setNumberStyle(
    NumberStyleEnum value
    )
  {getBaseDataObject().put(PdfName.S, value != null ? value.getCode() : null);}

  /**
    @see #getPrefix()
  */
  public void setPrefix(
    String value
    )
  {getBaseDataObject().put(PdfName.P, PdfTextString.get(value));}
  // </interface>
  // </dynamic>
}
