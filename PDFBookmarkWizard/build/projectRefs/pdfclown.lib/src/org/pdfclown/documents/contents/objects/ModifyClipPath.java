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

import java.awt.geom.Path2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.WindModeEnum;

/**
  Clipping path operation [PDF:1.6:4.4.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public final class ModifyClipPath
  extends Operation
{
  // <class>
  // <static>
  // <fields>
  public static final String EvenOddOperator = "W*";
  public static final String NonZeroOperator = "W";

  /**
    'Modify the current clipping path by intersecting it with the current path,
    using the even-odd rule to determine which regions lie inside the clipping path'
    operation.
  */
  public static final ModifyClipPath EvenOdd = new ModifyClipPath(EvenOddOperator, WindModeEnum.EvenOdd);
  /**
    'Modify the current clipping path by intersecting it with the current path,
    using the nonzero winding number rule to determine which regions lie inside
    the clipping path' operation.
  */
  public static final ModifyClipPath NonZero = new ModifyClipPath(NonZeroOperator, WindModeEnum.NonZero);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private WindModeEnum clipMode;
  // </fields>

  // <constructors>
  private ModifyClipPath(
    String operator,
    WindModeEnum clipMode
    )
  {
    super(operator);
    this.clipMode = clipMode;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the clipping rule.
  */
  public WindModeEnum getClipMode(
    )
  {return clipMode;}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    ContentScanner scanner = state.getScanner();
    Path2D pathObject = (Path2D)scanner.getRenderObject();
    if(pathObject != null)
    {
      pathObject.setWindingRule(clipMode.toAwt());
      scanner.getRenderContext().clip(pathObject);
    }
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}