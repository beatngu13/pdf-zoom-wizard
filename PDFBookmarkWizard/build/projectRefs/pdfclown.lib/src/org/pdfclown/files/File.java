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

package org.pdfclown.files;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;

import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.FileInputStream;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.bytes.OutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Document.Configuration.XRefModeEnum;
import org.pdfclown.objects.Cloner;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.tokens.Reader;
import org.pdfclown.tokens.Reader.FileInfo;
import org.pdfclown.tokens.Writer;
import org.pdfclown.tokens.XRefEntry;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.StringUtils;

/**
  PDF file representation.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.0
  @version 0.1.2, 12/28/12
*/
public final class File
  implements Closeable
{
  // <class>
  // <classes>
  /**
    File configuration.

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.1
  */
  public static final class Configuration
  {
    private DecimalFormat realFormat;

    private final File file;

    Configuration(
      File file
      )
    {this.file = file;}

    /**
      Gets the file associated with this configuration.
    */
    public File getFile(
      )
    {return file;}

    /**
      Gets the format applied to real number serialization.
    */
    public DecimalFormat getRealFormat(
      )
    {
      if(realFormat == null)
      {setRealFormat(5);}
      return realFormat;
    }

    /**
      @see #getRealFormat()
    */
    public void setRealFormat(
      DecimalFormat value
      )
    {realFormat = value;}

    /**
      @see #getRealFormat()
      @param decimalPlacesCount Number of digits in decimal places.
    */
    public void setRealFormat(
      int decimalPlacesCount
      )
    {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator('.');
      setRealFormat(new DecimalFormat("0." + StringUtils.repeat("#", decimalPlacesCount), symbols));
    }
  }

  private static final class ImplicitContainer
    extends PdfIndirectObject
  {
    public ImplicitContainer(
      File file,
      PdfDataObject dataObject
      )
    {super(file, dataObject, new XRefEntry(Integer.MIN_VALUE, Integer.MIN_VALUE));}
  }
  // </classes>

  // <static>
  // <fields>
  private static Random hashCodeGenerator = new Random();
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private final Configuration configuration = new Configuration(this);
  private final Document document;
  private final int hashCode = hashCodeGenerator.nextInt();
  private final IndirectObjects indirectObjects;
  private String path;
  private Reader reader;
  private final PdfDictionary trailer;
  private final Version version;

  private Cloner cloner;
  // </fields>

  // <constructors>
  public File(
    )
  {
    version = VersionEnum.PDF14.getVersion();
    trailer = prepareTrailer(new PdfDictionary());
    indirectObjects = new IndirectObjects(this, null);
    document = new Document(this);
  }

  public File(
    String path
    ) throws java.io.FileNotFoundException
  {
    this(
      new FileInputStream(
        new java.io.RandomAccessFile(path,"r")
        )
      );
    this.path = path;
  }

  public File(
    IInputStream stream
    )
  {
    reader = new Reader(stream, this);

    FileInfo info = reader.readInfo();
    version = info.getVersion();
    trailer = prepareTrailer(info.getTrailer());
    if(trailer.containsKey(PdfName.Encrypt)) // Encrypted file.
      throw new NotImplementedException("Encrypted files are currently not supported.");

    indirectObjects = new IndirectObjects(this, info.getXrefEntries());
    document = new Document(trailer.get(PdfName.Root));
    document.getConfiguration().setXrefMode(PdfName.XRef.equals(trailer.get(PdfName.Type)) ? XRefModeEnum.Compressed : XRefModeEnum.Plain);
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the default cloner.
  */
  public Cloner getCloner(
    )
  {
    if(cloner == null)
    {cloner = new Cloner(this);}

    return cloner;
  }

  /**
    Gets the file configuration.
  */
  public Configuration getConfiguration(
    )
  {return configuration;}

  /**
    Gets the high-level representation of the file content.
  */
  public Document getDocument(
    )
  {return document;}

  /**
    Gets the identifier of this file.
  */
  public FileIdentifier getID(
    )
  {return FileIdentifier.wrap(getTrailer().get(PdfName.ID));}

  /**
    Gets the indirect objects collection.
  */
  public IndirectObjects getIndirectObjects(
    )
  {return indirectObjects;}

  /**
    Gets the file path.
  */
  public String getPath(
    )
  {return path;}

  /**
    Gets the data reader backing this file.

    @return <code>null</code> in case of newly-created file.
  */
  public Reader getReader(
    )
  {return reader;}

  /**
    Gets the file trailer.
  */
  public PdfDictionary getTrailer(
    )
  {return trailer;}

  /**
    Gets the file header version [PDF:1.6:3.4.1].
    <p>This property represents just the original file version; to get the actual version,
    use the {@link org.pdfclown.documents.Document#getVersion() Document.getVersion} method.</p>
  */
  public Version getVersion(
    )
  {return version;}

  @Override
  public int hashCode(
    )
  {return hashCode;}

  /**
    Registers an <b>internal data object</b>.

    @since 0.0.4
  */
  public PdfReference register(
    PdfDataObject object
    )
  {return indirectObjects.add(object).getReference();}

  /**
    Serializes the file to the current file-system path using the {@link
    SerializationModeEnum#Standard standard serialization mode}.
  */
  public void save(
    ) throws IOException
  {save(SerializationModeEnum.Standard);}

  /**
    Serializes the file to the current file-system path.

    @param mode Serialization mode.
  */
  public void save(
    SerializationModeEnum mode
    ) throws IOException
  {
    if(!new java.io.File(path).exists())
      throw new FileNotFoundException("No valid source path available.");

    /*
      NOTE: The document file cannot be directly overwritten as it's locked for reading by the open
      stream; its update is therefore delayed to its disposal, when the temporary file will overwrite
      it (see close() method).
    */
    save(getTempPath(), mode);
  }

  /**
    Serializes the file to the specified file-system path.

    @param path Target path.
    @param mode Serialization mode.
  */
  public void save(
    String path,
    SerializationModeEnum mode
    ) throws IOException
  {
    save(
      new java.io.File(path),
      mode
      );
  }

  /**
    Serializes the file to the specified file-system file.

    @param file Target file.
    @param mode Serialization mode.
  */
  public void save(
    java.io.File file,
    SerializationModeEnum mode
    ) throws IOException
  {
    OutputStream outputStream;
    java.io.BufferedOutputStream baseOutputStream;
    try
    {
      file.createNewFile();
      baseOutputStream = new java.io.BufferedOutputStream(
        new java.io.FileOutputStream(file)
        );
      outputStream = new OutputStream(baseOutputStream);
    }
    catch(Exception e)
    {throw new IOException(file.getPath() + " file couldn't be created.",e);}
    try
    {
      save(
        outputStream,
        mode
        );
      baseOutputStream.flush();
      baseOutputStream.close();
    }
    catch(Exception e)
    {throw new IOException(file.getPath() + " file writing has failed.",e);}
  }

  /**
    Serializes the file to the specified stream.
    <p>It's caller responsibility to close the stream after this method ends.</p>

    @param stream Target stream.
    @param mode Serialization mode.
  */
  public void save(
    IOutputStream stream,
    SerializationModeEnum mode
    )
  {
    if(getReader() == null)
    {
      try
      {
        Package package_ = getClass().getPackage();
        getDocument().getInformation().setProducer(package_.getSpecificationTitle() + " " + package_.getSpecificationVersion());
      }
      catch(Exception e)
      {/* NOOP */}
    }

    Writer writer = Writer.get(this, stream);
    writer.write(mode);
  }

  /**
    @see #getCloner()
  */
  public void setCloner(
    Cloner value
    )
  {cloner = value;}

  /**
    @see #getPath()
  */
  public void setPath(
    String value
    )
  {path = value;}

  /**
    Unregisters an <b>internal object</b>.

    @since 0.0.5
  */
  public void unregister(
    PdfReference reference
    )
  {indirectObjects.remove(reference.getObjectNumber());}

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {
    if(reader != null)
    {
      reader.close();
      reader = null;

      /*
        NOTE: If the temporary file exists (see save() method), it must overwrite the document file.
      */
      java.io.File sourceFile = new java.io.File(getTempPath());
      if(sourceFile.exists())
      {
        java.io.File targetFile = new java.io.File(path);
        targetFile.delete();
        sourceFile.renameTo(targetFile);
      }
    }
  }
  // </Closeable>
  // </public>

  // <protected>
  @Override
  protected void finalize(
    ) throws Throwable
  {
    try
    {close();}
    finally
    {super.finalize();}
  }
  // </protected>

  // <private>
  private String getTempPath(
    )
  {return (path == null ? null : path + ".tmp");}

  private PdfDictionary prepareTrailer(
    PdfDictionary trailer
    )
  {return (PdfDictionary)new ImplicitContainer(this, trailer).getDataObject();}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}