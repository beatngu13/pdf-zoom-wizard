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

package org.pdfclown.documents.contents.objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfString;

/**
  'Show one or more text strings, allowing individual glyph positioning' operation
  [PDF:1.6:5.3.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class ShowAdjustedText
  extends ShowText
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "TJ";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    @param value Each element can be either a byte array (encoded text) or a number.
      If the element is a byte array (encoded text), this operator shows the text glyphs.
      If it is a number (glyph adjustment), the operator adjusts the next glyph position by that amount.
  */
  public ShowAdjustedText(
    List<Object> value,
    int reserved
    )
  {
    super(Operator);
    setValue(value);
  }

  public ShowAdjustedText(
    List<PdfDirectObject> operands
    )
  {super(Operator,operands);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public byte[] getText(
    )
  {
    ByteArrayOutputStream textStream = new ByteArrayOutputStream();
    for(PdfDirectObject element : ((PdfArray)operands.get(0)))
    {
      if(element instanceof PdfString)
      {
        try
        {textStream.write(((PdfString)element).getRawValue());}
        catch(IOException e)
        {throw new RuntimeException(e);}
      }
    }
    return textStream.toByteArray();
  }

  @Override
  public List<Object> getValue(
    )
  {
    List<Object> value = new ArrayList<Object>();
    for(PdfDirectObject element : ((PdfArray)operands.get(0)))
    {
      value.add(
        ((PdfSimpleObject<?>)element).getRawValue()
        );
    }
    return value;
  }

  @Override
  public void setText(
    byte[] value
    )
  {setValue(Arrays.asList((Object)value));}

  @Override
  public void setValue(
    List<Object> value
    )
  {
    PdfArray elements = new PdfArray();
    operands.set(0,elements);
    boolean textItemExpected = true;
    for(Object valueItem : value)
    {
      PdfDirectObject element;
      if(textItemExpected)
      {element = new PdfString((byte[])valueItem);}
      else
      {element = PdfReal.get((Double)valueItem);}
      elements.add(element);

      textItemExpected = !textItemExpected;
    }
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}