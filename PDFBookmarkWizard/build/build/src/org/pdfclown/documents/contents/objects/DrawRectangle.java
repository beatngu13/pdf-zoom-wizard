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

import java.awt.geom.Path2D;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  'Append a rectangle to the current path as a complete subpath' operation
  [PDF:1.6:4.4.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class DrawRectangle
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "re";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public DrawRectangle(
    double x,
    double y,
    double width,
    double height
    )
  {
    super(
      Operator,
      PdfReal.get(x),
      PdfReal.get(y),
      PdfReal.get(width),
      PdfReal.get(height)
      );
  }

  public DrawRectangle(
    List<PdfDirectObject> operands
    )
  {super(Operator,operands);}
  // </constructors>

  // <interface>
  // <public>
  public double getHeight(
    )
  {return ((PdfNumber<?>)operands.get(3)).getDoubleValue();}

  public double getWidth(
    )
  {return ((PdfNumber<?>)operands.get(2)).getDoubleValue();}

  public double getX(
    )
  {return ((PdfNumber<?>)operands.get(0)).getDoubleValue();}

  public double getY(
    )
  {return ((PdfNumber<?>)operands.get(1)).getDoubleValue();}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    Path2D pathObject = (Path2D)state.getScanner().getRenderObject();
    if(pathObject != null)
    {
      double x = getX(),
        y = getY(),
        width = getWidth(),
        height = getHeight();
      pathObject.moveTo(x,y);
      pathObject.lineTo(x + width, y);
      pathObject.lineTo(x + width, y + height);
      pathObject.lineTo(x, y + height);
      pathObject.closePath();
    }
  }

  public void setHeight(
    double value
    )
  {operands.set(3, PdfReal.get(value));}

  public void setWidth(
    double value
    )
  {operands.set(2, PdfReal.get(value));}

  public void setX(
    double value
    )
  {operands.set(0, PdfReal.get(value));}

  public void setY(
    double value
    )
  {operands.set(1, PdfReal.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}