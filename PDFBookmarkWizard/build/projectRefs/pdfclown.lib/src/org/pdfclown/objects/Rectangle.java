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

package org.pdfclown.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.pdfclown.documents.Document;

/**
  PDF rectangle object [PDF:1.6:3.8.4].
  <p>Rectangles are described by two diagonally-opposite corners. Corner pairs which don't respect
  the canonical form (lower-left and upper-right) are automatically normalized to provide a
  consistent representation.</p>
  <p>Coordinates are expressed within the PDF coordinate space (lower-left origin and
  positively-oriented axes).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 12/28/12
*/
public final class Rectangle
  extends PdfObjectWrapper<PdfArray>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Rectangle wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Rectangle(baseObject) : null;}
  // </public>

  // <private>
  private static PdfArray normalize(
    PdfArray rectangle
    )
  {
    if(rectangle.get(0).compareTo(rectangle.get(2)) > 0)
    {
      PdfDirectObject leftCoordinate = rectangle.get(2);
      rectangle.set(2, rectangle.get(0));
      rectangle.set(0, leftCoordinate);
    }
    if(rectangle.get(1).compareTo(rectangle.get(3)) > 0)
    {
      PdfDirectObject bottomCoordinate = rectangle.get(3);
      rectangle.set(3, rectangle.get(1));
      rectangle.set(1, bottomCoordinate);
    }
    return rectangle;
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Rectangle(
    RectangularShape rectangle
    )
  {
    this(
      rectangle.getMinX(),
      rectangle.getMaxY(),
      rectangle.getWidth(),
      rectangle.getHeight()
      );
  }

  public Rectangle(
    Point2D lowerLeft,
    Point2D upperRight
    )
  {
    this(
      lowerLeft.getX(),
      upperRight.getY(),
      upperRight.getX()-lowerLeft.getX(),
      upperRight.getY()-lowerLeft.getY()
      );
  }

  public Rectangle(
    double left,
    double top,
    double width,
    double height
    )
  {
    this(
      new PdfArray(
        new PdfDirectObject[]
        {
          PdfReal.get(left), // Left (X).
          PdfReal.get(top - height), // Bottom (Y).
          PdfReal.get(left + width), // Right.
          PdfReal.get(top) // Top.
        }
        )
      );
  }

  private Rectangle(
    PdfDirectObject baseObject
    )
  {super(normalize((PdfArray)baseObject.resolve()));}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Rectangle clone(
    Document context
    )
  {return (Rectangle)super.clone(context);}

  public double getBottom(
    )
  {return ((PdfNumber<?>)getBaseDataObject().get(1)).getDoubleValue();}

  public double getHeight(
    )
  {return getTop() - getBottom();}

  public double getLeft(
    )
  {return ((PdfNumber<?>)getBaseDataObject().get(0)).getDoubleValue();}

  public double getRight(
    )
  {return ((PdfNumber<?>)getBaseDataObject().get(2)).getDoubleValue();}

  public double getTop(
    )
  {return ((PdfNumber<?>)getBaseDataObject().get(3)).getDoubleValue();}

  public double getWidth(
    )
  {return getRight() - getLeft();}

  public double getX(
    )
  {return getLeft();}

  public double getY(
    )
  {return getBottom();}

  public void setBottom(
    double value
    )
  {((PdfNumber<?>)getBaseDataObject().get(1)).setValue(value);}

  public void setHeight(
    double value
    )
  {setBottom(getTop() - value);}

  public void setLeft(
    double value
    )
  {((PdfNumber<?>)getBaseDataObject().get(0)).setValue(value);}

  public void setRight(
    double value
    )
  {((PdfNumber<?>)getBaseDataObject().get(2)).setValue(value);}

  public void setTop(
    double value
    )
  {((PdfNumber<?>)getBaseDataObject().get(3)).setValue(value);}

  public void setWidth(
    double value
    )
  {setRight(getLeft() + value);}

  public void setX(
    double value
    )
  {setLeft(value);}

  public void setY(
    double value
    )
  {setBottom(value);}

  public Rectangle2D toRectangle2D(
    )
  {return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}