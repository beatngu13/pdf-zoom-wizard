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

package org.pdfclown.util.math;

/**
  An interval of comparable objects.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 09/24/12
*/
public final class Interval<T extends Comparable<? super T>>
{
  private T high = null;
  private boolean highInclusive;
  private T low = null;
  private boolean lowInclusive;

  public Interval(
    T low,
    T high
    )
  {this(low, high, true, true);}

  public Interval(
    T low,
    T high,
    boolean lowInclusive,
    boolean highInclusive
    )
  {
    this.low = low;
    this.high = high;
    this.lowInclusive = lowInclusive;
    this.highInclusive = highInclusive;
  }

  /**
    Gets whether the specified value is contained within this interval.

    @param value Value to check for containment.
  */
  public boolean contains(
    T value
    )
  {
    int lowCompare = (low != null ? low.compareTo(value) : -1);
    int highCompare = (high != null ? high.compareTo(value) : 1);
    return (lowCompare < 0
        || (lowCompare == 0 && lowInclusive))
      && (highCompare > 0
        || (highCompare == 0 && highInclusive));
  }

  /**
    Gets the higher interval endpoint.
  */
  public T getHigh(
    )
  {return high;}

  /**
    Gets the lower interval endpoint.
  */
  public T getLow(
    )
  {return low;}

  /**
    Gets whether the higher endpoint is inclusive.
  */
  public boolean isHighInclusive(
    )
  {return highInclusive;}

  /**
    Gets whether the lower endpoint is inclusive.
  */
  public boolean isLowInclusive(
    )
  {return lowInclusive;}

  /**
    @see #getHigh()
  */
  public void setHigh(
    T value
    )
  {high = value;}

  /**
    @see #isHighInclusive()
  */
  public void setHighInclusive(
    boolean value
    )
  {highInclusive = value;}

  /**
    @see #getLow()
  */
  public void setLow(
    T value
    )
  {low = value;}

  /**
    @see #isLowInclusive()
  */
  public void setLowInclusive(
    boolean value
    )
  {lowInclusive = value;}
}