/*
  Copyright 2007-2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util;

/**
  Exception thrown to indicate that a block of code has not been implemented yet.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.3
  @version 0.1.0
*/
public final class NotImplementedException
  extends UnsupportedOperationException
{
  // <class>
  // <static>
  // <fields>
  private static final long serialVersionUID = 1L;
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public NotImplementedException(
    )
  {this(null,null);}

  public NotImplementedException(
    String message
    )
  {this(message,null);}

  public NotImplementedException(
    String message,
    Throwable cause
    )
  {
    super(
      message == null ?
        "There's work for you! You reached a code block that hasn't been implemented yet."
        : message,
      cause
      );
  }
  // </constructors>
  // </dynamic>
  // </class>
}