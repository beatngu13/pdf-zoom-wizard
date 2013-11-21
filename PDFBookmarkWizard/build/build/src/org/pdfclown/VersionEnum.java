/*
  Copyright 2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown;

/**
  Managed PDF version number [PDF:1.6:H.1].

  @see Version
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.0
*/
public enum VersionEnum
{
  /**
    Version 1.0 (1993, Acrobat 1).
  */
  PDF10,
  /**
    Version 1.1 (1996, Acrobat 2).
  */
  PDF11,
  /**
    Version 1.2 (1996, Acrobat 3).
  */
  PDF12,
  /**
    Version 1.3 (2000, Acrobat 4).
  */
  PDF13,
  /**
    Version 1.4 (2001, Acrobat 5).
  */
  PDF14,
  /**
    Version 1.5 (2003, Acrobat 6).
  */
  PDF15,
  /**
    Version 1.6 (2004, Acrobat 7).
  */
  PDF16,
  /**
    Version 1.7 (2006, Acrobat 8).
  */
  PDF17;

  private Version version;

  private VersionEnum(
    )
  {
    String versionSuffix = name().substring(name().length() - 2);
    this.version = Version.get(versionSuffix.charAt(0) + "." + versionSuffix.charAt(1));
  }

  public Version getVersion(
    )
  {return version;}
}