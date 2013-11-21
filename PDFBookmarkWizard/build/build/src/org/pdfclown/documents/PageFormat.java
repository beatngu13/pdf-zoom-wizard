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

package org.pdfclown.documents;

import org.pdfclown.util.NotImplementedException;

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
  Page format.
  <p>This utility provides an easy access to the dimension of common page formats.</p>

  @see Document#setPageSize(Dimension2D)
  @see Page#setSize(Dimension2D)
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.3
  @version 0.1.0
*/
public final class PageFormat
{
  // <class>
  // <classes>
  /**
    Paper size.
    <h3>Remarks</h3>
    <p>References:</p>
    <ul>
      <li>{ 'A' digit+ }: [ISO 216] "A" series: Paper and boards, trimmed sizes.</li>
      <li>{ 'B' digit+ }: [ISO 216] "B" series: Posters, wall charts and similar items.</li>
      <li>{ 'C' digit+ }: [ISO 269] "C" series: Envelopes or folders suitable for A-size
      stationery.</li>
      <li>{ 'Ansi' letter }: [ANSI/ASME Y14.1] ANSI series: US engineering drawing series.</li>
      <li>{ 'Arch' letter }: Architectural series.</li>
      <li>{ "Letter", "Legal", "Executive", "Statement", "Tabloid" }: Traditional north-american
      sizes.</li>
    </ul>
  */
  public enum SizeEnum
  {
    A0,A1,A2,A3,A4,A5,A6,A7,A8,A9,A10,
    B0,B1,B2,B3,B4,B5,B6,B7,B8,B9,B10,
    C0,C1,C2,C3,C4,C5,C6,C7,C8,C9,C10,
    Letter,
    Legal,
    Executive,
    Statement,
    Tabloid,
    ArchA, ArchB, ArchC, ArchD, ArchE,
    AnsiA, AnsiB, AnsiC, AnsiD, AnsiE
  };

  /**
    Page orientation.
  */
  public enum OrientationEnum
  {
    Portrait,
    Landscape
  }
  // </classes>

  // <static>
  // <fields>
  private static final String IsoSeriesSize_A = "A";
  private static final String IsoSeriesSize_B = "B";
  private static final String IsoSeriesSize_C = "C";

  private static final Pattern IsoSeriesSizePattern = Pattern.compile(
    "(["
      + IsoSeriesSize_A
      + IsoSeriesSize_B
      + IsoSeriesSize_C
      + "])([\\d]+)"
    );
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the default page size.
    <h3>Remarks</h3>
    <p>The returned dimension corresponds to the widely-established ISO A4 standard paper format,
    portrait orientation.</p>
  */
  public static Dimension getSize(
    )
  {return getSize(SizeEnum.A4);}

  /**
    Gets the page size of the given format, portrait orientation.
    @param size Page size.
  */
  public static Dimension getSize(
    SizeEnum size
    )
  {return getSize(size,OrientationEnum.Portrait);}

  /**
    Gets the page size of the given format and orientation.
    @param size Page size.
    @param orientation Page orientation.
  */
  public static Dimension getSize(
    SizeEnum size,
    OrientationEnum orientation
    )
  {
    int width, height = 0;

    // Size.
    {
      String sizeName = size.name();
      Matcher matcher = IsoSeriesSizePattern.matcher(sizeName);
      // Is it an ISO standard size?
      if(matcher.matches())
      {
        int baseWidth, baseHeight = 0;
        String isoSeriesSize = matcher.group(1);
        if(isoSeriesSize.equals(IsoSeriesSize_A))
        {baseWidth = 2384; baseHeight = 3370;}
        else if(isoSeriesSize.equals(IsoSeriesSize_B))
        {baseWidth = 2834; baseHeight = 4008;}
        else if(isoSeriesSize.equals(IsoSeriesSize_C))
        {baseWidth = 2599; baseHeight = 3676;}
        else
        {throw new NotImplementedException("Paper format " + size + " not supported yet.");}

        int isoSeriesSizeIndex = Integer.parseInt(matcher.group(2));
        double isoSeriesSizeFactor = 1 / Math.pow(2,isoSeriesSizeIndex/2d);

        width = (int)Math.floor(baseWidth * isoSeriesSizeFactor);
        height = (int)Math.floor(baseHeight * isoSeriesSizeFactor);
      }
      else // Non-ISO size.
      {
        switch(size)
        {
          case ArchA: width = 648; height = 864; break;
          case ArchB: width = 864; height = 1296; break;
          case ArchC: width = 1296; height = 1728; break;
          case ArchD: width = 1728; height = 2592; break;
          case ArchE: width = 2592; height = 3456; break;
          case AnsiA: case Letter: width = 612; height = 792; break;
          case AnsiB: case Tabloid: width = 792; height = 1224; break;
          case AnsiC: width = 1224; height = 1584; break;
          case AnsiD: width = 1584; height = 2448; break;
          case AnsiE: width = 2448; height = 3168; break;
          case Legal: width = 612; height = 1008; break;
          case Executive: width = 522; height = 756; break;
          case Statement: width = 396; height = 612; break;
          default: throw new NotImplementedException("Paper format " + size + " not supported yet.");
        }
      }
    }

    // Orientation.
    switch(orientation)
    {
      case Portrait:
        return new Dimension(width,height);
      case Landscape:
        return new Dimension(height,width);
      default:
        throw new NotImplementedException("Orientation " + orientation + " not supported yet.");
    }
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  private PageFormat(
    )
  {}
  // </constructors>
  // </dynamic>
  // </class>
}