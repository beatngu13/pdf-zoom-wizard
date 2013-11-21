/*
  Copyright 2009-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util.math;

/**
  Specialized math operations.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 11/28/11
*/
public final class OperationUtils
{
  // <class>
  // <static>
  // <fields>
  /**
    Default relative floating-point precision error tolerance.
  */
  private static final double Epsilon = 0.000001;
  // <fields>

  // <interface>
  /**
    Compares double-precision floating-point numbers applying the default error tolerance.

    @param value1 First argument to compare.
    @param value2 Second argument to compare.
    @return How the first argument compares to the second:
      <ul>
        <li>-1, smaller;</li>
        <li>0, equal;</li>
        <li>1, greater.</li>
      </ul>
  */
  public static int compare(
    double value1,
    double value2
    )
  {return compare(value1, value2, Epsilon);}

  /**
    Compares double-precision floating-point numbers applying the specified error tolerance.

    @param value1 First argument to compare.
    @param value2 Second argument to compare.
    @param epsilon Relative error tolerance.
    @return How the first argument compares to the second:
      <ul>
        <li>-1, smaller;</li>
        <li>0, equal;</li>
        <li>1, greater.</li>
      </ul>
  */
  public static int compare(
    double value1,
    double value2,
    double epsilon
    )
  {
    int exponent = Math.getExponent(Math.max(value1, value2));
    double delta = Math.scalb(epsilon, exponent);
    double difference = value1 - value2;
    if (difference > delta)
      return 1;
    else if (difference < -delta)
      return -1;
    else
      return 0;
  }

  /**
    Compares big-endian byte arrays.

    @param data1 First argument to compare.
    @param data2 Second argument to compare.
    @return How the first argument compares to the second:
      <ul>
        <li>-1, smaller;</li>
        <li>0, equal;</li>
        <li>1, greater.</li>
      </ul>
  */
  public static int compare(
    byte[] data1,
    byte[] data2
    )
  {
    for(
      int index = 0,
        length = data1.length;
      index < length;
      index++
      )
    {
      switch((int)Math.signum((data1[index] & 0xff)-(data2[index] & 0xff)))
      {
        case -1:
          return -1;
        case 1:
          return 1;
      }
    }
    return 0;
  }

  /**
    Increments a big-endian byte array.
  */
  public static void increment(
    byte[] data
    )
  {increment(data, data.length-1);}

  /**
    Increments a big-endian byte array at the specified position.
  */
  public static void increment(
    byte[] data,
    int position
    )
  {
    if((data[position] & 0xff) == 255)
    {
      data[position] = 0;
      increment(data, position-1);
    }
    else
    {data[position]++;}
  }
  // </interface>
  // </static>
  // </class>
}