/*
  Copyright 2008-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Symbol;

/**
  Marked-content sequence [PDF:1.6:10.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF12)
public final class MarkedContent
  extends ContainerObject
{
  // <class>
  // <static>
  // <fields>
  public static final String EndOperator = EndMarkedContent.Operator;

  private static final byte[] EndChunk = Encoding.Pdf.encode(EndOperator + Symbol.LineFeed);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private BeginMarkedContent header;
  // </fields>

  // <constructors>
  public MarkedContent(
    BeginMarkedContent header
    )
  {this(header, new ArrayList<ContentObject>());}

  public MarkedContent(
    BeginMarkedContent header,
    List<ContentObject> objects
    )
  {
    super(objects);
    this.header = header;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets information about this marked-content sequence.
  */
  @Override
  public BeginMarkedContent getHeader(
    )
  {return header;}

  @Override
  public void setHeader(
    Operation value
    )
  {header = (BeginMarkedContent)value;}

  @Override
  public void writeTo(
    IOutputStream stream,
    Document context
    )
  {
    header.writeTo(stream, context);
    super.writeTo(stream, context);
    stream.write(EndChunk);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}