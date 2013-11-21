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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.objects.PdfString;

/**
  Media clip data [PDF:1.7:9.1.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF15)
public final class MediaClipData
  extends MediaClip
{
  // <class>
  // <classes>
  /**
    Circumstance under which it is acceptable to write a temporary file in order to play a media
    clip.
  */
  public enum TempFilePermissionEnum
  {
    /**
      Never allowed.
    */
    Never(new PdfString("TEMPNEVER")),
    /**
      Allowed only if the document permissions allow content extraction.
    */
    ContentExtraction(new PdfString("TEMPEXTRACT")),
    /**
      Allowed only if the document permissions allow content extraction, including for accessibility
      purposes.
    */
    Accessibility(new PdfString("TEMPACCESS")),
    /**
      Always allowed.
    */
    Always(new PdfString("TEMPALWAYS"));

    private static Map<PdfString, TempFilePermissionEnum> map = new HashMap<PdfString, TempFilePermissionEnum>();

    static
    {
      for (TempFilePermissionEnum value : TempFilePermissionEnum.values())
      {map.put(value.getCode(), value);}
    }

    public static TempFilePermissionEnum valueOf(
      PdfString code
      )
    {return map.get(code);}

    private final PdfString code;

    private TempFilePermissionEnum(
      PdfString code
      )
    {this.code = code;}

    public PdfString getCode(
      )
    {return code;}
  }

  /**
    Media clip data viability.
  */
  public static class Viability
    extends PdfObjectWrapper<PdfDictionary>
  {
    private Viability(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Viability clone(
      Document context
      )
    {return (Viability)super.clone(context);}

    /**
      Gets the absolute URL to be used as the base URL in resolving any relative URLs found within
      the media data.
    */
    public URL getBaseURL(
      )
    {
      PdfString baseURLObject = (PdfString)getBaseDataObject().get(PdfName.BU);
      try
      {return baseURLObject != null ? new URL(baseURLObject.getStringValue()) : null;}
      catch(MalformedURLException e)
      {throw new RuntimeException(e);}
    }

    public void setBaseURL(
      URL value
      )
    {getBaseDataObject().put(PdfName.BU, value != null ? new PdfString(value.toString()) : null);}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public MediaClipData(
    PdfObjectWrapper<?> data,
    String mimeType
    )
  {
    super(data.getDocument(), PdfName.MCD);
    setData(data);
    setMimeType(mimeType);
    setTempFilePermission(TempFilePermissionEnum.Always);
  }

  MediaClipData(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public MediaClipData clone(
    Document context
    )
  {return (MediaClipData)super.clone(context);}

  @Override
  public PdfObjectWrapper<?> getData(
    )
  {
    PdfDirectObject dataObject = getBaseDataObject().get(PdfName.D);
    if(dataObject == null)
      return null;

    if(dataObject.resolve() instanceof PdfStream)
      return FormXObject.wrap(dataObject);
    else
      return FileSpecification.wrap(dataObject);
  }

  /**
    Gets the MIME type of data [RFC 2045].
  */
  public String getMimeType(
    )
  {return (String)PdfString.getValue(getBaseDataObject().get(PdfName.CT));}

  /**
    Gets the player rules for playing this media.
  */
  public MediaPlayers getPlayers(
    )
  {return MediaPlayers.wrap(getBaseDataObject().get(PdfName.PL, PdfDictionary.class));}

  /**
    Gets the preferred options the renderer should attempt to honor without affecting its viability.
  */
  public Viability getPreferences(
    )
  {return new Viability(getBaseDataObject().get(PdfName.BE, PdfDictionary.class));}

  /**
    Gets the minimum requirements the renderer must honor in order to be considered viable.
  */
  public Viability getRequirements(
    )
  {return new Viability(getBaseDataObject().get(PdfName.MH, PdfDictionary.class));}

  /**
    Gets the circumstance under which it is acceptable to write a temporary file in order to play
    this media clip.
  */
  public TempFilePermissionEnum getTempFilePermission(
    )
  {return TempFilePermissionEnum.valueOf((PdfString)getBaseDataObject().resolve(PdfName.P, PdfDictionary.class).get(PdfName.TF));}

  /**
    @see #getData()
  */
  public void setData(
    PdfObjectWrapper<?> value
    )
  {getBaseDataObject().put(PdfName.D, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getMimeType()
  */
  public void setMimeType(
    String value
    )
  {getBaseDataObject().put(PdfName.CT, value != null ? new PdfString(value) : null);}

  /**
    @see #getPlayers()
  */
  public void setPlayers(
    MediaPlayers value
    )
  {getBaseDataObject().put(PdfName.PL, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getPreferences()
  */
  public void setPreferences(
    Viability value
    )
  {getBaseDataObject().put(PdfName.BE, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getRequirements()
  */
  public void setRequirements(
    Viability value
    )
  {getBaseDataObject().put(PdfName.MH, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getTempFilePermission()
  */
  public void setTempFilePermission(
    TempFilePermissionEnum value
    )
  {getBaseDataObject().resolve(PdfName.P, PdfDictionary.class).put(PdfName.TF, value != null ? value.getCode() : null);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
