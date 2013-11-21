/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Border characteristics [PDF:1.6:8.4.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF11)
public final class Border
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Border style [PDF:1.6:8.4.3].
  */
  public enum StyleEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Solid.
    */
    Solid(PdfName.S),
    /**
      Dashed.

      @see #getPattern()
    */
    Dashed(PdfName.D),
    /**
      Beveled.
    */
    Beveled(PdfName.B),
    /**
      Inset.
    */
    Inset(PdfName.I),
    /**
      Underline.
    */
    Underline(PdfName.U);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the style corresponding to the given value.
    */
    public static StyleEnum get(
      PdfName value
      )
    {
      for(StyleEnum style : StyleEnum.values())
      {
        if(style.getCode().equals(value))
          return style;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfName code;
    // </fields>

    // <constructors>
    private StyleEnum(
      PdfName code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfName getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <static>
  // <fields>
  private static final LineDash DefaultLineDash = new LineDash(new double[]{3});
  private static final StyleEnum DefaultStyle = StyleEnum.Solid;
  private static final double DefaultWidth = 1;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public Border(
    Document context,
    double width,
    StyleEnum style,
    LineDash pattern
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.Border}
        )
      );
    setWidth(width);
    setStyle(style);
    setPattern(pattern);
  }

  Border(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Border clone(
    Document context
    )
  {return (Border)super.clone(context);}

  /**
    Gets the dash pattern used in case of dashed border.
  */
  public LineDash getPattern(
    )
  {
    /*
      NOTE: 'D' entry may be undefined.
    */
    PdfArray dashObject = (PdfArray)getBaseDataObject().get(PdfName.D);
    if(dashObject == null)
      return DefaultLineDash;

    double[] dashArray = new double[dashObject.size()];
    for(
      int dashIndex = 0,
        dashLength = dashArray.length;
      dashIndex < dashLength;
      dashIndex++
      )
    {dashArray[dashIndex] = ((PdfNumber<?>)dashObject.get(dashIndex)).getDoubleValue();}

    return new LineDash(dashArray);
  }

  /**
    Gets the border style.
  */
  public StyleEnum getStyle(
    )
  {
    /*
      NOTE: 'S' entry may be undefined.
    */
    PdfName styleObject = (PdfName)getBaseDataObject().get(PdfName.S);
    if(styleObject == null)
      return DefaultStyle;

    return StyleEnum.get(styleObject);
  }

  /**
    Gets the border width in points.
  */
  public double getWidth(
    )
  {
    /*
      NOTE: 'W' entry may be undefined.
    */
    PdfNumber<?> widthObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.W);
    return widthObject == null
      ? DefaultWidth
      : widthObject.getDoubleValue();
  }

  /**
    @see #getPattern()
  */
  public void setPattern(
    LineDash value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.D);}
    else
    {
      PdfArray dashObject = new PdfArray();

      double[] dashArray = value.getDashArray();
      for(
        int dashIndex = 0,
          dashLength = dashArray.length;
        dashIndex < dashLength;
        dashIndex++
        )
      {dashObject.add(PdfReal.get(dashArray[dashIndex]));}

      getBaseDataObject().put(PdfName.D, dashObject);
    }
  }

  /**
    @see #getStyle()
  */
  public void setStyle(
    StyleEnum value
    )
  {
    if(value == null
      || value == DefaultStyle)
    {getBaseDataObject().remove(PdfName.S);}
    else
    {getBaseDataObject().put(PdfName.S, value.getCode());}
  }

  /**
    @see #getWidth()
  */
  public void setWidth(
    double value
    )
  {getBaseDataObject().put(PdfName.W, PdfReal.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}