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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.files.FullFileSpecification;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Media clip object [PDF:1.7:9.1.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF15)
public abstract class MediaClip
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps a clip base object into a clip object.
  */
  public static MediaClip wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfName subtype = (PdfName)((PdfDictionary)baseObject.resolve()).get(PdfName.S);
    if(PdfName.MCD.equals(subtype))
      return new MediaClipData(baseObject);
    else if(PdfName.MCS.equals(subtype))
      return new MediaClipSection(baseObject);
    else
      throw new IllegalArgumentException("'baseObject' parameter doesn't represent a valid clip object.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected MediaClip(
    Document context,
    PdfName subtype
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.S
        },
        new PdfDirectObject[]
        {
          PdfName.MediaClip,
          subtype
        }
        )
      );
  }

  protected MediaClip(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the actual media data.

    @return Either a {@link FullFileSpecification}} or a {@link FormXObject}}.
  */
  public abstract PdfObjectWrapper<?> getData(
    );
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
