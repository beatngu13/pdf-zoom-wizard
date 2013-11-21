/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.multimedia;

import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfInteger;

/**
  Monitor specifier [PDF:1.7:9.1.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 09/24/12
*/
@PDF(VersionEnum.PDF15)
public enum MonitorSpecifierEnum
{
  // <class>
  // <static>
  // <fields>
  /**
    The monitor containing the largest section of the document window.
  */
  LargestDocumentWindowSection(PdfInteger.get(0)),
  /**
    The monitor containing the smallest section of the document window.
  */
  SmallestDocumentWindowSection(PdfInteger.get(1)),
  /**
    Primary monitor, otherwise the monitor containing the largest section of the document window.
  */
  Primary(PdfInteger.get(2)),
  /**
    The monitor with the greatest color depth (in bits).
  */
  GreatestColorDepth(PdfInteger.get(3)),
  /**
    The monitor with the greatest area (in pixels squared).
  */
  GreatestArea(PdfInteger.get(4)),
  /**
    The monitor with the greatest height (in pixels).
  */
  GreatestHeight(PdfInteger.get(5)),
  /**
    The monitor with the greatest width (in pixels).
  */
  GreatestWidth(PdfInteger.get(6));

  private static Map<PdfInteger, MonitorSpecifierEnum> map = new HashMap<PdfInteger, MonitorSpecifierEnum>();
  // </fields>

  // <constructors>
  static
  {
    for (MonitorSpecifierEnum value : MonitorSpecifierEnum.values())
    {map.put(value.getCode(), value);}
  }
  // </constructors>

  // <interface>
  // <public>
  public static MonitorSpecifierEnum valueOf(
    PdfInteger code
    )
  {return map.containsKey(code) ? map.get(code) : LargestDocumentWindowSection;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final PdfInteger code;
  // </fields>

  // <constructors>
  private MonitorSpecifierEnum(
    PdfInteger code
    )
  {this.code = code;}
  // </constructors>

  // <interface>
  // <public>
  public PdfInteger getCode(
    )
  {return code;}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}