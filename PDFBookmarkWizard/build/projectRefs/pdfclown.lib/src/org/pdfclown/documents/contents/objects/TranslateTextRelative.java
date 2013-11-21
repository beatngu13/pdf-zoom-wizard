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

import java.awt.geom.AffineTransform;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  'Move to the start of the next line, offset from the start of the current line' operation
  [PDF:1.6:5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class TranslateTextRelative
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  /**
    No side effect.
  */
  public static final String SimpleOperator = "Td";
  /**
    Lead parameter setting.
  */
  public static final String LeadOperator = "TD";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public TranslateTextRelative(
    double offsetX,
    double offsetY
    )
  {this(offsetX,offsetY,false);}

  public TranslateTextRelative(
    double offsetX,
    double offsetY,
    boolean leadSet
    )
  {
    super(
      leadSet ? LeadOperator : SimpleOperator,
      PdfReal.get(offsetX),
      PdfReal.get(offsetY)
      );
  }

  public TranslateTextRelative(
    String operator,
    List<PdfDirectObject> operands
    )
  {super(operator,operands);}
  // </constructors>

  // <interface>
  // <public>
  public double getOffsetX(
    )
  {return ((PdfNumber<?>)operands.get(0)).getDoubleValue();}

  public double getOffsetY(
    )
  {return ((PdfNumber<?>)operands.get(1)).getDoubleValue();}

  /**
    Gets whether this operation, as a side effect, sets the leading parameter in the text state.
  */
  public boolean isLeadSet(
    )
  {return operator.equals(LeadOperator);}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    state.getTlm().translate(getOffsetX(),getOffsetY());
    state.setTm((AffineTransform)state.getTlm().clone());
    if(isLeadSet())
    {state.setLead(getOffsetY());}
  }

  /**
    @see #isLeadSet()
  */
  public void setLeadSet(
    boolean value
    )
  {operator = (value ? LeadOperator : SimpleOperator);}

  public void setOffsetX(
    double value
    )
  {operands.set(0, PdfReal.get(value));}

  public void setOffsetY(
    double value
    )
  {operands.set(1, PdfReal.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}