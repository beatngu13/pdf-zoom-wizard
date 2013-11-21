/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import java.awt.geom.Point2D;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  'Begin a new subpath by moving the current point' operation [PDF:1.6:4.4.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class BeginSubpath
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "m";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    @param point Current point.
  */
  public BeginSubpath(
    Point2D point
    )
  {
    this(
      point.getX(),
      point.getY()
      );
  }

  /**
    @param pointX Current point X.
    @param pointY Current point Y.
  */
  public BeginSubpath(
    double pointX,
    double pointY
    )
  {
    super(
      Operator,
      PdfReal.get(pointX),
      PdfReal.get(pointY)
      );
  }

  public BeginSubpath(
    List<PdfDirectObject> operands
    )
  {super(Operator, operands);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the current point.
  */
  public Point2D getPoint(
    )
  {
    return new Point2D.Double(
      ((PdfNumber<?>)operands.get(0)).getDoubleValue(),
      ((PdfNumber<?>)operands.get(1)).getDoubleValue()
      );
  }

  @Override
  public void scan(
    GraphicsState state
    )
  {
    Path2D pathObject = (Path2D)state.getScanner().getRenderObject();
    if(pathObject != null)
    {
      Point2D point = getPoint();
      pathObject.moveTo(point.getX(),point.getY());
    }
  }

  /**
    Sets the current point.

    @see #getPoint()
  */
  public void setPoint(
    Point2D value
    )
  {
    operands.set(0, PdfReal.get(value.getX()));
    operands.set(1, PdfReal.get(value.getY()));
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}