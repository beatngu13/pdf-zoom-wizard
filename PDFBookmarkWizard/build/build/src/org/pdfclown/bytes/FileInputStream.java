/*
  Copyright 2006-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import org.pdfclown.tokens.Encoding;
import org.pdfclown.util.ConvertUtils;

/**
  File stream.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public final class FileInputStream
  implements IInputStream
{
  // <class>
  // <dynamic>
  // <fields>
  private RandomAccessFile file;
  // </fields>

  // <constructors>
  public FileInputStream(
    RandomAccessFile file
    )
  {this.file = file;}
  // </constructors>

  // <interface>
  // <public>
  // <IInputStream>
  @Override
  public ByteOrder getByteOrder(
    )
  {return ByteOrder.BIG_ENDIAN;}

  @Override
  public long getPosition(
    )
  {
    try
    {return file.getFilePointer();}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public int hashCode(
    )
  {return file.hashCode();}

  @Override
  public void read(
    byte[] data
    ) throws EOFException
  {
    try
    {file.readFully(data);}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public void read(
    byte[] data,
    int offset,
    int length
    ) throws EOFException
  {
    try
    {file.readFully(data,offset,length);}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public byte readByte(
    ) throws EOFException
  {
    try
    {return file.readByte();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public int readInt(
    ) throws EOFException
  {
    try
    {return file.readInt();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public int readInt(
    int length
    ) throws EOFException
  {
    byte[] data = new byte[length];
    try
    {file.readFully(data,0,length);}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
    return ConvertUtils.byteArrayToNumber(data,0,length,getByteOrder());
  }

  @Override
  public String readLine(
    ) throws EOFException
  {
    try
    {return file.readLine();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public short readShort(
    ) throws EOFException
  {
    try
    {return file.readShort();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public String readString(
    int length
    ) throws EOFException
  {
    byte[] data = new byte[length];
    try
    {
      file.readFully(data);
      return Encoding.Pdf.decode(data, 0, length);
    }
    catch(EOFException e)
    {throw e;}
    catch(Exception e)
    {throw new RuntimeException(e);}
  }

  @Override
  public int readUnsignedByte(
    ) throws EOFException
  {
    try
    {return file.readUnsignedByte();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public int readUnsignedShort(
    ) throws EOFException
  {
    try
    {return file.readUnsignedShort();}
    catch(EOFException e)
    {throw e;}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public void seek(
    long offset
    )
  {
    try
    {file.seek(offset);}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public void setByteOrder(
    ByteOrder value
    )
  {/* TODO */}

  @Override
  public void setPosition(
    long value
    )
  {
    try
    {file.seek(value);}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  @Override
  public void skip(
    long offset
    )
  {
    try
    {file.seek(file.getFilePointer() + offset);}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  // <IDataWrapper>
  @Override
  public byte[] toByteArray(
    )
  {
    byte[] data = null;
    try
    {
      file.seek(0);
      data = new byte[(int)file.length()];
      file.readFully(data);
    }
    catch(IOException e)
    {throw new RuntimeException(e);}
    return data;
  }
  // </IDataWrapper>

  // <IStream>
  @Override
  public long getLength(
    )
  {
    try
    {return file.length();}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {
    if(file != null)
    {
      file.close();
      file = null;
    }
  }
  // </Closeable>
  // </IStream>
  // </IInputStream>
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
  // </interface>
  // </dynamic>
  // </class>
}