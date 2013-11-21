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
  'Append a cubic Bezier curve to the current path' operation [PDF:1.6:4.4.1].
  <p>Such curves are defined by four points:
  two endpoints (the <b>current point</b> and the <b>final point</b>)
  and two control points (the <b>first control point</b>, associated to the current point,
  and the <b>second control point</b>, associated to the final point).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF10)
public final class DrawCurve
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  /**
    Specifies only the second control point
    (the first control point coincides with the initial point of the curve).
  */
  public static final String FinalOperator = "v";
  /**
    Specifies both control points explicitly.
  */
  public static final String FullOperator = "c";
  /**
    Specifies only the first control point
    (the second control point coincides with the final point of the curve).
  */
  public static final String InitialOperator = "y";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a fully-explicit curve.

    @param point Final endpoint.
    @param control1 First control point.
    @param control2 Second control point.
  */
  public DrawCurve(
    Point2D point,
    Point2D control1,
    Point2D control2
    )
  {
    this(
      point.getX(),
      point.getY(),
      control1.getX(),
      control1.getY(),
      control2.getX(),
      control2.getY()
      );
  }

  /**
    Creates a fully-explicit curve.
  */
  public DrawCurve(
    double pointX,
    double pointY,
    double control1X,
    double control1Y,
    double control2X,
    double control2Y
    )
  {
    super(
      FullOperator,
      PdfReal.get(control1X),
      PdfReal.get(control1Y),
      PdfReal.get(control2X),
      PdfReal.get(control2Y),
      PdfReal.get(pointX),
      PdfReal.get(pointY)
      );
  }

  /**
    Creates a partially-explicit curve.

    @param point Final endpoint.
    @param control Explicit control point.
    @param operator Operator (either <code>InitialOperator</code> or <code>FinalOperator</code>).
      It defines how to interpret the <code>control</code> parameter.
  */
  public DrawCurve(
    Point2D point,
    Point2D control,
    String operator
    )
  {
    super(
      operator.equals(InitialOperator) ? InitialOperator : FinalOperator,
      PdfReal.get(control.getX()),
      PdfReal.get(control.getY()),
      PdfReal.get(point.getX()),
      PdfReal.get(point.getY())
      );
  }

  public DrawCurve(
    String operator,
    List<PdfDirectObject> operands
    )
  {super(operator,operands);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the first control point.
  */
  public Point2D getControl1(
    )
  {
    if(operator.equals(FinalOperator))
      return null;
    else
      return new Point2D.Double(
        ((PdfNumber<?>)operands.get(0)).getDoubleValue(),
        ((PdfNumber<?>)operands.get(1)).getDoubleValue()
        );
  }

  /**
    Gets the second control point.
  */
  public Point2D getControl2(
    )
  {
    if(operator.equals(FinalOperator))
      return new Point2D.Double(
        ((PdfNumber<?>)operands.get(0)).getDoubleValue(),
        ((PdfNumber<?>)operands.get(1)).getDoubleValue()
        );
    else
      return new Point2D.Double(
        ((PdfNumber<?>)operands.get(2)).getDoubleValue(),
        ((PdfNumber<?>)operands.get(3)).getDoubleValue()
        );
  }

  /**
    Gets the final endpoint.
  */
  public Point2D getPoint(
    )
  {
    if(operator.equals(FullOperator))
      return new Point2D.Double(
        ((PdfNumber<?>)operands.get(4)).getDoubleValue(),
        ((PdfNumber<?>)operands.get(5)).getDoubleValue()
        );
    else
      return new Point2D.Double(
        ((PdfNumber<?>)operands.get(2)).getDoubleValue(),
        ((PdfNumber<?>)operands.get(3)).getDoubleValue()
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
      Point2D controlPoint1 = getControl1();
      if(controlPoint1 == null)
      {controlPoint1 = pathObject.getCurrentPoint();}
      Point2D finalPoint = getPoint();
      Point2D controlPoint2 = getControl2();
      if(controlPoint2 == null)
      {controlPoint2 = finalPoint;}
      pathObject.curveTo(
        controlPoint1.getX(),
        controlPoint1.getY(),
        controlPoint2.getX(),
        controlPoint2.getY(),
        finalPoint.getX(),
        finalPoint.getY()
        );
    }
  }

  /**
    @see #getControl1()
  */
  public void setControl1(
    Point2D value
    )
  {
    if(operator.equals(FinalOperator))
    {
      operator = FullOperator;
      operands.add(0,PdfReal.get(value.getX()));
      operands.add(1,PdfReal.get(value.getY()));
    }
    else
    {
      operands.set(0, PdfReal.get(value.getX()));
      operands.set(1, PdfReal.get(value.getY()));
    }
  }

  /**
    @see #getControl2()
  */
  public void setControl2(
    Point2D value
    )
  {
    if(operator.equals(FinalOperator))
    {
      operands.set(0, PdfReal.get(value.getX()));
      operands.set(1, PdfReal.get(value.getY()));
    }
    else
    {
      operands.set(2, PdfReal.get(value.getX()));
      operands.set(3, PdfReal.get(value.getY()));
    }
  }

  /**
    @see #getPoint()
  */
  public void setPoint(
    Point2D value
    )
  {
    if(operator.equals(FullOperator))
    {
      operands.set(4, PdfReal.get(value.getX()));
      operands.set(5, PdfReal.get(value.getY()));
    }
    else
    {
      operands.set(2, PdfReal.get(value.getX()));
      operands.set(3, PdfReal.get(value.getY()));
    }
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}