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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.bytes.OutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.files.FileIdentifier;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.PdfTextString;

/**
  Extended reference to the contents of another file [PDF:1.6:3.10.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF11)
public final class FullFileSpecification
  extends FileSpecification<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Standard file system.
  */
  public static enum StandardFileSystemEnum
  {
    /**
      Generic platform file system.
    */
    Native(null),
    /**
      Uniform resource locator.
    */
    URL(PdfName.URL);

    private static Map<PdfName, StandardFileSystemEnum> map = new HashMap<PdfName, StandardFileSystemEnum>();

    static
    {
      for(StandardFileSystemEnum value : StandardFileSystemEnum.values())
      {map.put(value.getCode(), value);}
    }

    public static StandardFileSystemEnum valueOf(
      PdfName code
      )
    {return map.get(code);}

    private final PdfName code;

    private StandardFileSystemEnum(
      PdfName code
      )
    {this.code = code;}

    public PdfName getCode(
      )
    {return code;}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  FullFileSpecification(
    Document context,
    String path
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.Filespec}
        )
      );
    setPath(path);
  }

  FullFileSpecification(
    EmbeddedFile embeddedFile,
    String filename
    )
  {
    this(embeddedFile.getDocument(), filename);
    setEmbeddedFile(embeddedFile);
  }

  FullFileSpecification(
    Document context,
    URL url
    )
  {
    this(context, url.toString());
    setFileSystem(StandardFileSystemEnum.URL);
  }

  FullFileSpecification(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public FullFileSpecification clone(
    Document context
    )
  {return (FullFileSpecification)super.clone(context);}

  /**
    Gets the related files.
  */
  public RelatedFiles getDependencies(
    )
  {return getDependencies(PdfName.F);}

  /**
    Gets the description of the file.
  */
  public String getDescription(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.Desc));}

  /**
    Gets the embedded file corresponding to this file.
  */
  public EmbeddedFile getEmbeddedFile(
    )
  {return getEmbeddedFile(PdfName.F);}

  /**
    Gets the file system to be used to interpret this file specification.

    @return Either {@link StandardFileSystemEnum} (standard file system) or {@link String} (custom
      file system).
  */
  public Object getFileSystem(
    )
  {
    PdfName fileSystemObject = (PdfName)getBaseDataObject().get(PdfName.FS);
    StandardFileSystemEnum standardFileSystem = StandardFileSystemEnum.valueOf(fileSystemObject);
    return standardFileSystem != null ? standardFileSystem : fileSystemObject.getValue();
  }

  /**
    Gets the identifier of the file.
  */
  public FileIdentifier getID(
    )
  {return FileIdentifier.wrap(getBaseDataObject().get(PdfName.ID));}

  @Override
  public String getPath(
    )
  {return getPath(PdfName.F);}

  @Override
  public IInputStream getInputStream(
    )
  {
    if(PdfName.URL.equals(getBaseDataObject().get(PdfName.FS))) // Remote resource [PDF:1.7:3.10.4].
    {
      URL fileUrl;
      try
      {fileUrl = new URL(getPath());}
      catch(MalformedURLException e)
      {throw new RuntimeException("Failed to instantiate URL for " + getPath(), e);}
      try
      {return new Buffer(fileUrl.openStream());}
      catch(IOException e)
      {throw new RuntimeException("Failed to open input stream for " + getPath(), e);}
    }
    else // Local resource [PDF:1.7:3.10.1].
      return super.getInputStream();
  }

  @Override
  public IOutputStream getOutputStream(
    )
  {
    if(PdfName.URL.equals(getBaseDataObject().get(PdfName.FS))) // Remote resource [PDF:1.7:3.10.4].
    {
      URL fileUrl;
      try
      {fileUrl = new URL(getPath());}
      catch(MalformedURLException e)
      {throw new RuntimeException("Failed to instantiate URL for " + getPath(), e);}
      URLConnection connection;
      try
      {connection = fileUrl.openConnection();}
      catch(IOException e)
      {throw new RuntimeException("Failed to open connection for " + getPath(), e);}
      connection.setDoOutput(true);
      try
      {return new OutputStream(connection.getOutputStream());}
      catch(IOException e)
      {throw new RuntimeException("Failed to open output stream for " + getPath(), e);}
    }
    else // Local resource [PDF:1.7:3.10.1].
      return super.getOutputStream();
  }

  /**
    Gets whether the referenced file is volatile (changes frequently with time).
  */
  public boolean isVolatile(
    )
  {return (Boolean)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.V), false);}

  /**
    @see #getDependencies()
  */
  public void setDependencies(
    RelatedFiles value
    )
  {setDependencies(PdfName.F,value);}

  /**
    @see #getDescription()
  */
  public void setDescription(
    String value
    )
  {getBaseDataObject().put(PdfName.Desc, new PdfTextString(value));}

  /**
    @see #getEmbeddedFile()
  */
  public void setEmbeddedFile(
    EmbeddedFile value
    )
  {setEmbeddedFile(PdfName.F,value);}

  /**
    @see #getFileSystem()
  */
  public void setFileSystem(
    Object value
    )
  {
    PdfName fileSystemObject;
    if(value instanceof StandardFileSystemEnum)
    {fileSystemObject = ((StandardFileSystemEnum)value).getCode();}
    else if(value instanceof String)
    {fileSystemObject = new PdfName((String)value);}
    else
      throw new IllegalArgumentException("MUST be either StandardFileSystemEnum (standard file system) or String (custom file system)");

    getBaseDataObject().put(PdfName.FS, fileSystemObject);
  }

  /**
    @see #getID()
  */
  public void setID(
    FileIdentifier value
    )
  {getBaseDataObject().put(PdfName.ID, value.getBaseObject());}

  /**
    @see #getPath()
  */
  public void setPath(
    String value
    )
  {setPath(PdfName.F,value);}

  /**
    @see #isVolatile()
  */
  public void  setVolatile(
    boolean value
    )
  {getBaseDataObject().put(PdfName.V, PdfBoolean.get(value));}
  // </public>

  // <private>
  /**
    Gets the related files associated to the given key.
  */
  private RelatedFiles getDependencies(
    PdfName key
    )
  {
    PdfDictionary dependenciesObject = (PdfDictionary)getBaseDataObject().get(PdfName.RF);
    if(dependenciesObject == null)
      return null;

    return RelatedFiles.wrap(dependenciesObject.get(key));
  }

  /**
    Gets the embedded file associated to the given key.
  */
  private EmbeddedFile getEmbeddedFile(
    PdfName key
    )
  {
    PdfDictionary embeddedFilesObject = (PdfDictionary)getBaseDataObject().get(PdfName.EF);
    if(embeddedFilesObject == null)
      return null;

    return EmbeddedFile.wrap(embeddedFilesObject.get(key));
  }

  /**
    Gets the file path associated to the given key.
  */
  private String getPath(
    PdfName key
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(key));}

  /**
    @see #getDependencies(PdfName)
  */
  private void setDependencies(
    PdfName key,
    RelatedFiles value
    )
  {
    PdfDictionary dependenciesObject = (PdfDictionary)getBaseDataObject().get(PdfName.RF);
    if(dependenciesObject == null)
    {getBaseDataObject().put(PdfName.RF, dependenciesObject = new PdfDictionary());}

    dependenciesObject.put(key, value.getBaseObject());
  }

  /**
    @see #getEmbeddedFile(PdfName)
  */
  private void setEmbeddedFile(
    PdfName key,
    EmbeddedFile value
    )
  {
    PdfDictionary embeddedFilesObject = (PdfDictionary)getBaseDataObject().get(PdfName.EF);
    if(embeddedFilesObject == null)
    {getBaseDataObject().put(PdfName.EF, embeddedFilesObject = new PdfDictionary());}

    embeddedFilesObject.put(key, value.getBaseObject());
  }

  /**
    @see #getPath(PdfName)
  */
  private void setPath(
    PdfName key,
    String value
    )
  {getBaseDataObject().put(key, new PdfString(value));}
  // <private>
  // </interface>
  // </dynamic>
  // </class>
}