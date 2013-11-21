/*
  Copyright 2008-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;

/**
  Path object [PDF:1.6:4.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public final class Path
  extends GraphicsObject
{
  // <class>
  // <static>
  // <fields>
  public static final String[] BeginOperators = new String[]
    {
      BeginSubpath.Operator,
      DrawRectangle.Operator
    };
  public static final String[] EndOperators = new String[]
    {
      PaintPath.CloseFillStrokeEvenOddOperator,
      PaintPath.CloseFillStrokeOperator,
      PaintPath.CloseStrokeOperator,
      PaintPath.EndPathNoOpOperator,
      PaintPath.FillEvenOddOperator,
      PaintPath.FillObsoleteOperator,
      PaintPath.FillOperator,
      PaintPath.FillStrokeEvenOddOperator,
      PaintPath.FillStrokeOperator,
      PaintPath.StrokeOperator
    };
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public Path(
    )
  {}

  public Path(
    List<ContentObject> operations
    )
  {super(operations);}
  // </constructors>

  // <interface>
  // <protected>
  @Override
  protected Shape createRenderObject(
    )
  {return new Path2D.Double();}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}