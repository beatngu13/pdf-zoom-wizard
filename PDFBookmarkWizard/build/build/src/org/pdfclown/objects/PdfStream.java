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

package org.pdfclown.objects;

import java.io.IOException;
import java.util.Iterator;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.bytes.filters.Filter;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.files.IFileResource;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.Symbol;

/**
  PDF stream object [PDF:1.6:3.2.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
public class PdfStream
  extends PdfDataObject
  implements IFileResource
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginStreamBodyChunk = Encoding.Pdf.encode(Symbol.LineFeed + Keyword.BeginStream + Symbol.LineFeed);
  private static final byte[] EndStreamBodyChunk = Encoding.Pdf.encode(Symbol.LineFeed + Keyword.EndStream);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  IBuffer body;
  PdfDictionary header;

  private PdfObject parent;
  private boolean updateable = true;
  private boolean updated;
  private boolean virtual;

  /**
    Indicates whether {@link #body} has already been resolved and therefore contains the actual
    stream data.
  */
  private boolean bodyResolved;
  // </fields>

  // <constructors>
  public PdfStream(
    )
  {
    this(
      new PdfDictionary(),
      new Buffer()
      );
  }

  public PdfStream(
    PdfDictionary header
    )
  {
    this(
      header,
      new Buffer()
      );
  }

  public PdfStream(
    IBuffer body
    )
  {
    this(
      new PdfDictionary(),
      body
      );
  }

  public PdfStream(
    PdfDictionary header,
    IBuffer body
    )
  {
    this.header = (PdfDictionary)include(header);

    this.body = body;
    body.setDirty(false);
    body.addListener(new IBuffer.IListener()
    {
      @Override
      public void onChange(
        IBuffer buffer
        )
      {update();}
    });
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  @Override
  public PdfStream clone(
    File context
    )
  {return (PdfStream)super.clone(context);}

  /**
    Gets the decoded stream body.
  */
  public IBuffer getBody(
    )
  {
    /*
      NOTE: Encoding filters are removed by default because they belong to a lower layer (token
      layer), so that it's appropriate and consistent to transparently keep the object layer unaware
      of such a facility.
    */
    return getBody(true);
  }

  /**
    Gets the stream body.

    @param decode Defines whether the body has to be decoded.
  */
  public IBuffer getBody(
    boolean decode
    )
  {
    if(!bodyResolved)
    {
      /*
        NOTE: In case of stream data from external file, a copy to the local buffer has to be done.
      */
      FileSpecification<?> dataFile = getDataFile();
      if(dataFile != null)
      {
        setUpdateable(false);
        body.setLength(0);
        body.write(dataFile.getInputStream());
        body.setDirty(false);
        setUpdateable(true);
      }
      bodyResolved = true;
    }
    if(decode)
    {
      PdfDataObject filter = getFilter();
      if(filter != null) // Stream encoded.
      {
        header.setUpdateable(false);
        PdfDataObject parameters = getParameters();
        if(filter instanceof PdfName) // Single filter.
        {
          body.decode(
            Filter.get((PdfName)filter),
            (PdfDictionary)parameters
            );
        }
        else // Multiple filters.
        {
          Iterator<PdfDirectObject> filterIterator = ((PdfArray)filter).iterator();
          Iterator<PdfDirectObject> parametersIterator = (parameters != null ? ((PdfArray)parameters).iterator() : null);
          while(filterIterator.hasNext())
          {
            body.decode(
              Filter.get((PdfName)resolve(filterIterator.next())),
              (PdfDictionary)(parametersIterator != null ? resolve(parametersIterator.next()) : null)
              );
          }
        }
        setFilter(null); // The stream is free from encodings.
        header.setUpdateable(true);
      }
    }
    return body;
  }

  public PdfDirectObject getFilter(
    )
  {
    return (PdfDirectObject)(header.get(PdfName.F) == null
      ? header.resolve(PdfName.Filter)
      : header.resolve(PdfName.FFilter));
  }

  /**
    Gets the stream header.
  */
  public PdfDictionary getHeader(
    )
  {return header;}

  public PdfDirectObject getParameters(
    )
  {
    return (PdfDirectObject)(header.get(PdfName.F) == null
      ? header.resolve(PdfName.DecodeParms)
      : header.resolve(PdfName.FDecodeParms));
  }

  @Override
  public PdfObject getParent(
    )
  {return parent;}

  @Override
  public boolean isUpdateable(
    )
  {return updateable;}

  @Override
  public boolean isUpdated(
    )
  {return updated;}

  /**
    @param preserve Indicates whether the data from the old data source substitutes the new one.
      This way data can be imported to/exported from local or preserved in case of external file
      location changed.
    @see #setDataFile(FileSpecification)
  */
  public void setDataFile(
    FileSpecification<?> value,
    boolean preserve
    )
  {
    /*
      NOTE: If preserve argument is set to true, body's dirtiness MUST be forced in order to ensure
      data serialization to the new external location.

      Old data source | New data source | preserve | Action
      ----------------------------------------------------------------------------------------------
      local           | not null        | false     | A. Substitute local with new file.
      local           | not null        | true      | B. Export local to new file.
      external        | not null        | false     | C. Substitute old file with new file.
      external        | not null        | true      | D. Copy old file data to new file.
      local           | null            | (any)     | E. No action.
      external        | null            | false     | F. Empty local.
      external        | null            | true      | G. Import old file to local.
      ----------------------------------------------------------------------------------------------
    */
    FileSpecification<?> oldDataFile = getDataFile();
    PdfDirectObject dataFileObject = (value != null ? value.getBaseObject() : null);
    if(value != null)
    {
      if(preserve)
      {
        if(oldDataFile != null) // Case D (copy old file data to new file).
        {
          if(!bodyResolved)
          {
            // Transfer old file data to local!
            getBody(false); // Ensures that external data is loaded as-is into the local buffer.
          }
        }
        else // Case B (export local to new file).
        {
          // Transfer local settings to file!
          header.put(PdfName.FFilter, header.remove(PdfName.Filter));
          header.put(PdfName.FDecodeParms, header.remove(PdfName.DecodeParms));
          // Ensure local data represents actual data (otherwise it would be substituted by resolved file data)!
          bodyResolved = true;
        }
        // Ensure local data has to be serialized to new file!
        body.setDirty(true);
      }
      else // Case A/C (substitute local/old file with new file).
      {
        // Dismiss local/old file data!
        body.setLength(0);
        // Dismiss local/old file settings!
        setFilter(null);
        setParameters(null);
        // Ensure local data has to be loaded from new file!
        bodyResolved = false;
      }
    }
    else
    {
      if(oldDataFile != null)
      {
        if(preserve) // Case G (import old file to local).
        {
          // Transfer old file data to local!
          getBody(false); // Ensures that external data is loaded as-is into the local buffer.
          // Transfer old file settings to local!
          header.put(PdfName.Filter, header.remove(PdfName.FFilter));
          header.put(PdfName.DecodeParms, header.remove(PdfName.FDecodeParms));
        }
        else // Case F (empty local).
        {
          // Dismiss old file data!
          body.setLength(0);
          // Dismiss old file settings!
          setFilter(null);
          setParameters(null);
          // Ensure local data represents actual data (otherwise it would be substituted by resolved file data)!
          bodyResolved = true;
        }
      }
      else // E (no action).
      { /* NOOP */ }
    }
    header.put(PdfName.F, dataFileObject);
  }

  @Override
  public void setUpdateable(
    boolean value
    )
  {updateable = value;}

  @Override
  public PdfStream swap(
    PdfObject other
    )
  {
    PdfStream otherStream = (PdfStream)other;
    PdfDictionary otherHeader = otherStream.header;
    IBuffer otherBody = otherStream.body;
    // Update the other!
    otherStream.header = this.header;
    otherStream.body = this.body;
    otherStream.update();
    // Update this one!
    this.header = otherHeader;
    this.body = otherBody;
    this.update();
    return this;
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {
    /*
      NOTE: The header is temporarily tweaked to accommodate serialization settings.
    */
    header.setUpdateable(false);

    byte[] bodyData;
    {
      boolean bodyUnencoded;
      {
        FileSpecification<?> dataFile = getDataFile();
        /*
          NOTE: In case of external file, the body buffer has to be saved back only if the file was
          actually resolved (that is brought into the body buffer) and modified.
        */
        boolean encodeBody = (dataFile == null || (bodyResolved && body.isDirty()));
        if(encodeBody)
        {
          PdfDirectObject filterObject = getFilter();
          if(filterObject == null) // Unencoded body.
          {
            /*
              NOTE: Header entries related to stream body encoding are temporary, instrumental to
              the current serialization process only.
            */
            bodyUnencoded = true;

            // Set the filter to apply!
            filterObject = PdfName.FlateDecode; // zlib/deflate filter.
            // Get encoded body data applying the filter to the stream!
            bodyData = body.encode(Filter.get((PdfName)filterObject), null);
            // Set 'Filter' entry!
            setFilter(filterObject);
          }
          else // Encoded body.
          {
            bodyUnencoded = false;

            // Get encoded body data!
            bodyData = body.toByteArray();
          }

          if(dataFile != null)
          {
            /*
              NOTE: In case of external file, body data has to be serialized there, leaving empty
              its representation within this stream.
            */
            try
            {
              IOutputStream dataFileOutputStream = dataFile.getOutputStream();
              dataFileOutputStream.write(bodyData);
              dataFileOutputStream.close();
            }
            catch(IOException e)
            {throw new RuntimeException("Data writing into " + dataFile.getPath() + " failed.", e);}
            // Local serialization is empty!
            bodyData = new byte[]{};
          }
        }
        else
        {
          bodyUnencoded = false;
          bodyData = new byte[]{};
        }
      }

      // Set the encoded data length!
      header.put(PdfName.Length, PdfInteger.get(bodyData.length));

      // 1. Header.
      header.writeTo(stream, context);

      if(bodyUnencoded)
      {
        // Restore actual header entries!
        header.put(PdfName.Length, PdfInteger.get((int)body.getLength()));
        setFilter(null);
      }
    }

    // 2. Body.
    stream.write(BeginStreamBodyChunk);
    stream.write(bodyData);
    stream.write(EndStreamBodyChunk);

    header.setUpdateable(true);
  }

  // <IFileResource>
  @Override
  @PDF(VersionEnum.PDF12)
  public FileSpecification<?> getDataFile(
    )
  {return FileSpecification.wrap(header.get(PdfName.F));}

  @Override
  public void setDataFile(
    FileSpecification<?> value
    )
  {setDataFile(value, false);}
  // </IFileResource>
  // </public>

  // <protected>
  @Override
  protected boolean isVirtual(
    )
  {return virtual;}

  /**
    @see #getFilter()
  */
  protected void setFilter(
    PdfDirectObject value
    )
  {
    header.put(
      header.get(PdfName.F) == null
        ? PdfName.Filter
        : PdfName.FFilter,
      value
      );
  }

  /**
    @see #getParameters()
  */
  protected void setParameters(
    PdfDirectObject value
    )
  {
    header.put(
      header.get(PdfName.F) == null
        ? PdfName.DecodeParms
        : PdfName.FDecodeParms,
      value
      );
  }

  @Override
  protected void setUpdated(
    boolean value
    )
  {updated = value;}

  @Override
  protected void setVirtual(
    boolean value
    )
  {virtual = value;}
  // </protected>

  // <internal>
  @Override
  void setParent(
    PdfObject value
    )
  {parent = value;}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}