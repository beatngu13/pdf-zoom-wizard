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
import java.util.Iterator;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.math.Interval;

/**
  Function [PDF:1.6:3.9].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public abstract class Function<TDataObject extends PdfDataObject>
  extends PdfObjectWrapper<TDataObject>
{
  // <class>
  // <classes>
  /**
    Default intervals callback.
  */
  protected interface IDefaultIntervalsCallback<T extends Comparable<T>>
  {List<Interval<T>> invoke(List<Interval<T>> intervals);}
  // </classes>

  // <static>
  // <fields>
  private static final int FunctionType0 = 0;
  private static final int FunctionType2 = 2;
  private static final int FunctionType3 = 3;
  private static final int FunctionType4 = 4;
  // </fields>

  // <interface>
  // <public>
  /**
    Wraps a function base object into a function object.

    @param baseObject Function base object.
    @return Function object associated to the base object.
  */
  public static final Function<?> wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfDataObject dataObject = baseObject.resolve();
    PdfDictionary dictionary = getDictionary(dataObject);
    int functionType = ((PdfInteger)dictionary.get(PdfName.FunctionType)).getRawValue();
    switch(functionType)
    {
      case FunctionType0:
        return new Type0Function(baseObject);
      case FunctionType2:
        return new Type2Function(baseObject);
      case FunctionType3:
        return new Type3Function(baseObject);
      case FunctionType4:
        return new Type4Function(baseObject);
      default:
        throw new UnsupportedOperationException("Function type " + functionType + " unknown.");
    }
  }
  // </public>

  // <private>
  /**
    Gets a function's dictionary.

    @param functionDataObject Function data object.
  */
  private static final PdfDictionary getDictionary(
    PdfDataObject functionDataObject
    )
  {
    if(functionDataObject instanceof PdfDictionary)
      return (PdfDictionary)functionDataObject;
    else // MUST be PdfStream.
      return ((PdfStream)functionDataObject).getHeader();
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected Function(
    Document context,
    TDataObject baseDataObject
    )
  {super(context, baseDataObject);}

  protected Function(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the result of the calculation applied by this function
    to the specified input values.

    @param inputs Input values.
   */
  public abstract double[] calculate(
    double[] inputs
    );

  /**
    Gets the result of the calculation applied by this function
    to the specified input values.

    @param inputs Input values.
   */
  public final List<PdfDirectObject> calculate(
    List<PdfDirectObject> inputs
    )
  {
    List<PdfDirectObject> outputs = new ArrayList<PdfDirectObject>();
    {
      double[] inputValues = new double[inputs.size()];
      for(
        int index = 0,
          length = inputValues.length;
        index < length;
        index++
        )
      {inputValues[index] = ((PdfNumber<?>)inputs.get(index)).getDoubleValue();}
      double[] outputValues = calculate(inputValues);
      for(
        int index = 0,
          length = outputValues.length;
        index < length;
        index++
        )
      {outputs.add(PdfReal.get(outputValues[index]));}
    }
    return outputs;
  }

  /**
    Gets the (inclusive) domains of the input values.
    <p>Input values outside the declared domains are clipped to the nearest boundary value.</p>
  */
  public List<Interval<Double>> getDomains(
    )
  {return getIntervals(PdfName.Domain, null);}

  /**
    Gets the number of input values (parameters) of this function.
  */
  public int getInputCount(
    )
  {return ((PdfArray)getDictionary().get(PdfName.Domain)).size() / 2;}

  /**
    Gets the number of output values (results) of this function.
  */
  public int getOutputCount(
    )
  {
    PdfArray rangesObject = (PdfArray)getDictionary().get(PdfName.Range);
    return rangesObject == null ? 1 : rangesObject.size() / 2;
  }

  /**
    Gets the (inclusive) ranges of the output values.
    <p>Output values outside the declared ranges are clipped to the nearest boundary value;
    if this entry is absent, no clipping is done.</p>

    @return <code>null</code> in case of unbounded ranges.
  */
  public List<Interval<Double>> getRanges(
    )
  {return getIntervals(PdfName.Range, null);}
  // </public>

  // <protected>
  /**
    Gets this function's dictionary.
  */
  protected final PdfDictionary getDictionary(
    )
  {return getDictionary(getBaseDataObject());}

  /**
    Gets the intervals corresponding to the specified key.
  */
  @SuppressWarnings("unchecked")
  protected final <T extends Comparable<T>> List<Interval<T>> getIntervals(
    PdfName key,
    IDefaultIntervalsCallback<T> defaultIntervalsCallback
    )
  {
    List<Interval<T>> intervals;
    {
      PdfArray intervalsObject = (PdfArray)getDictionary().get(key);
      if(intervalsObject == null)
      {
        intervals = (defaultIntervalsCallback == null
          ? null
          : defaultIntervalsCallback.invoke(new ArrayList<Interval<T>>()));
      }
      else
      {
        intervals = new ArrayList<Interval<T>>();
        Iterator<PdfDirectObject> intervalsObjectIterator = intervalsObject.iterator();
        while(intervalsObjectIterator.hasNext())
        {
          intervals.add(
            new Interval<T>(
              (T)((PdfNumber<?>)intervalsObjectIterator.next()).getValue(),
              (T)((PdfNumber<?>)intervalsObjectIterator.next()).getValue()
              )
            );
        }
      }
    }
    return intervals;
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}
