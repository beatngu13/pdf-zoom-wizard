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

package org.pdfclown.bytes;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.bytes.filters.Filter;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.util.ConvertUtils;

/**
  Byte buffer.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 01/29/12
*/
public final class Buffer
  implements IBuffer
{
  // <class>
  // <static>
  // <fields>
  /**
    Default buffer capacity.
  */
  private static final int DefaultCapacity = 1 << 8;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  /**
    Inner buffer where data are stored.
  */
  private byte[] data;
  /**
    Number of bytes actually used in the buffer.
  */
  private int length;
  /**
    Pointer position within the buffer.
  */
  private int position = 0;

  private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

  private boolean dirty;
  private List<IListener> listeners;
  // </fields>

  // <constructors>
  public Buffer(
    )
  {this(0);}

  public Buffer(
    int capacity
    )
  {
    if(capacity < 1)
    {capacity = DefaultCapacity;}

    this.data = new byte[capacity];
    this.length = 0;
  }

  public Buffer(
    java.io.BufferedReader dataStream
    )
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try
    {
      char[] buffer = new char[8192]; int bufferLength;
      while((bufferLength = dataStream.read(buffer, 0, buffer.length)) != -1)
      {
        for(int i = 0; i < bufferLength; i++)
        {outputStream.write((byte)buffer[i]);}
      }
    }
    catch(IOException e)
    {throw new RuntimeException(e);}

    this.data = outputStream.toByteArray();
    this.length = data.length;
  }

  public Buffer(
    InputStream dataStream
    )
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try
    {
      byte[] buffer = new byte[8192]; int bufferLength;
      while((bufferLength = dataStream.read(buffer, 0, buffer.length)) != -1)
      {outputStream.write(buffer, 0, bufferLength);}
    }
    catch(IOException e)
    {throw new RuntimeException(e);}

    this.data = outputStream.toByteArray();
    this.length = data.length;
  }

  public Buffer(
    byte[] data
    )
  {
    this.data = data;
    this.length = data.length;
  }

  public Buffer(
    byte[] data,
    ByteOrder byteOrder
    )
  {
    this.data = data;
    this.length = data.length;
    this.byteOrder = byteOrder;
  }
  // </constructors>

  // <interface>
  // <public>
  // <IBuffer>
  @Override
  public void addListener(
    IListener listener
    )
  {
    if(listeners == null)
    {listeners = new ArrayList<IBuffer.IListener>();}

    listeners.add(listener);
  }

  @Override
  public IBuffer append(
    byte data
    )
  {
    ensureCapacity(1);
    this.data[this.length++] = data;
    notifyChange();
    return this;
  }

  @Override
  public IBuffer append(
    byte[] data
    )
  {return append(data, 0, data.length);}

  @Override
  public IBuffer append(
    byte[] data,
    int offset,
    int length
    )
  {
    ensureCapacity(length);
    System.arraycopy(data, offset, this.data, this.length, length);
    this.length += length;
    notifyChange();
    return this;
  }

  @Override
  public IBuffer append(
    String data
    )
  {return append(Encoding.Pdf.encode(data));}

  @Override
  public IBuffer append(
    IInputStream data
    )
  {return append(data.toByteArray(), 0, (int)data.getLength());}

  @Override
  public Buffer clone(
    )
  {
    Buffer clone = new Buffer(getCapacity());
    clone.append(data);
    return clone;
  }

  @Override
  public void decode(
    Filter filter,
    PdfDictionary parameters
    )
  {
    data = filter.decode(data, 0, length, parameters);
    length = data.length;
  }

  @Override
  public void delete(
    int index,
    int length
    )
  {
    // Shift left the trailing data block to override the deleted data!
    System.arraycopy(this.data, index + length, this.data, index, this.length - (index + length));
    this.length -= length;
    notifyChange();
  }

  @Override
  public byte[] encode(
    Filter filter,
    PdfDictionary parameters
    )
  {return filter.encode(data, 0, length, parameters);}

  @Override
  public int getByte(
    int index
    )
  {return data[index];}

  @Override
  public byte[] getByteArray(
    int index,
    int length
    )
  {
    byte[] data = new byte[length];
    System.arraycopy(this.data, index, data, 0, length);
    return data;
  }

  @Override
  public String getString(
    int index,
    int length
    )
  {return Encoding.Pdf.decode(data, index, length);}

  @Override
  public int getCapacity(
    )
  {return data.length;}

  @Override
  public void insert(
    int index,
    byte[] data
    )
  {insert(index, data, 0, data.length);}

  @Override
  public void insert(
    int index,
    byte[] data,
    int offset,
    int length
    )
  {
    ensureCapacity(length);
    // Shift right the existing data block to make room for new data!
    System.arraycopy(this.data, index, this.data, index + length, this.length - index);
    // Insert additional data!
    System.arraycopy(data, offset, this.data, index, length);
    this.length += length;
    notifyChange();
  }

  @Override
  public void insert(
    int index,
    String data
    )
  {insert(index, Encoding.Pdf.encode(data));}

  @Override
  public void insert(
    int index,
    IInputStream data
    )
  {insert(index, data.toByteArray());}

  @Override
  public boolean removeListener(
    IListener listener
    )
  {
    if(listeners == null)
      return false;

    return listeners.remove(listener);
  }

  @Override
  public void replace(
    int index,
    byte[] data
    )
  {
    System.arraycopy(data, 0, this.data, index, data.length);
    notifyChange();
  }

  @Override
  public void replace(
    int index,
    byte[] data,
    int offset,
    int length
    )
  {
    System.arraycopy(data, offset, this.data, index, data.length);
    notifyChange();
  }

  @Override
  public void replace(
    int index,
    String data
    )
  {replace(index, Encoding.Pdf.encode(data));}

  @Override
  public void replace(
    int index,
    IInputStream data
    )
  {replace(index, data.toByteArray());}

  @Override
  public void setDirty(
    boolean value
    )
  {dirty = value;}

  @Override
  public void setLength(
    int value
    )
  {
    length = value;
    notifyChange();
  }

  @Override
  public void writeTo(
    IOutputStream stream
    )
  {stream.write(data, 0, length);}

  // <IInputStream>
  @Override
  public ByteOrder getByteOrder(
    )
  {return byteOrder;}

  @Override
  public long getPosition(
    )
  {return position;}

  /* int hashCode() uses inherited implementation. */

  @Override
  public boolean isDirty(
    )
  {return dirty;}

  @Override
  public void read(
    byte[] data
    )
  {read(data, 0, data.length);}

  @Override
  public void read(
    byte[] data,
    int offset,
    int length
    )
  {
    System.arraycopy(this.data, position, data, offset, length);
    position += length;
  }

  @Override
  public byte readByte(
    ) throws EOFException
  {
    if(position >= data.length)
      throw new EOFException();

    return data[position++];
  }

  @Override
  public int readInt(
    ) throws EOFException
  {
    int value = ConvertUtils.byteArrayToInt(data, position, byteOrder);
    position +=4;
    return value;
  }

  @Override
  public int readInt(
    int length
    ) throws EOFException
  {
    int value = ConvertUtils.byteArrayToNumber(data, position, length, byteOrder);
    position += length;
    return value;
  }

  @Override
  public String readLine(
    ) throws EOFException
  {
    if(position >= data.length)
      throw new EOFException();

    StringBuilder buffer = new StringBuilder();
    while(position < data.length)
    {
      int c = data[position++];
      if(c == '\r'
        || c == '\n')
        break;

      buffer.append((char)c);
    }
    return buffer.toString();
  }

  @Override
  public short readShort(
    ) throws EOFException
  {
    short value = (short)ConvertUtils.byteArrayToNumber(data, position, 2, byteOrder);
    position += 2;
    return value;
  }

  @Override
  public String readString(
    int length
    )
  {
    String data = Encoding.Pdf.decode(this.data, position, length);
    position += length;
    return data;
  }

  @Override
  public int readUnsignedByte(
    ) throws EOFException
  {
    if(position >= data.length)
      throw new EOFException();

    return (data[position++] & 0xFF);
  }

  @Override
  public int readUnsignedShort(
    ) throws EOFException
  {
  //TODO: harmonize byteorder semantics with C# version!!!
    try
    {
      if(byteOrder == ByteOrder.LITTLE_ENDIAN)
        return (data[position++] & 0xFF) | (data[position++] & 0xFF) << 8;
      else // ByteOrder.BIG_ENDIAN
        return (data[position++] & 0xFF) << 8 | (data[position++] & 0xFF);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {throw new EOFException();}
  }

  @Override
  public void seek(
    long position
    )
  {setPosition(position);}

  @Override
  public void setByteOrder(
    ByteOrder value
    )
  {byteOrder = value;}

  @Override
  public void setPosition(
    long value
    )
  {
    if(value < 0)
    {value = 0;}
    else if(value > data.length)
    {value = data.length;}

    position = (int)value;
  }

  @Override
  public void skip(
    long offset
    )
  {setPosition(position + offset);}

  // <IDataWrapper>
  @Override
  public byte[] toByteArray(
    )
  {
    byte[] data = new byte[this.length];
    System.arraycopy(this.data, 0, data, 0, this.length);
    return data;
  }
  // </IDataWrapper>

  // <IStream>
  @Override
  public long getLength(
    )
  {return length;}

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {}
  // </Closeable>
  // </IStream>
  // </IInputStream>
  // </IBuffer>

  // <IOutputStream>
  @Override
  public void write(
    byte[] data
    )
  {append(data);}

  @Override
  public void write(
    byte[] data,
    int offset,
    int length
    )
  {append(data, offset, length);}

  @Override
  public void write(
    String data
    )
  {append(data);}

  @Override
  public void write(
    IInputStream data
    )
  {append(data);}
  // </IOutputStream>
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
  /**
    Check whether the buffer capacity has sufficient room for adding data.
  */
  private void ensureCapacity(
    int additionalLength
    )
  {
    int minCapacity = this.length + additionalLength;
    // Is additional data within the buffer capacity?
    if(minCapacity <= this.data.length)
      return;

    // Additional data exceed buffer capacity.
    // Reallocate the buffer!
    byte[] data = new byte[
      Math.max(
        this.data.length << 1, // 1 order of magnitude greater than current capacity.
        minCapacity // Minimum capacity required.
        )
      ];
    System.arraycopy(this.data, 0, data, 0, this.length);
    this.data = data;
  }

  private void notifyChange(
    )
  {
    if(dirty || listeners == null)
      return;

    dirty = true;
    for(IListener listener : listeners)
    {listener.onChange(this);}
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}