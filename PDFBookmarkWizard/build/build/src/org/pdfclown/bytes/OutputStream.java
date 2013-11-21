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

import org.pdfclown.tokens.Encoding;

/**
  Output stream default implementation.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public final class OutputStream
  implements IOutputStream
{
  // <class>
  // <dynamic>
  // <fields>
  private java.io.OutputStream stream;

  private int length;
  // </fields>

  // <constructors>
  public OutputStream(
    java.io.OutputStream stream
    )
  {this.stream = stream;}
  // </constructors>

  // <interface>
  // <public>
  // <IOutputStream>
  @Override
  public void write(
    byte[] data
    )
  {
    try
    {stream.write(data);}
    catch(IOException e)
    {throw new RuntimeException(e);}

    length += data.length;
  }

  @Override
  public void write(
    byte[] data,
    int offset,
    int length
    )
  {
    try
    {stream.write(data,offset,length);}
    catch(IOException e)
    {throw new RuntimeException(e);}

    this.length += length;
  }

  @Override
  public void write(
    String data
    )
  {
    try
    {stream.write(Encoding.Pdf.encode(data));}
    catch(IOException e)
    {throw new RuntimeException(e);}

    length += data.length();
  }

  @Override
  public void write(
    IInputStream data
    )
  {
    try
    {
      // TODO:IMPL bufferize!!!
      byte[] baseData = new byte[(int)data.getLength()];
      // Force the source pointer to the BOF (as we must copy the entire content)!
      data.seek(0);
      // Read source content!
      data.read(baseData);
      // Write target content!
      write(baseData);
    }
    catch(EOFException e)
    {throw new RuntimeException(e);}
  }

  // <IStream>
  @Override
  public long getLength(
    )
  {return length;}

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {
    if(stream != null)
    {
      stream.close();
      stream = null;
    }
  }
  // </Closeable>
  // </IStream>
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
  // </interface>
  // </dynamic>
  // </class>
}