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

package org.pdfclown.documents.contents.xObjects;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.PropertyList;
import org.pdfclown.documents.contents.layers.ILayerable;
import org.pdfclown.documents.contents.layers.LayerEntity;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfStream;

/**
  External graphics object whose contents are defined by a self-contained content stream, separate
  from the content stream in which it is used [PDF:1.6:4.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public abstract class XObject
  extends PdfObjectWrapper<PdfStream>
  implements ILayerable
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps an external object reference into an external object.
    @param baseObject External object base object.
    @return External object associated to the reference.
  */
  public static XObject wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfName subtype = (PdfName)((PdfStream)baseObject.resolve()).getHeader().get(PdfName.Subtype);
    if(subtype.equals(PdfName.Form))
      return FormXObject.wrap(baseObject);
    else if(subtype.equals(PdfName.Image))
      return ImageXObject.wrap(baseObject);
    else
      return null;
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new external object inside the document.
  */
  protected XObject(
    Document context
    )
  {this(context, new PdfStream());}

  /**
    Creates a new external object inside the document.
  */
  protected XObject(
    Document context,
    PdfStream baseDataObject
    )
  {
    super(context, baseDataObject);
    baseDataObject.getHeader().put(PdfName.Type,PdfName.XObject);
  }

  /**
    Instantiates an existing external object.
  */
  protected XObject(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the mapping from external-object space to user space.
  */
  public abstract AffineTransform getMatrix(
    );

  /**
    Gets the external object size.
  */
  public abstract Dimension2D getSize(
    );

  /**
    @see #getMatrix()
  */
  public abstract void setMatrix(
    AffineTransform value
    );

  /**
    @see #getSize()
  */
  public abstract void setSize(
    Dimension2D value
    );

  // <ILayerable>
  @Override
  @PDF(VersionEnum.PDF15)
  public LayerEntity getLayer(
    )
  {return (LayerEntity)PropertyList.wrap(getBaseDataObject().getHeader().get(PdfName.OC));}

  @Override
  public void setLayer(
    LayerEntity value
    )
  {getBaseDataObject().getHeader().put(PdfName.OC, value.getBaseObject());}
  // </ILayerable>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}