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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.math.Interval;
import org.pdfclown.util.metadata.IVersion;
import org.pdfclown.util.metadata.VersionUtils;

/**
  Software identifier [PDF:1.7:9.1.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class SoftwareIdentifier
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Software version number [PDF:1.7:9.1.6].

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.2
    @version 0.1.2, 09/24/12
  */
  public static final class Version
    extends PdfObjectWrapper<PdfArray>
    implements IVersion
  {
    public Version(
      Integer... numbers
      )
    {
      super(new PdfArray());
      PdfArray baseDataObject = getBaseDataObject();
      for(Integer number : numbers)
      {baseDataObject.add(new PdfInteger(number));}
    }

    private Version(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Version clone(
      Document context
      )
    {return (Version)super.clone(context);}

    @Override
    public int compareTo(
      IVersion value
      )
    {return VersionUtils.compareTo(this, value);}

    @Override
    public List<Integer> getNumbers(
      )
    {
      List<Integer> numbers = new ArrayList<Integer>();
      for(PdfDirectObject numberObject : getBaseDataObject())
      {numbers.add(((PdfInteger)numberObject).getValue());}
      return numbers;
    }

    @Override
    public String toString(
      )
    {return VersionUtils.toString(this);}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static SoftwareIdentifier wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new SoftwareIdentifier(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public SoftwareIdentifier(
    Document context
    )
  {super(context, new PdfDictionary());}

  private SoftwareIdentifier(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public SoftwareIdentifier clone(
    Document context
    )
  {return (SoftwareIdentifier)super.clone(context);}

  /**
    Gets the operating system identifiers that indicate which operating systems this object applies
    to. The defined values are the same as those defined for SMIL 2.0's systemOperatingSystem
    attribute. An empty list is considered to represent all operating systems.
  */
  public List<String> getOSes(
    )
  {
    List<String> oses = new ArrayList<String>();
    {
      PdfArray osesObject = (PdfArray)getBaseDataObject().get(PdfName.OS);
      if(osesObject != null)
      {
        for(PdfDirectObject osObject : osesObject)
        {oses.add(((PdfString)osObject).getStringValue());}
      }
    }
    return oses;
  }

  /**
    Gets the URI that identifies a piece of software.
    <p>It is interpreted according to its scheme; the only presently defined scheme is
    vnd.adobe.swname. The scheme name is case-insensitive; if is not recognized by the viewer
    application, the software must be considered a non-match. The syntax of URIs of this scheme is
    "vnd.adobe.swname:" software_name where software_name is equivalent to reg_name as defined in
    Internet RFC 2396, Uniform Resource Identifiers (URI): Generic Syntax.</p>
  */
  public URI getURI(
    )
  {
    try
    {
      PdfString uriObject = (PdfString)getBaseDataObject().get(PdfName.U);
      return uriObject != null ? new URI(uriObject.getStringValue()) : null;
    }
    catch(URISyntaxException e)
    {throw new RuntimeException("URI instantiation failed.", e);}
  }

  /**
    Gets the software version bounds.
  */
  public Interval<Version> getVersion(
    )
  {
    PdfDictionary baseDataObject = getBaseDataObject();
    return new Interval<Version>(
      new Version(baseDataObject.get(PdfName.L)),
      new Version(baseDataObject.get(PdfName.H)),
      (Boolean)PdfBoolean.getValue(baseDataObject.get(PdfName.LI), true),
      (Boolean)PdfBoolean.getValue(baseDataObject.get(PdfName.HI), true)
      );
  }

  //TODO:setters!!!
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
