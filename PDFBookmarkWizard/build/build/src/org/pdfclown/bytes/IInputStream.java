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
import java.nio.ByteOrder;

import org.pdfclown.util.IDataWrapper;

/**
  Input stream.
  <p>Its pivotal concept is the access pointer.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public interface IInputStream
  extends IStream,
    IDataWrapper
{
  /**
    Gets the byte order.
  */
  ByteOrder getByteOrder(
    );

  /**
    Gets the pointer position.
  */
  long getPosition(
    );

  /**
    Gets the hash representation of the sequence.
  */
  @Override
  int hashCode(
    );

  /**
    Reads a sequence of bytes.
    <p>This operation causes the stream pointer to advance after the read data.</p>

    @param data Target byte array.
  */
  void read(
    byte[] data
    ) throws EOFException;

  /**
    Reads a sequence of bytes.
    <p>This operation causes the stream pointer to advance after the read data.</p>

    @param data Target byte array.
    @param offset Location in the byte array at which storing begins.
    @param length Number of bytes to read.
  */
  void read(
    byte[] data,
    int offset,
    int length
    ) throws EOFException;

  /**
    Reads a byte.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  byte readByte(
    ) throws EOFException;

  /**
    Reads an integer.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  int readInt(
    ) throws EOFException;

  /**
    Reads a variable-length integer.
    <p>This operation causes the stream pointer to advance after the read data.</p>

    @param length Number of bytes to read.
  */
  int readInt(
    int length
    ) throws EOFException;

  /**
    Reads the next line of text.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  String readLine(
    ) throws EOFException;

  /**
    Reads a short integer.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  short readShort(
    ) throws EOFException;

  /**
    Reads a string.
    <p>This operation causes the stream pointer to advance after the read data.</p>

    @param length Number of bytes to read.
  */
  String readString(
    int length
    ) throws EOFException;

  /**
    Reads an unsigned byte integer.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  int readUnsignedByte(
    ) throws EOFException;

  /**
    Reads an unsigned short integer.
    <p>This operation causes the stream pointer to advance after the read data.</p>
  */
  int readUnsignedShort(
    ) throws EOFException;

  /**
    Sets the pointer absolute position.
  */
  void seek(
    long position
    );

  /**
    Sets the byte order.
  */
  void setByteOrder(
    ByteOrder value
    );

  /**
    Sets the pointer position.
  */
  void setPosition(
    long value
    );

  /**
    Sets the pointer relative position.
  */
  void skip(
    long offset
    );
}