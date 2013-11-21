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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.math.Interval;

/**
  Sampled function using a sequence of sample values to provide an approximation for functions whose
  domains and ranges are bounded [PDF:1.6:3.9.1].
  <p>The samples are organized as an m-dimensional table in which each entry has n components.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class Type0Function
  extends Function<PdfStream>
{
  // <class>
  // <classes>
  public enum InterpolationOrderEnum
  {
    /**
      Linear spline interpolation.
    */
    Linear(1),
    /**
      Cubic spline interpolation.
    */
    Cubic(3);

    private static final Map<Integer,InterpolationOrderEnum> values = new HashMap<Integer,InterpolationOrderEnum>();
    static
    {
      for(InterpolationOrderEnum value : InterpolationOrderEnum.values())
      {values.put(value.getCode(), value);}
    }

    public static InterpolationOrderEnum get(
      int code
      )
    {return values.get(code);}

    private int code;

    private InterpolationOrderEnum(
      int code
      )
    {this.code = code;}

    public int getCode(
      )
    {return code;}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  //TODO:implement function creation and sample table management!

  Type0Function(
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
  public Type0Function clone(
    Document context
    )
  {return (Type0Function)super.clone(context);}

  /**
    Gets the linear mapping of input values into the domain of the function's sample table.
  */
  public List<Interval<Integer>> getDomainEncodes(
    )
  {
    return getIntervals(
      PdfName.Encode,
      new IDefaultIntervalsCallback<Integer>()
      {
        @Override
        public List<Interval<Integer>> invoke(
          List<Interval<Integer>> intervals
          )
        {
          for(Integer sampleCount : getSampleCounts())
          {intervals.add(new Interval<Integer>(0, sampleCount-1));}
          return intervals;
        }
      }
      );
  }

  /**
    Gets the order of interpolation between samples.
  */
  public InterpolationOrderEnum getOrder(
    )
  {
    PdfInteger interpolationOrderObject = (PdfInteger)getDictionary().get(PdfName.Order);
    return (interpolationOrderObject == null
      ? InterpolationOrderEnum.Linear
      : InterpolationOrderEnum.get(interpolationOrderObject.getRawValue()));
  }

  /**
    Gets the linear mapping of sample values into the ranges of the function's output values.
  */
  public List<Interval<Double>> getRangeDecodes(
    )
  {return getIntervals(PdfName.Decode, null);}

  /**
    Gets the number of bits used to represent each sample.
  */
  public int getSampleBitsCount(
    )
  {return ((PdfInteger)getDictionary().get(PdfName.BitsPerSample)).getRawValue();}

  /**
    Gets the number of samples in each input dimension of the sample table.
  */
  public List<Integer> getSampleCounts(
    )
  {
    ArrayList<Integer> sampleCounts = new ArrayList<Integer>();
    {
      PdfArray sampleCountsObject = (PdfArray)getDictionary().get(PdfName.Size);
      for(PdfDirectObject sampleCountObject : sampleCountsObject)
      {sampleCounts.add(((PdfInteger)sampleCountObject).getRawValue());}
    }
    return sampleCounts;
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
