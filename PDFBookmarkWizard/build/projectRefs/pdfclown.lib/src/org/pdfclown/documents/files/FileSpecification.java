/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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
import java.net.URL;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.FileInputStream;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.bytes.OutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.IPdfNamedObjectWrapper;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfString;

/**
  Reference to the contents of another file (file specification) [PDF:1.6:3.10.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF11)
public abstract class FileSpecification<TDataObject extends PdfDirectObject>
  extends PdfObjectWrapper<TDataObject>
  implements IPdfNamedObjectWrapper
{
  // <class>
  // <static>
  // <public>
  /**
    Creates a new reference to an external file.

    @param context Document context.
    @param path File path.
  */
  public static SimpleFileSpecification get(
    Document context,
    String path
    )
  {return (SimpleFileSpecification)get(context, path, false);}

  /**
    Creates a new reference to a file.

    @param context Document context.
    @param path File path.
    @param full Whether the reference is able to support extended dependencies.
  */
  public static FileSpecification<?> get(
    Document context,
    String path,
    boolean full
    )
  {
    return full
      ? new FullFileSpecification(context, path)
      : new SimpleFileSpecification(context, path);
  }

  /**
    Creates a new reference to an embedded file.

    @param embeddedFile Embedded file corresponding to the reference.
    @param filename Name corresponding to the reference.
  */
  public static FullFileSpecification get(
    EmbeddedFile embeddedFile,
    String filename
    )
  {return new FullFileSpecification(embeddedFile, filename);}

  /**
    Creates a new reference to a remote file.

    @param context Document context.
    @param url Remote file location.
  */
  public static FullFileSpecification get(
    Document context,
    URL url
    )
  {return new FullFileSpecification(context, url);}

  /**
    Instantiates an existing file reference.

    @param baseObject Base object.
  */
  public static FileSpecification<?> wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject ==  null)
      return null;

    PdfDataObject baseDataObject = baseObject.resolve();
    if(baseDataObject instanceof PdfString)
      return new SimpleFileSpecification(baseObject);
    else if(baseDataObject instanceof PdfDictionary)
      return new FullFileSpecification(baseObject);
    else
      return null;
  }
  // </public>
  // </static>

  // <dynamic>
  // <constructors>
  protected FileSpecification(
    Document context,
    TDataObject baseDataObject
    )
  {super(context, baseDataObject);}

  protected FileSpecification(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the file absolute path.
  */
  public String getAbsolutePath(
    )
  {
    java.io.File file = new java.io.File(getPath());
    if(!file.isAbsolute()) // Path needs to be resolved.
    {
      String basePath = getDocument().getFile().getPath();
      if(basePath != null)
      {
        java.io.File baseFile = new java.io.File(basePath);
        file = new java.io.File(
          baseFile.isFile() ? baseFile.getParentFile() : baseFile,
          file.getPath()
          );
      }
    }
    return file.getAbsolutePath();
  }

  /**
    Gets an input stream to read from the file.
  */
  public IInputStream getInputStream(
    )
  {
    try
    {
      return new FileInputStream(
        new java.io.RandomAccessFile(getAbsolutePath(),"r")
        );
    }
    catch(FileNotFoundException e)
    {throw new RuntimeException(e);}
  }

  /**
    Gets an output stream to write into the file.
  */
  public IOutputStream getOutputStream(
    )
  {
    try
    {
      return new OutputStream(
        new java.io.BufferedOutputStream(
          new java.io.FileOutputStream(getAbsolutePath())
          )
        );
    }
    catch(FileNotFoundException e)
    {throw new RuntimeException(e);}
  }

  /**
    Gets the file path.
  */
  public abstract String getPath(
    );

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