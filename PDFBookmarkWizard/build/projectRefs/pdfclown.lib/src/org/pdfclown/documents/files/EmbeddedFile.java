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

package org.pdfclown.documents.files;

import java.io.FileNotFoundException;
import java.util.Date;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.FileInputStream;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDate;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfStream;

/**
  Embedded file [PDF:1.6:3.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class EmbeddedFile
  extends PdfObjectWrapper<PdfStream>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Creates a new embedded file inside the document.

    @param context Document context.
    @param path Path of the file to embed.
  */
  public static EmbeddedFile get(
    Document context,
    String path
    )
  {
    try
    {
      return new EmbeddedFile(
        context,
        new FileInputStream(
          new java.io.RandomAccessFile(path,"r")
          )
        );
    }
    catch(FileNotFoundException e)
    {throw new RuntimeException(e);}
  }

  /**
    Creates a new embedded file inside the document.

    @param context Document context.
    @param file File to embed.
  */
  public static EmbeddedFile get(
    Document context,
    java.io.File file
    )
  {return get(context, file.getPath());}

  /**
    Creates a new embedded file inside the document.

    @param context Document context.
    @param stream File stream to embed.
  */
  public static EmbeddedFile get(
    Document context,
    IInputStream stream
    )
  {return new EmbeddedFile(context, stream);}

  /**
    Instantiates an existing embedded file.

    @param baseObject Base object.
  */
  public static EmbeddedFile wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new EmbeddedFile(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  private EmbeddedFile(
    Document context,
    IInputStream stream
    )
  {
    super(
      context,
      new PdfStream(
        new PdfDictionary(
          new PdfName[]{PdfName.Type},
          new PdfDirectObject[]{PdfName.EmbeddedFile}
          ),
        new Buffer(stream.toByteArray())
        )
      );
  }

  private EmbeddedFile(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public EmbeddedFile clone(
    Document context
    )
  {return (EmbeddedFile)super.clone(context);}

  /**
    Gets the creation date of this file.
  */
  public Date getCreationDate(
    )
  {
    PdfDate dateObject = (PdfDate)getInfo(PdfName.CreationDate);
    return dateObject != null ? dateObject.getValue() : null;
  }

  /**
    Gets the data contained within this file.
  */
  public IBuffer getData(
    )
  {return getBaseDataObject().getBody();}

  /**
    Gets the MIME media type name of this file [RFC 2046].
  */
  public String getMimeType(
    )
  {
    PdfName subtype = (PdfName)getBaseDataObject().getHeader().get(PdfName.Subtype);
    return subtype != null ? subtype.getValue() : null;
  }

  /**
    Gets the modification date of this file.
  */
  public Date getModificationDate(
    )
  {
    PdfDate dateObject = (PdfDate)getInfo(PdfName.ModDate);
    return dateObject != null ? dateObject.getValue() : null;
  }

  /**
    Gets the size of this file, in bytes.
  */
  public int getSize(
    )
  {
    PdfInteger sizeObject = (PdfInteger)getInfo(PdfName.Size);
    return sizeObject != null ? sizeObject.getValue() : 0;
  }

  /**
    @see #getCreationDate()
  */
  public void setCreationDate(
    Date value
    )
  {setInfo(PdfName.CreationDate, PdfDate.get(value));}

  /**
    @see #getMimeType()
  */
  public void setMimeType(
    String value
    )
  {getBaseDataObject().getHeader().put(PdfName.Subtype, new PdfName(value));}

  /**
    @see #getModificationDate()
  */
  public void setModificationDate(
    Date value
    )
  {setInfo(PdfName.ModDate, PdfDate.get(value));}

  /**
    @see #getSize()
  */
  public void setSize(
    int value
    )
  {setInfo(PdfName.Size, PdfInteger.get(value));}
  // </public>

  // <private>
  /**
    Gets the file parameter associated to the specified key.

    @param key Parameter key.
  */
  private PdfDirectObject getInfo(
    PdfName key
    )
  {return getParams().get(key);}

  /**
    Gets the file parameters.
  */
  private PdfDictionary getParams(
    )
  {return getBaseDataObject().getHeader().resolve(PdfName.Params, PdfDictionary.class);}

  /**
    @see #getInfo(PdfName)
  */
  private void setInfo(
    PdfName key,
    PdfDirectObject value
    )
  {getParams().put(key, value);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}