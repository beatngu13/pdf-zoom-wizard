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

package org.pdfclown.documents.interchange.access;

import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.StringUtils;

/**
  Language identifier [PDF:1.7:10.8.1][RFC 3066].
  <p>Language identifiers can be based on codes defined by the International Organization for
  Standardization in ISO 639 (language code) and ISO 3166 (country code) or registered with the
  Internet Assigned Numbers Authority (<a href="http://iana.org">IANA</a>), or they can include codes
  created for private use.</p>
  <p>A language identifier consists of a primary code optionally followed by one or more subcodes
  (each preceded by a hyphen).</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF14)
public final class LanguageIdentifier
  extends PdfObjectWrapper<PdfTextString>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps a language identifier base object into a language identifier object.
  */
  public static LanguageIdentifier wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    if(baseObject.resolve() instanceof PdfTextString)
      return new LanguageIdentifier(baseObject);
    else
      throw new IllegalArgumentException("'baseObject' parameter doesn't represent a valid language identifier object.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public LanguageIdentifier(
    String... components
    )
  {this(Arrays.asList(components));}

  public LanguageIdentifier(
    List<String> components
    )
  {setComponents(components);}

  LanguageIdentifier(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LanguageIdentifier clone(
    Document context
    )
  {return (LanguageIdentifier)super.clone(context);}

  /**
    Gets the identifier components:
    <ol>
      <li>the first one is the <b>primary code</b>. It can be any of the following:
        <ul>
          <li>a 2-character ISO 639 language code (e.g., <code>en</code> for English);</li>
          <li>the letter <code>i</code>, designating an IANA-registered identifier;</li>
          <li>the letter <code>x</code>, for private use;</li>
        </ul>
      </li>
      <li>the second one is the <b>first subcode</b>. It can be any of the following:
        <ul>
          <li>a 2-character ISO 3166 country code (e.g., <code>en-US</code>);</li>
          <li>a 3-to-8-character subcode registered with IANA (e.g., <code>en-cockney</code>)</li>
          <li>private non-registered subcodes;</li>
        </ul>
      </li>
      <li><b>subcodes beyond the first</b> can be any that have been registered with IANA.</li>
    </ol>
  */
  public List<String> getComponents(
    )
  {return Arrays.asList(getBaseDataObject().getValue().split("-"));}

  /**
    @see #getComponents()
  */
  public void setComponents(
    List<String> value
    )
  {setBaseObject(new PdfTextString(StringUtils.join('-', value.toArray(new String[0]))));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
