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

package org.pdfclown.documents.contents.colorSpaces;

import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.util.NotImplementedException;

/**
  CIE-based red-green-blue color value [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF11)
public final class CalRGBColor
  extends LeveledColor
{
  // <class>
  // <static>
  // <fields>
  public static final CalRGBColor Black = new CalRGBColor(0,0,0);
  public static final CalRGBColor White = new CalRGBColor(1,1,1);

  public static final CalRGBColor Default = Black;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public CalRGBColor(
    double r,
    double g,
    double b
    )
  {
    this(
      Arrays.asList(
        PdfReal.get(normalizeComponent(r)),
        PdfReal.get(normalizeComponent(g)),
        PdfReal.get(normalizeComponent(b))
        )
      );
  }

  CalRGBColor(
    List<? extends PdfDirectObject> components
    )
  {
    super(
      null,//TODO:colorspace?
      new PdfArray(components)
      );
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Gets the blue component.
  */
  public double getB(
    )
  {return getComponentValue(2);}

  /**
    Gets the green component.
  */
  public double getG(
    )
  {return getComponentValue(1);}

  /**
    Gets the red component.
  */
  public double getR(
    )
  {return getComponentValue(0);}

  /**
    @see #getB()
  */
  public void setB(
    double value
    )
  {setComponentValue(2, value);}

  /**
    @see #getG()
  */
  public void setG(
    double value
    )
  {setComponentValue(1, value);}

  /**
    @see #getR()
  */
  public void setR(
    double value
    )
  {setComponentValue(0, value);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
