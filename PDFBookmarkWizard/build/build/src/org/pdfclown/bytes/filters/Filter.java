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

package org.pdfclown.bytes.filters;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  Abstract filter [PDF:1.6:3.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.2
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public abstract class Filter
{
  // <class>
  // <static>
  // <fields>
  private static final Filter ASCII85Filter = new ASCII85Filter();
  private static final Filter FlateDecode = new FlateFilter();
  // </fields>

  // <interface>
  // <public>
  /**
    Gets a specific filter object.

    @param name Name of the requested filter.
    @return Filter object associated to the name.
  */
  public static Filter get(
    PdfName name
    )
  {
    /*
      NOTE: This is a factory singleton method for any filter-derived object.
    */
    if(name == null)
      return null;

    if(name.equals(PdfName.FlateDecode)
      || name.equals(PdfName.Fl))
      return FlateDecode;
    else if(name.equals(PdfName.LZWDecode)
      || name.equals(PdfName.LZW))
      throw new NotImplementedException("LZWDecode");
    else if(name.equals(PdfName.ASCIIHexDecode)
      || name.equals(PdfName.AHx))
      throw new NotImplementedException("ASCIIHexDecode");
    else if(name.equals(PdfName.ASCII85Decode)
      || name.equals(PdfName.A85))
      return ASCII85Filter;
    else if(name.equals(PdfName.RunLengthDecode)
      || name.equals(PdfName.RL))
      throw new NotImplementedException("RunLengthDecode");
    else if(name.equals(PdfName.CCITTFaxDecode)
      || name.equals(PdfName.CCF))
      throw new NotImplementedException("CCITTFaxDecode");
    else if(name.equals(PdfName.JBIG2Decode))
      throw new NotImplementedException("JBIG2Decode");
    else if(name.equals(PdfName.DCTDecode)
      || name.equals(PdfName.DCT))
      throw new NotImplementedException("DCTDecode");
    else if(name.equals(PdfName.JPXDecode))
      throw new NotImplementedException("JPXDecode");
    else if(name.equals(PdfName.Crypt))
      throw new NotImplementedException("Crypt");

    return null;
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected Filter(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  public abstract byte[] decode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    );

  public abstract byte[] encode(
    byte[] data,
    int offset,
    int length,
    PdfDictionary parameters
    );
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}