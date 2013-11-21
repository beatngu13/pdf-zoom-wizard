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

/**
  Output stream interface.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public interface IOutputStream
  extends IStream
{
  /**
    Writes a byte array into the stream.

    @param data Byte array to write into the stream.
  */
  void write(
    byte[] data
    );

  /**
    Writes a byte range into the stream.

    @param data Byte array to write into the stream.
    @param offset Location in the byte array at which writing begins.
    @param length Number of bytes to write.
  */
  void write(
    byte[] data,
    int offset,
    int length
    );

  /**
    Writes a string into the stream.

    @param data String to write into the stream.
  */
  void write(
    String data
    );

  /**
    Writes an {@link IInputStream IInputStream} into the stream.

    @param data IInputStream to write into the stream.
  */
  void write(
    IInputStream data
    );
}