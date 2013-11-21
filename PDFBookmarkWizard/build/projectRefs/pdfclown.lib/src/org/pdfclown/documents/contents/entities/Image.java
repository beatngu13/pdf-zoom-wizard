/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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
import java.io.FileNotFoundException;

import org.pdfclown.bytes.FileInputStream;
import org.pdfclown.bytes.IInputStream;

/**
  Abstract image object [PDF:1.6:4.8].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 04/25/11
*/
public abstract class Image
  extends Entity
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Image get(
    String path
    )
  {
    try
    {
      return get(
        new FileInputStream(
          new java.io.RandomAccessFile(path,"r")
          )
        );
    }
    catch(FileNotFoundException e)
    {throw new RuntimeException(e);}
  }

  public static Image get(
    java.io.File file
    )
  {return get(file.getPath());}

  public static Image get(
    IInputStream stream
    )
  {
    try
    {
      // Get the format identifier!
      byte[] formatMarkerBytes = new byte[2];
      stream.read(formatMarkerBytes);

      // Is JPEG?
      /*
        NOTE: JPEG files are identified by a SOI (Start Of Image) marker [ISO 10918-1].
      */
      if(formatMarkerBytes[0] == (byte)0xFF
        && formatMarkerBytes[1] == (byte)0xD8) // JPEG.
      {return new JpegImage(stream);}
      else // Unknown.
      {return null;}
    }
    catch(EOFException e)
    {throw new RuntimeException(e);}
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private int bitsPerComponent;
  private int height;
  private int width;

  private final IInputStream stream;
  // </fields>

  // <constructors>
  protected Image(
    IInputStream stream
    )
  {this.stream = stream;}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the number of bits per color component [PDF:1.6:4.8.2].
  */
  public int getBitsPerComponent(
    )
  {return bitsPerComponent;}

  /**
    Gets the height of the image in samples [PDF:1.6:4.8.2].
  */
  public int getHeight(
    )
  {return height;}

  /**
    Gets the width of the image in samples [PDF:1.6:4.8.2].
  */
  public int getWidth(
    )
  {return width;}
  // </public>

  // <protected>
  /**
    Gets the underlying stream.
  */
  protected IInputStream getStream(
    )
  {return stream;}

  /**
    Sets the number of bits per color component [PDF:1.6:4.8.2].
  */
  protected void setBitsPerComponent(
    int value
    )
  {bitsPerComponent = value;}

  /**
    Sets the height of the image in samples [PDF:1.6:4.8.2].
  */
  protected void setHeight(
    int value
    )
  {height = value;}

  /**
    Sets the width of the image in samples [PDF:1.6:4.8.2].
  */
  protected void setWidth(
    int value
    )
  {width = value;}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}