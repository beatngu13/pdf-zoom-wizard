/*
  Copyright 2007-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;

/**
  Inline image object [PDF:1.6:4.8.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF10)
public final class InlineImage
  extends GraphicsObject
{
  // <class>
  // <static>
  // <fields>
  public static final String BeginOperator = BeginInlineImage.Operator;
  public static final String EndOperator = EndInlineImage.Operator;

  private static final String DataOperator = "ID";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public InlineImage(
    InlineImageHeader header,
    InlineImageBody body
    )
  {super(header,body);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the image body.
   */
  public InlineImageBody getBody(
    )
  {return (InlineImageBody)getObjects().get(1);}

  /**
    Gets the image header.
  */
  @Override
  public InlineImageHeader getHeader(
    )
  {return (InlineImageHeader)getObjects().get(0);}

  /**
    Gets the image size.
  */
  public Dimension2D getSize(
    )
  {
    InlineImageHeader header = getHeader();
    return new Dimension(
      ((PdfNumber<?>)header.get(header.containsKey(PdfName.W) ? PdfName.W : PdfName.Width)).getValue().intValue(),
      ((PdfNumber<?>)header.get(header.containsKey(PdfName.H) ? PdfName.H : PdfName.Height)).getValue().intValue()
      );
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    Document context
    )
  {
    stream.write(BeginOperator); stream.write("\n");
    getHeader().writeTo(stream, context);
    stream.write(DataOperator); stream.write("\n");
    getBody().writeTo(stream, context); stream.write("\n");
    stream.write(EndOperator);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}