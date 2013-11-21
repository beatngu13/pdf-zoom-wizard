/*
  Copyright 2007-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.colorSpaces.Color;
import org.pdfclown.documents.contents.colorSpaces.Pattern;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  'Set the color to use for stroking operations' operation [PDF:1.6:4.5.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.0
*/
@PDF(VersionEnum.PDF12)
public class SetStrokeColor
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  /**
    'Set the color to use for stroking operations in any color space' operator.
  */
  @PDF(VersionEnum.PDF12)
  public static final String ExtendedOperator = "SCN";
  /**
    'Set the color to use for stroking operations in a device, CIE-based (other than ICCBased),
    or Indexed color space' operator.
  */
  @PDF(VersionEnum.PDF11)
  public static final String Operator = "SC";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public SetStrokeColor(
    Color<?> value
    )
  {this(ExtendedOperator, value);}

  public SetStrokeColor(
    List<PdfDirectObject> operands
    )
  {this(ExtendedOperator, operands);}

  public SetStrokeColor(
    String operator,
    List<PdfDirectObject> operands
    )
  {super(operator, operands);}

  protected SetStrokeColor(
    String operator,
    Color<?> value
    )
  {super(operator, new ArrayList<PdfDirectObject>(value.getComponents()));}

  /**
    @param operator Graphics operator.
    @param name Name of the color resource entry (see {@link Pattern}).
   */
  protected SetStrokeColor(
    String operator,
    PdfName name
    )
  {this(operator, name, null);}

  /**
    @param operator Graphics operator.
    @param name Name of the color resource entry (see {@link Pattern}).
    @param underlyingColor Color used to colorize the pattern.
   */
  protected SetStrokeColor(
    String operator,
    PdfName name,
    Color<?> underlyingColor
    )
  {
    super(operator, new ArrayList<PdfDirectObject>());
    if(underlyingColor != null)
    {operands.addAll(underlyingColor.getComponents());}
    operands.add(name);
  }
  // </constructors>

  // <interface>
  // <public>
  public List<PdfDirectObject> getComponents(
    )
  {return operands;}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    state.setStrokeColor(
      state.getStrokeColorSpace().getColor(
        operands,
        state.getScanner().getContentContext()
        )
      );
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}