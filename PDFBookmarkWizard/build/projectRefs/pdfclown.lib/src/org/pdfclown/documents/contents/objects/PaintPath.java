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

package org.pdfclown.documents.contents.objects;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.LineDash;
import org.pdfclown.documents.contents.WindModeEnum;
import org.pdfclown.util.ConvertUtils;

/**
  Path-painting operation [PDF:1.6:4.4.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF10)
public final class PaintPath
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String CloseFillStrokeEvenOddOperator = "b*";
  public static final String CloseFillStrokeOperator = "b";
  public static final String CloseStrokeOperator = "s";
  public static final String EndPathNoOpOperator = "n";
  public static final String FillEvenOddOperator = "f*";
  public static final String FillObsoleteOperator = "F";
  public static final String FillOperator = "f";
  public static final String FillStrokeEvenOddOperator = "B*";
  public static final String FillStrokeOperator = "B";
  public static final String StrokeOperator = "S";

  /**
    'Close, fill, and then stroke the path, using the nonzero winding number rule to determine
    the region to fill' operation.
  */
  public static final PaintPath CloseFillStroke = new PaintPath(CloseFillStrokeOperator, true, true, true, WindModeEnum.NonZero);
  /**
    'Close, fill, and then stroke the path, using the even-odd rule to determine the region
    to fill' operation.
  */
  public static final PaintPath CloseFillStrokeEvenOdd = new PaintPath(CloseFillStrokeEvenOddOperator, true, true, true, WindModeEnum.EvenOdd);
  /**
    'Close and stroke the path' operation.
  */
  public static final PaintPath CloseStroke = new PaintPath(CloseStrokeOperator, true, true, false, null);
  /**
    'End the path object without filling or stroking it' operation.
  */
  public static final PaintPath EndPathNoOp = new PaintPath(EndPathNoOpOperator, false, false, false, null);
  /**
    'Fill the path, using the nonzero winding number rule to determine the region to fill' operation.
  */
  public static final PaintPath Fill = new PaintPath(FillOperator, false, false, true, WindModeEnum.NonZero);
  /**
    'Fill the path, using the even-odd rule to determine the region to fill' operation.
  */
  public static final PaintPath FillEvenOdd = new PaintPath(FillEvenOddOperator, false, false, true, WindModeEnum.EvenOdd);
  /**
    'Fill and then stroke the path, using the nonzero winding number rule to determine the region to
    fill' operation.
  */
  public static final PaintPath FillStroke = new PaintPath(FillStrokeOperator, false, true, true, WindModeEnum.NonZero);
  /**
    'Fill and then stroke the path, using the even-odd rule to determine the region to fill' operation.
  */
  public static final PaintPath FillStrokeEvenOdd = new PaintPath(FillStrokeEvenOddOperator, false, true, true, WindModeEnum.EvenOdd);
  /**
    'Stroke the path' operation.
  */
  public static final PaintPath Stroke = new PaintPath(StrokeOperator, false, true, false, null);
  // </fields>

  // <interface>
  // <private>
  private static java.awt.Stroke getStroke(
    GraphicsState state
    )
  {
    LineDash lineDash = state.getLineDash();
    double[] dashArray = lineDash.getDashArray();

    return new BasicStroke(
      (float)state.getLineWidth(),
      state.getLineCap().toAwt(),
      state.getLineJoin().toAwt(),
      (float)state.getMiterLimit(),
      dashArray != null && dashArray.length > 0 ? ConvertUtils.toFloatArray(dashArray) : null,
      (float)lineDash.getDashPhase()
      );
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final boolean closed;
  private final boolean filled;
  private final WindModeEnum fillMode;
  private final boolean stroked;
  // </fields>

  // <constructors>
  private PaintPath(
    String operator,
    boolean closed,
    boolean stroked,
    boolean filled,
    WindModeEnum fillMode
    )
  {
    super(operator);

    this.closed = closed;
    this.stroked = stroked;
    this.filled = filled;
    this.fillMode = fillMode;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the filling rule.
  */
  public WindModeEnum getFillMode(
    )
  {return fillMode;}

  /**
    Gets whether the current path has to be closed.
  */
  public boolean isClosed(
    )
  {return closed;}

  /**
    Gets whether the current path has to be filled.
  */
  public boolean isFilled(
    )
  {return filled;}

  /**
    Gets whether the current path has to be stroked.
  */
  public boolean isStroked(
    )
  {return stroked;}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    ContentScanner scanner = state.getScanner();
    Path2D pathObject = (Path2D)scanner.getRenderObject();
    if(pathObject != null)
    {
      Graphics2D context = scanner.getRenderContext();

      if(closed)
      {
        pathObject.closePath();
      }
      if(filled)
      {
        context.setPaint(
          state.getFillColorSpace().getPaint(state.getFillColor())
          );
        pathObject.setWindingRule(fillMode.toAwt());
        context.fill(pathObject);
      }
      if(stroked)
      {
        context.setPaint(
          state.getStrokeColorSpace().getPaint(state.getStrokeColor())
          );
        context.fill(new Path2D.Double(getStroke(state).createStrokedShape(pathObject)));
      }
    }
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}