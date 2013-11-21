/*
  Copyright 2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util.parsers;

/**
  Exception thrown in case of unexpected condition while parsing.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.1, 04/25/11
*/
public class ParseException
  extends RuntimeException
{
  // <class>
  // <static>
  // <fields>
  private static final long serialVersionUID = 1L;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private final long position;
  // </fields>

  // <constructors>
  public ParseException(
    String message
    )
  {this(message, -1);}

  public ParseException(
    String message,
    long position
    )
  {
    super(message);

    this.position = position;
  }

  public ParseException(
    Throwable cause
    )
  {this(null, cause);}

  public ParseException(
    String message,
    Throwable cause
    )
  {this(message, cause, -1);}

  public ParseException(
    String message,
    Throwable cause,
    long position
    )
  {
    super(message, cause);

    this.position = position;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the offset where error happened.
  */
  public long getPosition(
    )
  {return position;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}