/*
  Copyright 2008-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.forms.styles;

import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.interaction.forms.Field;

/**
  Abstract field appearance style.
  <p>It automates the definition of field appearance, applying a common look.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 11/01/11
*/
public abstract class FieldStyle
{
  // <dynamic>
  // <fields>
  private Color<?> backColor = DeviceRGBColor.White;
  private char checkSymbol = (char)52;
  private double fontSize = 10;
  private Color<?> foreColor = DeviceRGBColor.Black;
  private boolean graphicsVisibile = false;
  private char radioSymbol = (char)108;
  // </fields>

  // <constructors>
  protected FieldStyle(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  public abstract void apply(
    Field field
    );

  public Color<?> getBackColor(
    )
  {return backColor;}

  public char getCheckSymbol(
    )
  {return checkSymbol;}

  public double getFontSize(
    )
  {return fontSize;}

  public Color<?> getForeColor(
    )
  {return foreColor;}

  public boolean isGraphicsVisibile(
    )
  {return graphicsVisibile;}

  public char getRadioSymbol(
    )
  {return radioSymbol;}

  public void setBackColor(
    Color<?> value
    )
  {backColor = value;}

  public void setCheckSymbol(
    char value
    )
  {checkSymbol = value;}

  public void setFontSize(
    double value
    )
  {fontSize = value;}

  public void setForeColor(
    Color<?> value
    )
  {foreColor = value;}

  public void setGraphicsVisibile(
    boolean value
    )
  {graphicsVisibile = value;}

  public void setRadioSymbol(
    char value
    )
  {radioSymbol = value;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}