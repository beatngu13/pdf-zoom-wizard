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

package org.pdfclown.documents.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;

/**
  Exponential interpolation of one input value and <code>n</code> output values [PDF:1.6:3.9.2].
  <p>Each input value <code>x</code> will return <code>n</code> values, given by <code>y[j] = C0[j]
  + x^N × (C1[j] − C0[j])</code>, for <code>0 ≤ j < n</code>, where <code>C0</code> and <code>C1</code>
  are the {@link #getBoundOutputValues() function results} when, respectively, <code>x = 0</code> and
  <code>x = 1</code>, and <code>N</code> is the {@link #getExponent() interpolation exponent}.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class Type2Function
  extends Function<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:implement function creation!

  Type2Function(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public double[] calculate(
    double[] inputs
    )
  {
    // FIXME: Auto-generated method stub
    return null;
  }

  @Override
  public Type2Function clone(
    Document context
    )
  {return (Type2Function)super.clone(context);}

  /**
    Gets the output value pairs <code>(C0,C1)</code> for lower (<code>0.0</code>)
    and higher (<code>1.0</code>) input values.
  */
  public List<double[]> getBoundOutputValues(
    )
  {
    List<double[]> outputBounds;
    {
      PdfArray lowOutputBoundsObject = (PdfArray)getDictionary().get(PdfName.C0);
      PdfArray highOutputBoundsObject = (PdfArray)getDictionary().get(PdfName.C1);
      if(lowOutputBoundsObject == null)
      {outputBounds = Arrays.asList(new double[]{0,1});}
      else
      {
        outputBounds = new ArrayList<double[]>();
        Iterator<PdfDirectObject> lowOutputBoundsObjectIterator = lowOutputBoundsObject.iterator();
        Iterator<PdfDirectObject> highOutputBoundsObjectIterator = highOutputBoundsObject.iterator();
        while(lowOutputBoundsObjectIterator.hasNext()
          && highOutputBoundsObjectIterator.hasNext())
        {
          outputBounds.add(
            new double[]
            {
              ((PdfNumber<?>)lowOutputBoundsObjectIterator.next()).getDoubleValue(),
              ((PdfNumber<?>)highOutputBoundsObjectIterator.next()).getDoubleValue()
            }
            );
        }
      }
    }
    return outputBounds;
  }

  /**
    Gets the interpolation exponent.
  */
  public double getExponent(
    )
  {return ((PdfNumber<?>)getDictionary().get(PdfName.N)).getDoubleValue();}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
