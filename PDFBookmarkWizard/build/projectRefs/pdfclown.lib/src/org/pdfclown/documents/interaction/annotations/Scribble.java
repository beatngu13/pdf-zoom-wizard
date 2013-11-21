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
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  Freehand "scribble" composed of one or more disjoint paths [PDF:1.6:8.4.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class Scribble
  extends Annotation
{
  // <class>
  // <dynamic>
  // <constructors>
  public Scribble(
    Page page,
    Rectangle2D box,
    String text,
    List<List<Point2D>> paths
    )
  {
    super(page, PdfName.Ink, box, text);
    setPaths(paths);
  }

  Scribble(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Scribble clone(
    Document context
    )
  {return (Scribble)super.clone(context);}

  /**
    Gets the coordinates of each path.
  */
  public List<List<Point2D>> getPaths(
    )
  {
    PdfArray pathsObject = (PdfArray)getBaseDataObject().get(PdfName.InkList);
    List<List<Point2D>> paths = new ArrayList<List<Point2D>>();
    double pageHeight = getPage().getBox().getHeight();
    for(
      int pathIndex = 0,
        pathLength = pathsObject.size();
      pathIndex < pathLength;
      pathIndex++
      )
    {
      PdfArray pathObject = (PdfArray)pathsObject.get(pathIndex);
      List<Point2D> path = new ArrayList<Point2D>();
      for(
        int pointIndex = 0,
          pointLength = pathObject.size();
        pointIndex < pointLength;
        pointIndex += 2
        )
      {
        path.add(
          new Point2D.Double(
            ((PdfNumber<?>)pathObject.get(pointIndex)).getDoubleValue(),
            pageHeight - ((PdfNumber<?>)pathObject.get(pointIndex+1)).getDoubleValue()
            )
          );
      }
      paths.add(path);
    }

    return paths;
  }

  /**
    @see #getPaths()
  */
  public void setPaths(
    List<List<Point2D>> value
    )
  {
    PdfArray pathsObject = new PdfArray();
    double pageHeight = getPage().getBox().getHeight();
    for(List<Point2D> path : value)
    {
      PdfArray pathObject = new PdfArray();
      for(Point2D point : path)
      {
        pathObject.add(PdfReal.get(point.getX())); // x.
        pathObject.add(PdfReal.get(pageHeight-point.getY())); // y.
      }
      pathsObject.add(pathObject);
    }

    getBaseDataObject().put(PdfName.InkList,pathsObject);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}