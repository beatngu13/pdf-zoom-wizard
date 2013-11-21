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
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Media player rules [PDF:1.7:9.1.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class MediaPlayers
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static MediaPlayers wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new MediaPlayers(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public MediaPlayers(
    Document context
    )
  {super(context, new PdfDictionary());}

  private MediaPlayers(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public MediaPlayers clone(
    Document context
    )
  {return (MediaPlayers)super.clone(context);}

  /**
    Gets a set of players, any of which may be used in playing the associated media object. This
    collection is ignored if {@link #getRequiredPlayers()} is non-empty.
  */
  public Array<MediaPlayer> getAllowedPlayers(
    )
  {return Array.wrap(MediaPlayer.class, getBaseDataObject().get(PdfName.A, PdfArray.class));}

  /**
    Gets a set of players that must NOT be used in playing the associated media object. This
    collection takes priority over {@link #getRequiredPlayers()}.
  */
  public Array<MediaPlayer> getForbiddenPlayers(
    )
  {return Array.wrap(MediaPlayer.class, getBaseDataObject().get(PdfName.NU, PdfArray.class));}

  /**
    Gets a set of players, one of which must be used in playing the associated media object.
  */
  public Array<MediaPlayer> getRequiredPlayers(
    )
  {return Array.wrap(MediaPlayer.class, getBaseDataObject().get(PdfName.MU, PdfArray.class));}

  /**
    @see #getAllowedPlayers()
  */
  public void setAllowedPlayers(
    Array<MediaPlayer> value
    )
  {getBaseDataObject().put(PdfName.A, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getForbiddenPlayers()
  */
  public void setForbiddenPlayers(
    Array<MediaPlayer> value
    )
  {getBaseDataObject().put(PdfName.NU, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getRequiredPlayers()
  */
  public void setRequiredPlayers(
    Array<MediaPlayer> value
    )
  {getBaseDataObject().put(PdfName.MU, PdfObjectWrapper.getBaseObject(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
