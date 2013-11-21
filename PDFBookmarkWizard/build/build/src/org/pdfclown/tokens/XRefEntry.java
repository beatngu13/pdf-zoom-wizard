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

package org.pdfclown.tokens;

/**
  Cross-reference table entry [PDF:1.6:3.4.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 11/30/12
*/
public final class XRefEntry
  implements Cloneable
{
  // <class>
  // <classes>
  /**
    Cross-reference table entry usage [PDF:1.6:3.4.3].
  */
  public enum UsageEnum
  {
    /**
      Free entry.
    */
    Free,
    /**
      Ordinary (uncompressed) object entry.
    */
    InUse,
    /**
      Compressed object entry [PDF:1.6:3.4.6].
    */
    InUseCompressed
  }
  // </classes>

  // <static>
  // <fields>
  /**
    Unreusable generation [PDF:1.6:3.4.3].
  */
  public static final int GenerationUnreusable = 65535;

  /**
    Undefined offset.
  */
  public static final int UndefinedOffset = -1;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private int number;
  private int generation;
  private int offset;
  private int streamNumber;
  private UsageEnum usage;
  // </fields>

  // <constructors>
  /**
    Instantiates a new in-use ordinary (uncompressed) object entry.

    @param number Object number.
    @param generation Generation number.
  */
  public XRefEntry(
    int number,
    int generation
    )
  {this(number, generation, -1, UsageEnum.InUse);}

  /**
    Instantiates an original ordinary (uncompressed) object entry.

    @param number Object number.
    @param generation Generation number.
    @param offset Indirect-object byte offset within the serialized file (in-use entry),
      or the next free-object object number (free entry).
    @param usage Usage state.
  */
  public XRefEntry(
    int number,
    int generation,
    int offset,
    UsageEnum usage
    )
  {this(number, generation, offset, usage, -1);}

  /**
    Instantiates a compressed object entry.

    @param number Object number.
    @param offset Object index within its object stream.
    @param streamNumber Object number of the object stream in which this object is stored.
  */
  public XRefEntry(
    int number,
    int offset,
    int streamNumber
    )
  {this(number, 0, offset, UsageEnum.InUseCompressed, streamNumber);}

  private XRefEntry(
    int number,
    int generation,
    int offset,
    UsageEnum usage,
    int streamNumber
    )
  {
    this.number = number;
    this.generation = generation;
    this.offset = offset;
    this.usage = usage;
    this.streamNumber = streamNumber;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the generation number.
  */
  public int getGeneration(
    )
  {return generation;}

  /**
    Gets the object number.
  */
  public int getNumber(
    )
  {return number;}

  /**
    Gets its indirect-object byte offset within the serialized file (in-use entry),
    the next free-object object number (free entry) or the object index within its object stream (compressed entry).
  */
  public int getOffset(
    )
  {return offset;}

  /**
    Gets the object number of the object stream in which this object is stored [PDF:1.6:3.4.7],
    in case it is a {@link UsageEnum#InUseCompressed compressed} one.

    @return <code>-1</code> in case this is {@link UsageEnum#InUse not a compressed}-object entry.
  */
  public int getStreamNumber(
    )
  {return streamNumber;}

  /**
    Gets the usage state.
  */
  public UsageEnum getUsage(
    )
  {return usage;}

  public void setGeneration(
    int value
    )
  {generation = value;}

  public void setNumber(
    int value
    )
  {number = value;}

  public void setOffset(
    int value
    )
  {offset = value;}

  public void setStreamNumber(
    int value
    )
  {streamNumber = value;}

  /**
    @see #getUsage()
  */
  public void setUsage(
    UsageEnum value
    )
  {usage = value;}
  // </public>

  // <protected>
  @Override
  protected XRefEntry clone(
    ) throws CloneNotSupportedException
  {return (XRefEntry)super.clone();}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}