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

package org.pdfclown.documents.contents.entities;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.InlineImage;
import org.pdfclown.documents.contents.objects.InlineImageBody;
import org.pdfclown.documents.contents.objects.InlineImageHeader;
import org.pdfclown.documents.contents.xObjects.ImageXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;

/**
  JPEG image object [ISO 10918-1;JFIF:1.02].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 08/23/12
*/
public final class JpegImage
  extends Image
{
  // <class>
  // <dynamic>
  // <constructors>
  JpegImage(
    IInputStream stream
    )
  {
    super(stream);

    load();
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    @since 0.0.6
  */
  @SuppressWarnings("unchecked")
  @Override
  public ContentObject toInlineObject(
    PrimitiveComposer composer
    )
  {
    return composer.add(
      new InlineImage(
        new InlineImageHeader(
          (List<PdfDirectObject>)(List<? extends PdfDirectObject>)Arrays.asList(
            PdfName.W, PdfInteger.get(getWidth()),
            PdfName.H, PdfInteger.get(getHeight()),
            PdfName.CS, PdfName.RGB,
            PdfName.BPC, PdfInteger.get(getBitsPerComponent()),
            PdfName.F, PdfName.DCT
            )
          ),
        new InlineImageBody(
          new Buffer(getStream().toByteArray())
          )
        )
      );
  }

  @Override
  public XObject toXObject(
    Document context
    )
  {
    return new ImageXObject(
      context,
      new PdfStream(
        new PdfDictionary(
          new PdfName[]
          {
            PdfName.Width,
            PdfName.Height,
            PdfName.BitsPerComponent,
            PdfName.ColorSpace,
            PdfName.Filter
          },
          new PdfDirectObject[]
          {
            PdfInteger.get(getWidth()),
            PdfInteger.get(getHeight()),
            PdfInteger.get(getBitsPerComponent()),
            PdfName.DeviceRGB,
            PdfName.DCTDecode
          }
          ),
        new Buffer(getStream().toByteArray())
        )
      );
  }
  // </public>

  // <private>
  private void load(
    )
  {
    /*
      NOTE: Big-endian data expected.
    */
    IInputStream stream = getStream();
    // Ensure that data is read using the proper endianness!
    stream.setByteOrder(ByteOrder.BIG_ENDIAN);
    try
    {
      int index = 4;
      stream.seek(index);
      byte[] markerBytes = new byte[2];
      while(true)
      {
        index += stream.readUnsignedShort();
        stream.seek(index);

        stream.read(markerBytes);
        index += 2;

        // Frame header?
        if(markerBytes[0] == (byte)0xFF
          && markerBytes[1] == (byte)0xC0)
        {
          stream.skip(2);
          // Get the image bits per color component (sample precision)!
          setBitsPerComponent(stream.readUnsignedByte());
          // Get the image size!
          setHeight(stream.readUnsignedShort());
          setWidth(stream.readUnsignedShort());

          break;
        }
      }
    }
    catch(EOFException e)
    {throw new RuntimeException(e);}
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}