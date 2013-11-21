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

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;

/**
  Image external object [PDF:1.6:4.8.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class ImageXObject
  extends XObject
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static ImageXObject wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ImageXObject(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ImageXObject(
    Document context,
    PdfStream baseDataObject
    )
  {
    /*
      NOTE: It's caller responsability to adequately populate the stream
      header and body in order to instantiate a valid object; header entries like
      'Width', 'Height', 'ColorSpace', 'BitsPerComponent' MUST be defined
      appropriately.
    */

    super(
      context,
      baseDataObject
      );

    baseDataObject.getHeader().put(PdfName.Subtype,PdfName.Image);
  }

  private ImageXObject(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ImageXObject clone(
    Document context
    )
  {return (ImageXObject)super.clone(context);}

  /**
    Gets the number of bits per color component.
  */
  public int getBitsPerComponent(
    )
  {return ((PdfInteger)getBaseDataObject().getHeader().get(PdfName.BitsPerComponent)).getRawValue();}

  /**
    Gets the color space in which samples are specified.
  */
  public String getColorSpace(
    )
  {return ((PdfName)getBaseDataObject().getHeader().get(PdfName.ColorSpace)).getRawValue();}

  @Override
  public AffineTransform getMatrix(
    )
  {
    Dimension2D size = getSize();
    /*
      NOTE: Image-space-to-user-space matrix is [1/w 0 0 1/h 0 0],
      where w and h are the width and height of the image in samples [PDF:1.6:4.8.3].
    */
    return new AffineTransform(
      1 / size.getWidth(), // a.
      0, // b.
      0, // c.
      1 / size.getHeight(), // d.
      0, // e.
      0 // f.
      );
  }

  /**
    Gets the size of the image (in samples).
  */
  @Override
  public Dimension2D getSize(
    )
  {
    PdfDictionary header = getBaseDataObject().getHeader();

    return new Dimension(
      ((PdfInteger)header.get(PdfName.Width)).getRawValue(),
      ((PdfInteger)header.get(PdfName.Height)).getRawValue()
      );
  }

  @Override
  public void setMatrix(
    AffineTransform value
    )
  {/* NOOP. */}

  @Override
  public void setSize(
    Dimension2D value
    )
  {throw new UnsupportedOperationException();}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}