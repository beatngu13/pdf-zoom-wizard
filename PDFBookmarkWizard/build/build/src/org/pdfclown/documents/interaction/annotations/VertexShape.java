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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  Abstract vertexed shape annotation.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public abstract class VertexShape
  extends Shape
{
  // <class>
  // <dynamic>
  // <constructors>
  protected VertexShape(
    Page page,
    Rectangle2D box,
    String text,
    PdfName subtype
    )
  {super(page, box, text, subtype);}

  protected VertexShape(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the coordinates of each vertex.
  */
  public List<Point2D> getVertices(
    )
  {
    PdfArray verticesObject = (PdfArray)getBaseDataObject().get(PdfName.Vertices);
    List<Point2D> vertices = new ArrayList<Point2D>();
    double pageHeight = getPage().getBox().getHeight();
    for(
      int index = 0,
        length = verticesObject.size();
      index < length;
      index += 2
      )
    {
      vertices.add(
        new Point2D.Double(
          ((PdfNumber<?>)verticesObject.get(index)).getDoubleValue(),
          pageHeight - ((PdfNumber<?>)verticesObject.get(index+1)).getDoubleValue()
          )
        );
    }

    return vertices;
  }

  /**
    @see #getVertices()
  */
  public void setVertices(
    List<Point2D> value
    )
  {
    PdfArray verticesObject = new PdfArray();
    double pageHeight = getPage().getBox().getHeight();
    for(Point2D vertex : value)
    {
      verticesObject.add(PdfReal.get(vertex.getX())); // x.
      verticesObject.add(PdfReal.get(pageHeight-vertex.getY())); // y.
    }

    getBaseDataObject().put(PdfName.Vertices,verticesObject);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}