/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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
  String utility.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 09/24/12
*/
public final class StringUtils
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static String join(
    char separator,
    String ...values
    )
  {
    StringBuilder builder = new  StringBuilder();
    for(String value : values)
    {
      if(builder.length() > 0)
      {builder.append(separator);}
      builder.append(value);
    }
    return builder.toString();
  }

  public static String repeat(
    String value,
    int count
    )
  {
    StringBuilder builder = new StringBuilder();
    for(int index = 0; index < count; index++)
    {builder.append(value);}
    return builder.toString();
  }
  // </public>
  // </interface>
  // </static>
  // </class>
}
