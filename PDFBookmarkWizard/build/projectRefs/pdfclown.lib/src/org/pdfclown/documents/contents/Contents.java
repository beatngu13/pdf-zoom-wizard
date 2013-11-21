/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.tokens.ContentParser;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.NotImplementedException;

/**
  Content stream [PDF:1.6:3.7.1].
  <p>During its loading, this content stream is parsed and its instructions
  are exposed as a list; in case of modifications, it's user responsability
  to call the {@link #flush()} method in order to serialize back the instructions
  into this content stream.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Contents
  extends PdfObjectWrapper<PdfDataObject>
  implements List<ContentObject>
{
  // <class>
  // <classes>
  /**
    Content stream wrapper.
  */
  private static class ContentStream
    implements IInputStream
  {
    private final PdfDataObject baseDataObject;

    private long basePosition;
    private IInputStream stream;
    private int streamIndex = -1;

    public ContentStream(
      PdfDataObject baseDataObject
      )
    {
      this.baseDataObject = baseDataObject;
      moveNextStream();
    }

    @Override
    public long getLength(
      )
    {
      if(baseDataObject instanceof PdfStream) // Single stream.
        return ((PdfStream)baseDataObject).getBody().getLength();
      else // Array of streams.
      {
        int length = 0;
        for(PdfDirectObject stream : (PdfArray)baseDataObject)
        {length += ((PdfStream)((PdfReference)stream).getDataObject()).getBody().getLength();}
        return length;
      }
    }

    @Override
    public void close(
      ) throws IOException
    {/* NOOP */}

    @Override
    public ByteOrder getByteOrder(
      )
    {return stream.getByteOrder();}

    @Override
    public long getPosition(
      )
    {return basePosition + stream.getPosition();}

    @Override
    public void read(
      byte[] data
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public void read(
      byte[] data,
      int offset,
      int length
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public byte readByte(
      ) throws EOFException
    {
      if((stream == null
        || stream.getPosition() >= stream.getLength())
        && !moveNextStream())
          throw new EOFException();

      return stream.readByte();
    }

    @Override
    public int readInt(
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public int readInt(
      int length
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public String readLine(
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public short readShort(
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public String readString(
      int length
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public int readUnsignedByte(
      ) throws EOFException
    {
      if((stream == null
        || stream.getPosition() >= stream.getLength())
        && !moveNextStream())
          throw new EOFException();

      return stream.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort(
      ) throws EOFException
    {throw new NotImplementedException();}

    @Override
    public void seek(
      long position
      )
    {
      while(true)
      {
        if(position < basePosition) //Before current stream.
        {
          if(!movePreviousStream())
            throw new IllegalArgumentException("The 'position' argument is lower than acceptable.");
        }
        else if(position > basePosition + stream.getLength()) // After current stream.
        {
          if(!moveNextStream())
            throw new IllegalArgumentException("The 'position' argument is higher than acceptable.");
        }
        else // At current stream.
        {
          stream.seek(position - basePosition);
          break;
        }
      }
    }

    @Override
    public void setByteOrder(
      ByteOrder value
      )
    {throw new UnsupportedOperationException();}

    @Override
    public void setPosition(
      long value
      )
    {seek(value);}

    @Override
    public void skip(
      long offset
      )
    {
      while(true)
      {
        long position = stream.getPosition() + offset;
        if(position < 0) //Before current stream.
        {
          offset += stream.getPosition();
          if(!movePreviousStream())
            throw new IllegalArgumentException("The 'offset' argument is lower than acceptable.");

          stream.setPosition(stream.getLength());
        }
        else if(position > stream.getLength()) // After current stream.
        {
          offset -= (stream.getLength() - stream.getPosition());
          if(!moveNextStream())
            throw new IllegalArgumentException("The 'offset' argument is higher than acceptable.");
        }
        else // At current stream.
        {
          stream.seek(position);
          break;
        }
      }
    }

    @Override
    public byte[] toByteArray(
      )
    {throw new NotImplementedException();}

    private boolean moveNextStream(
      )
    {
      // Is the content stream just a single stream?
      /*
        NOTE: A content stream may be made up of multiple streams [PDF:1.6:3.6.2].
      */
      if(baseDataObject instanceof PdfStream) // Single stream.
      {
        if(streamIndex < 1)
        {
          streamIndex++;

          basePosition = (streamIndex == 0
            ? 0
            : basePosition + stream.getLength());

          stream = (streamIndex < 1
            ? ((PdfStream)baseDataObject).getBody()
            : null);
        }
      }
      else // Multiple streams.
      {
        PdfArray streams = (PdfArray)baseDataObject;
        if(streamIndex < streams.size())
        {
          streamIndex++;

          basePosition = (streamIndex == 0
            ? 0
            : basePosition + stream.getLength());

          stream = (streamIndex < streams.size()
            ? ((PdfStream)streams.resolve(streamIndex)).getBody()
            : null);
        }
      }
      if(stream == null)
        return false;

      stream.setPosition(0);
      return true;
    }

    private boolean movePreviousStream(
      )
    {
      if(streamIndex == 0)
      {
        streamIndex--;
        stream = null;
      }
      if(streamIndex == -1)
        return false;

      streamIndex--;
      /* NOTE: A content stream may be made up of multiple streams [PDF:1.6:3.6.2]. */
      // Is the content stream just a single stream?
      if(baseDataObject instanceof PdfStream) // Single stream.
      {
        stream = ((PdfStream)baseDataObject).getBody();
        basePosition = 0;
      }
      else // Array of streams.
      {
        PdfArray streams = (PdfArray)baseDataObject;

        stream = ((PdfStream)((PdfReference)streams.get(streamIndex)).getDataObject()).getBody();
        basePosition -= stream.getLength();
      }

      return true;
    }
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static Contents wrap(
    PdfDirectObject baseObject,
    IContentContext contentContext
    )
  {return baseObject != null ? new Contents(baseObject, contentContext) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private List<ContentObject> items;

  private final IContentContext contentContext;
  // </fields>

  // <constructors>
  private Contents(
    PdfDirectObject baseObject,
    IContentContext contentContext
    )
  {
    super(baseObject);

    this.contentContext = contentContext;
    load();
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Contents clone(
    Document context
    )
  {throw new UnsupportedOperationException();}

  /**
    Serializes the contents into the content stream.
  */
  public void flush(
    )
  {
    PdfStream stream;
    PdfDataObject baseDataObject = getBaseDataObject();
    // Are contents just a single stream object?
    if(baseDataObject instanceof PdfStream) // Single stream.
    {stream = (PdfStream)baseDataObject;}
    else // Array of streams.
    {
      PdfArray streams = (PdfArray)baseDataObject;
      // No stream available?
      if(streams.isEmpty()) // No stream.
      {
        // Add first stream!
        stream = new PdfStream();
        streams.add( // Inserts the new stream into the content stream.
          getFile().register(stream) // Inserts the new stream into the file.
          );
      }
      else // Streams exist.
      {
        // Eliminating exceeding streams...
        /*
          NOTE: Applications that consume or produce PDF files are not required to preserve
          the existing structure of the Contents array [PDF:1.6:3.6.2].
        */
        while(streams.size() > 1)
        {
          getFile().unregister( // Removes the exceeding stream from the file.
            (PdfReference)streams.remove(1) // Removes the exceeding stream from the content stream.
            );
        }
        stream = (PdfStream)streams.resolve(0);
      }
    }

    // Get the stream buffer!
    IBuffer buffer = stream.getBody();
    // Delete old contents from the stream buffer!
    buffer.setLength(0);
    // Serializing the new contents into the stream buffer...
    Document context = getDocument();
    for(ContentObject item : items)
    {item.writeTo(buffer, context);}
  }

  public IContentContext getContentContext(
    )
  {return contentContext;}

  // <List>
  @Override
  public void add(
    int index,
    ContentObject content
    )
  {items.add(index,content);}

  @Override
  public boolean addAll(
    int index,
    Collection<? extends ContentObject> contents
    )
  {return items.addAll(index,contents);}

  @Override
  public ContentObject get(
    int index
    )
  {return items.get(index);}

  @Override
  public int indexOf(
    Object content
    )
  {return items.indexOf(content);}

  @Override
  public int lastIndexOf(
    Object content
    )
  {return items.lastIndexOf(content);}

  @Override
  public ListIterator<ContentObject> listIterator(
    )
  {return items.listIterator();}

  @Override
  public ListIterator<ContentObject> listIterator(
    int index
    )
  {return items.listIterator(index);}

  @Override
  public ContentObject remove(
    int index
    )
  {return items.remove(index);}

  @Override
  public ContentObject set(
    int index,
    ContentObject content
    )
  {return items.set(index,content);}

  @Override
  public List<ContentObject> subList(
    int fromIndex,
    int toIndex
    )
  {return items.subList(fromIndex,toIndex);}

  // <Collection>
  @Override
  public boolean add(
    ContentObject content
    )
  {return items.add(content);}

  @Override
  public boolean addAll(
    Collection<? extends ContentObject> contents
    )
  {return items.addAll(contents);}

  @Override
  public void clear(
    )
  {items.clear();}

  @Override
  public boolean contains(
    Object content
    )
  {return items.contains(content);}

  @Override
  public boolean containsAll(
    Collection<?> contents
    )
  {return items.containsAll(contents);}

  @Override
  public boolean equals(
    Object object
    )
  {throw new NotImplementedException();}

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {return items.isEmpty();}

  @Override
  public boolean remove(
    Object content
    )
  {return items.remove(content);}

  @Override
  public boolean removeAll(
    Collection<?> contents
    )
  {return items.removeAll(contents);}

  @Override
  public boolean retainAll(
    Collection<?> contents
    )
  {return items.retainAll(contents);}

  @Override
  public int size(
    )
  {return items.size();}

  @Override
  public Object[] toArray(
    )
  {return items.toArray();}

  @Override
  public <T> T[] toArray(
    T[] contents
    )
  {return items.toArray(contents);}

  // <Iterable>
  @Override
  public Iterator<ContentObject> iterator(
    )
  {return items.iterator();}
  // </Iterable>
  // </Collection>
  // </List>
  // </public>

  // <private>
  private void load(
    )
  {
    @SuppressWarnings("resource")
    ContentParser parser = new ContentParser(new ContentStream(getBaseDataObject()));
    items = parser.parseContentObjects();
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}