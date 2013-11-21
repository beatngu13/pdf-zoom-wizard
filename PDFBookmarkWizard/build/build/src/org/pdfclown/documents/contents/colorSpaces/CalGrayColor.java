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
  Single-component CIE-based color value [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF11)
public final class CalGrayColor
  extends LeveledColor
{
  // <class>
  // <static>
  // <fields>
  public static final CalGrayColor Black = new CalGrayColor(0);
  public static final CalGrayColor White = new CalGrayColor(1);

  public static final CalGrayColor Default = Black;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public CalGrayColor(
    double g
    )
  {
    this(
      Arrays.asList(
        PdfReal.get(normalizeComponent(g))
        )
      );
  }

  CalGrayColor(
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
    Gets the gray component.
  */
  public double getG(
    )
  {return getComponentValue(0);}

  /**
    @see #getG()
  */
  public void setG(
    double value
    )
  {setComponentValue(0, value);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
