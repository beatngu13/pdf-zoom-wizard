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

package org.pdfclown.documents.contents.objects;

import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  'Set the line dash pattern' operation [PDF:1.6:4.3.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 09/24/12
*/
@PDF(VersionEnum.PDF10)
public final class SetLineDash
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "d";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public SetLineDash(
    LineDash lineDash
    )
  {
    super(Operator, (PdfDirectObject)new PdfArray());
    setValue(lineDash);
  }

  public SetLineDash(
    List<PdfDirectObject> operands
    )
  {super(Operator, operands);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public void scan(
    GraphicsState state
    )
  {state.setLineDash(getValue());}

  public LineDash getValue(
    )
  {
    // 1. Dash array.
    PdfArray baseDashArray = (PdfArray)operands.get(0);
    double[] dashArray = new double[baseDashArray.size()];
    for(
      int index = 0,
        length = dashArray.length;
      index < length;
      index++
      )
    {dashArray[index] = ((PdfNumber<?>)baseDashArray.get(index)).getDoubleValue();}
    // 2. Dash phase.
    double dashPhase = ((PdfNumber<?>)operands.get(1)).getDoubleValue();

    return new LineDash(dashArray, dashPhase);
  }

  public void setValue(
    LineDash value
    )
  {
    operands.clear();
    // 1. Dash array.
    double[] dashArray = value.getDashArray();
    PdfArray baseDashArray = new PdfArray(dashArray.length);
    for(double dashItem : dashArray)
    {baseDashArray.add(PdfReal.get(dashItem));}
    operands.add(baseDashArray);
    // 2. Dash phase.
    operands.add(PdfReal.get(value.getDashPhase()));
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}