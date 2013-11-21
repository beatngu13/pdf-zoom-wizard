/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.navigation.document;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.IPdfNamedObjectWrapper;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfString;

/**
  Interaction target [PDF:1.6:8.2.1].
  <p>It represents a particular view of a document, consisting of the following items:</p>
  <ul>
    <li>the page of the document to be displayed;</li>
    <li>the location of the document window on that page;</li>
    <li>the magnification (zoom) factor to use when displaying the page.</li>
  </ul>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public abstract class Destination
  extends PdfObjectWrapper<PdfArray>
  implements IPdfNamedObjectWrapper
{
  // <class>
  // <classes>
  /**
    Destination mode [PDF:1.6:8.2.1].
  */
  public enum ModeEnum
  {
    /**
      Display the page at the given upper-left position,
      applying the given magnification.

      <p>View parameters:</p>
      <ol>
        <li>left coordinate</li>
        <li>top coordinate</li>
        <li>zoom</li>
      </ol>
    */
    XYZ(PdfName.XYZ),
    /**
      Display the page with its contents magnified just enough to fit
      the entire page within the window both horizontally and vertically.

      <p>No view parameters.</p>
    */
    Fit(PdfName.Fit),
    /**
      Display the page with the vertical coordinate <code>top</code> positioned
      at the top edge of the window and the contents of the page magnified
      just enough to fit the entire width of the page within the window.

      <p>View parameters:</p>
      <ol>
        <li>top coordinate</li>
      </ol>
    */
    FitHorizontal(PdfName.FitH),
    /**
      Display the page with the horizontal coordinate <code>left</code> positioned
      at the left edge of the window and the contents of the page magnified
      just enough to fit the entire height of the page within the window.

      <p>View parameters:</p>
      <ol>
        <li>left coordinate</li>
      </ol>
    */
    FitVertical(PdfName.FitV),
    /**
      Display the page with its contents magnified just enough to fit
      the rectangle specified by the given coordinates entirely
      within the window both horizontally and vertically.

      <p>View parameters:</p>
      <ol>
        <li>left coordinate</li>
        <li>bottom coordinate</li>
        <li>right coordinate</li>
        <li>top coordinate</li>
      </ol>
    */
    FitRectangle(PdfName.FitR),
    /**
      Display the page with its contents magnified just enough to fit
      its bounding box entirely within the window both horizontally and vertically.

      <p>No view parameters.</p>
    */
    FitBoundingBox(PdfName.FitB),
    /**
      Display the page with the vertical coordinate <code>top</code> positioned
      at the top edge of the window and the contents of the page magnified
      just enough to fit the entire width of its bounding box within the window.

      <p>View parameters:</p>
      <ol>
        <li>top coordinate</li>
      </ol>
    */
    FitBoundingBoxHorizontal(PdfName.FitBH),
    /**
      Display the page with the horizontal coordinate <code>left</code> positioned
      at the left edge of the window and the contents of the page magnified
      just enough to fit the entire height of its bounding box within the window.

      <p>View parameters:</p>
      <ol>
        <li>left coordinate</li>
      </ol>
    */
    FitBoundingBoxVertical(PdfName.FitBV);

    public static ModeEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return null;

      for(ModeEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Mode unknown: " + name);
    }

    private PdfName name;

    private ModeEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  /**
    Wraps a destination base object into a destination object.

    @param baseObject Destination base object.
    @return Destination object associated to the base object.
  */
  public static final Destination wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfArray dataObject = (PdfArray)baseObject.resolve();
    PdfDirectObject pageObject = dataObject.get(0);
    if(pageObject instanceof PdfReference)
      return new LocalDestination(baseObject);
    else if(pageObject instanceof PdfInteger)
      return new RemoteDestination(baseObject);
    else
      throw new IllegalArgumentException("'baseObject' parameter doesn't represent a valid destination object.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new destination within the given document context.

    @param context Document context.
    @param page Page reference. It may be either a {@link Page} or a page index (int).
    @param mode Destination mode.
    @param location Destination location.
    @param zoom Magnification factor to use when displaying the page.
  */
  protected Destination(
    Document context,
    Object page,
    ModeEnum mode,
    Object location,
    Double zoom
    )
  {
    super(context, new PdfArray(null, null));
    setPage(page);
    setMode(mode);
    setLocation(location);
    setZoom(zoom);
  }

  protected Destination(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the page location.
  */
  public Object getLocation(
    )
  {
    switch(getMode())
    {
      case FitBoundingBoxHorizontal:
      case FitBoundingBoxVertical:
      case FitHorizontal:
      case FitVertical:
        return PdfSimpleObject.getValue(getBaseDataObject().get(2), Double.NaN);
      case FitRectangle:
      {
        Double left = (Double)PdfSimpleObject.getValue(getBaseDataObject().get(2), Double.NaN);
        Double top = (Double)PdfSimpleObject.getValue(getBaseDataObject().get(5), Double.NaN);
        Double width = (Double)PdfSimpleObject.getValue(getBaseDataObject().get(4), Double.NaN) - left;
        Double height = (Double)PdfSimpleObject.getValue(getBaseDataObject().get(3), Double.NaN) - top;
        return new Rectangle2D.Double(left, top, width, height);
      }
      case XYZ:
        return new Point2D.Double(
          (Double)PdfSimpleObject.getValue(getBaseDataObject().get(2), Double.NaN),
          (Double)PdfSimpleObject.getValue(getBaseDataObject().get(3), Double.NaN)
          );
      default:
        return null;
    }
  }

  /**
    Gets the destination mode.
  */
  public ModeEnum getMode(
    )
  {return ModeEnum.valueOf((PdfName)getBaseDataObject().get(1));}

  /**
    Gets the target page reference.
  */
  public abstract Object getPage(
    );

  /**
    Gets the magnification factor to use when displaying the page.
  */
  public Double getZoom(
    )
  {
    switch(getMode())
    {
      case XYZ:
        return (Double)PdfSimpleObject.getValue(getBaseDataObject().get(4));
      default:
        return null;
    }
  }

  /**
    @see #getLocation()
  */
  public void setLocation(
    Object value
    )
  {
    PdfArray baseDataObject = getBaseDataObject();
    switch(getMode())
    {
      case FitBoundingBoxHorizontal:
      case FitBoundingBoxVertical:
      case FitHorizontal:
      case FitVertical:
        baseDataObject.set(2, PdfReal.get((Number)value));
        break;
      case FitRectangle:
      {
        Rectangle2D rectangle = (Rectangle2D)value;
        baseDataObject.set(2, PdfReal.get(rectangle.getMinX()));
        baseDataObject.set(3, PdfReal.get(rectangle.getMinY()));
        baseDataObject.set(4, PdfReal.get(rectangle.getMaxX()));
        baseDataObject.set(5, PdfReal.get(rectangle.getMaxY()));
        break;
      }
      case XYZ:
      {
        Point2D point = (Point2D)value;
        baseDataObject.set(2, PdfReal.get(point.getX()));
        baseDataObject.set(3, PdfReal.get(point.getY()));
        break;
      }
      default:
        /* NOOP */
        break;
    }
  }

  /**
    @see #getMode()
  */
  public void setMode(
    ModeEnum value
    )
  {
    PdfArray baseDataObject = getBaseDataObject();

    baseDataObject.set(1, value.getName());

    // Adjusting parameter list...
    int parametersCount;
    switch(value)
    {
      case Fit:
      case FitBoundingBox:
        parametersCount = 2;
        break;
      case FitBoundingBoxHorizontal:
      case FitBoundingBoxVertical:
      case FitHorizontal:
      case FitVertical:
        parametersCount = 3;
        break;
      case XYZ:
        parametersCount = 5;
        break;
      case FitRectangle:
        parametersCount = 6;
        break;
      default:
        throw new UnsupportedOperationException("Mode unknown: " + value);
    }
    while(baseDataObject.size() < parametersCount)
    {baseDataObject.add(null);}
    while(baseDataObject.size() > parametersCount)
    {baseDataObject.remove(baseDataObject.size() - 1);}
  }

  /**
    @see #getPage()
  */
  public abstract void setPage(
    Object value
    );

  /**
    @see #getZoom()
  */
  public void setZoom(
    Double value
    )
  {
    switch(getMode())
    {
      case XYZ:
        getBaseDataObject().set(4, PdfReal.get(value));
        break;
      default:
        /* NOOP */
        break;
    }
  }

  // <IPdfNamedObjectWrapper>
  @Override
  public PdfString getName(
    )
  {return retrieveName();}

  @Override
  public PdfDirectObject getNamedBaseObject(
    )
  {return retrieveNamedBaseObject();}
  // </IPdfNamedObjectWrapper>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}