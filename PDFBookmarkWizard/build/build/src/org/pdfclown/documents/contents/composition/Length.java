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

package org.pdfclown.documents.contents.composition;

/**
  Distance measure.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8.3
  @version 0.1.2, 01/20/12
*/
public final class Length
{
  /**
    Measurement mode.
  */
  public enum UnitModeEnum
  {
    /**
      Values are expressed as absolute measures.
    */
    Absolute,
    /**
      Values are expressed as ratios relative to a specified base value.
    */
    Relative
  }

  private UnitModeEnum unitMode;
  private double value;

  public Length(
    double value,
    UnitModeEnum unitMode
    )
  {
    this.value = value;
    this.unitMode = unitMode;
  }

  /**
    Gets the <b>measurement mode</b> applied to the {@link #getValue() distance value}.
  */
  public UnitModeEnum getUnitMode(
    )
  {return unitMode;}

  /**
    Gets the <b>distance value</b>.
    <p>According to the applied {@link #getUnitMode() unit mode}, this value can be
    either an <i>absolute measure</i> or a <i>ratio to be resolved through a base value</i>.</p>

    @see #getValue(double)
  */
  public double getValue(
    )
  {return value;}

  /**
    Gets the <b>resolved distance value</b>.
    <p>This method ensures that relative distance values are transformed according
    to the specified base value.</p>

    @param baseValue Value used to resolve relative values.
  */
  public double getValue(
    double baseValue
    )
  {
    switch(unitMode)
    {
      case Absolute:
        return value;
      case Relative:
        return baseValue * value;
      default:
        throw new UnsupportedOperationException(unitMode.getClass().getSimpleName() + " not supported.");
    }
  }

  public void setUnitMode(
    UnitModeEnum value
    )
  {unitMode = value;}

  public void setValue(
    double value
    )
  {this.value = value;}

  @Override
  public String toString(
    )
  {return value + " (" + unitMode + ")";}
}