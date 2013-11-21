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

package org.pdfclown.objects;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;

/**
  Abstract PDF direct object.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/01/11
*/
public abstract class PdfDirectObject
  extends PdfDataObject
  implements Comparable<PdfDirectObject>
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] NullChunk = Encoding.Pdf.encode(Keyword.Null);
  // </fields>

  // <interface>
  // <internal>
  /**
    Ensures that the given direct object is properly represented as string.
    <p>This method is useful to force null pointers to be expressed as PDF null objects.</p>
  */
  static String toString(
    PdfDirectObject object
    )
  {return (object == null ? Keyword.Null : object.toString());}

  /**
    Ensures that the given direct object is properly serialized.
    <p>This method is useful to force null pointers to be expressed as PDF null objects.</p>
  */
  static void writeTo(
    IOutputStream stream,
    File context,
    PdfDirectObject object
    )
  {
    if(object == null)
    {stream.write(NullChunk);}
    else
    {object.writeTo(stream, context);}
  }
  // </internal>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected PdfDirectObject(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  // <Comparable>
  @Override
  public abstract int compareTo(
    PdfDirectObject obj
    );
  // </Comparable>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}