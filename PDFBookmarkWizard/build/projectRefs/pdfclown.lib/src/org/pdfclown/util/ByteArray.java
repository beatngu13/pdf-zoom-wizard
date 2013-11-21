/*
  Copyright 2009-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Arrays;

/**
  Byte array.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
/*
  NOTE: This class is useful when applied as map key.
*/
public final class ByteArray
{
  public final byte[] data; //TODO: yes, I know it's risky (temporary simplification)...

  public ByteArray(byte[] data)
  {this.data = Arrays.copyOf(data,data.length);}

  @Override
  public boolean equals(
    Object object
    )
  {
    return object instanceof ByteArray
      && Arrays.equals(data,((ByteArray)object).data);
  }

  @Override
  public int hashCode(
    )
  {return Arrays.hashCode(data);}

  @Override
  public String toString(
    )
  {
    StringBuilder builder = new StringBuilder("[");
    for(byte datum : data)
    {
      if(builder.length() > 1)
      {builder.append(",");}

      builder.append(datum & 0xFF);
    }
    builder.append("]");
    return builder.toString();
  }
}