/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pdfclown.objects.PdfName;
import org.pdfclown.util.metadata.IVersion;
import org.pdfclown.util.metadata.VersionUtils;

/**
  Generic PDF version number [PDF:1.6:H.1].

  @see VersionEnum
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 09/24/12
*/
public final class Version
  implements IVersion
{
  // <class>
  // <static>
  // <fields>
  private static final Pattern versionPattern = Pattern.compile("^(\\d+)\\.(\\d+)$");
  private static final Map<String,Version> versions = new HashMap<String,Version>();
  // </fields>

  // <interface>
  // <public>
  public static Version get(
    PdfName version
    )
  {return get(version.getRawValue());}

  public static Version get(
    String version
    )
  {
    if(!versions.containsKey(version))
    {
      Matcher versionMatcher = versionPattern.matcher(version);
      if(!versionMatcher.find())
        throw new RuntimeException("Invalid PDF version format: '" + versionPattern + "' pattern expected.");

      Version versionObject = new Version(Integer.valueOf(versionMatcher.group(1)),Integer.valueOf(versionMatcher.group(2)));
      versions.put(version,versionObject);
    }
    return versions.get(version);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final int major;
  private final int minor;
  // </fields>

  // <constructors>
  private Version(
    int major,
    int minor
    )
  {
    this.major = major;
    this.minor = minor;
  }
  // </constructors>

  // <interface>
  // <public>
  public int getMajor(
    )
  {return major;}

  public int getMinor(
    )
  {return minor;}

  @Override
  public List<Integer> getNumbers(
  	)
  {return Arrays.asList(major, minor);}

  @Override
  public String toString(
    )
  {return VersionUtils.toString(this);}

  // <Comparable>
  @Override
  public int compareTo(
    IVersion value
    )
  {return VersionUtils.compareTo(this, value);}
  // </Comparable>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
