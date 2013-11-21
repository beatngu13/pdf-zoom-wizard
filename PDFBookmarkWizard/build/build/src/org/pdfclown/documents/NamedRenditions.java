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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.multimedia.Rendition;
import org.pdfclown.objects.NameTree;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfString;

/**
  Named renditions [PDF:1.6:3.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class NamedRenditions
  extends NameTree<Rendition>
{
  // <class>
  // <dynamic>
  // <constructors>
  public NamedRenditions(
    Document context
    )
  {super(context);}

  NamedRenditions(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public NamedRenditions clone(
    Document context
    )
  {return (NamedRenditions)super.clone(context);}

  @Override
  public Rendition put(
    PdfString key,
    Rendition value
    )
  {
    Rendition oldValue = super.put(key, value);
    updateName(oldValue, null);
    updateName(value, key);
    return oldValue;
  }

  @Override
  public Rendition remove(
    Object key
    )
  {
    Rendition oldValue = super.remove(key);
    updateName(oldValue, null);
    return oldValue;
  }
  // </public>

  // <protected>
  @Override
  protected Rendition wrapValue(
    PdfDirectObject baseObject
    )
  {return Rendition.wrap(baseObject);}
  // </protected>

  // <private>
  /**
    Ensures name reference synchronization for the specified rendition [PDF:1.7:9.1.2].
  */
  private void updateName(
    Rendition rendition,
    PdfString name
    )
  {
    if(rendition == null)
      return;

    rendition.getBaseDataObject().put(PdfName.N, name);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
