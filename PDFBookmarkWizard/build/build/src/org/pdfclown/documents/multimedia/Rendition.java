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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interchange.access.LanguageIdentifier;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.IPdfNamedObjectWrapper;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfString;
import org.pdfclown.util.math.Interval;

/**
  Rendition [PDF:1.7:9.1.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF15)
public abstract class Rendition
  extends PdfObjectWrapper<PdfDictionary>
  implements IPdfNamedObjectWrapper
{
  // <class>
  // <classes>
  /**
    Rendition viability [PDF:1.7:9.1.2].
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
      Gets the minimum system's bandwidth (in bits per second). Equivalent to SMIL's systemBitrate
      attribute.
    */
    public Integer getBandwidth(
      )
    {return (Integer)PdfInteger.getValue(getMediaCriteria().get(PdfName.R));}

    /**
      Gets the minimum screen color depth (in bits per pixel). Equivalent to SMIL's systemScreenDepth
      attribute.
    */
    public Integer getScreenDepth(
      )
    {
      PdfDictionary screenDepthObject = (PdfDictionary)getMediaCriteria().get(PdfName.D);
      return screenDepthObject != null ? ((PdfInteger)screenDepthObject.get(PdfName.V)).getValue() : null;
    }

    /**
      Gets the minimum screen size (in pixels). Equivalent to SMIL's systemScreenSize attribute.
    */
    public Dimension getScreenSize(
      )
    {
      PdfDictionary screenSizeObject = (PdfDictionary)getMediaCriteria().get(PdfName.Z);
      if(screenSizeObject == null)
        return null;

      PdfArray screenSizeValueObject = (PdfArray)screenSizeObject.get(PdfName.V);
      return screenSizeValueObject != null
        ? new Dimension(
          ((PdfInteger)screenSizeValueObject.get(0)).getValue(),
          ((PdfInteger)screenSizeValueObject.get(1)).getValue()
          )
        : null;
    }

    /**
      Gets the list of supported viewer applications.
    */
    public Array<SoftwareIdentifier> getRenderers(
      )
    {return Array.wrap(SoftwareIdentifier.class, getMediaCriteria().get(PdfName.V, PdfArray.class));}

    /**
      Gets the PDF version range supported by the viewer application.
    */
    public Interval<Version> getVersion(
      )
    {
      PdfArray pdfVersionArray = (PdfArray)getMediaCriteria().get(PdfName.P);
      return pdfVersionArray != null && !pdfVersionArray.isEmpty()
        ? new Interval<Version>(
          Version.get((PdfName)pdfVersionArray.get(0)),
          pdfVersionArray.size() > 1 ? Version.get((PdfName)pdfVersionArray.get(1)) : null
          )
        : null;
    }

    /**
      Gets the list of supported languages. Equivalent to SMIL's systemLanguage attribute.
    */
    public List<LanguageIdentifier> getLanguages(
      )
    {
      List<LanguageIdentifier> languages = new ArrayList<LanguageIdentifier>();
      {
        PdfArray languagesObject = (PdfArray)getMediaCriteria().get(PdfName.L);
        if(languagesObject != null)
        {
          for(PdfDirectObject languageObject : languagesObject)
          {languages.add(LanguageIdentifier.wrap(languageObject));}
        }
      }
      return languages;
    }

    /**
      Gets whether to hear audio descriptions. Equivalent to SMIL's systemAudioDesc attribute.
    */
    public Boolean isAudioDescriptionEnabled(
      )
    {return (Boolean)PdfBoolean.getValue(getMediaCriteria().get(PdfName.A));}

    /**
      Gets whether to hear audio overdubs.
    */
    public Boolean isAudioOverdubEnabled(
      )
    {return (Boolean)PdfBoolean.getValue(getMediaCriteria().get(PdfName.O));}

    /**
      Gets whether to see subtitles.
    */
    public Boolean isSubtitleEnabled(
      )
    {return (Boolean)PdfBoolean.getValue(getMediaCriteria().get(PdfName.S));}

    /**
      Gets whether to see text captions. Equivalent to SMIL's systemCaptions attribute.
    */
    public Boolean isTextCaptionEnabled(
      )
    {return (Boolean)PdfBoolean.getValue(getMediaCriteria().get(PdfName.C));}

    private PdfDictionary getMediaCriteria(
      )
    {return getBaseDataObject().resolve(PdfName.C, PdfDictionary.class);}

    //TODO:setters!
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  /**
    Wraps a rendition base object into a rendition object.

    @param baseObject Rendition base object.
    @return Rendition object associated to the base object.
  */
  public static Rendition wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfName subtype = (PdfName)((PdfDictionary)baseObject.resolve()).get(PdfName.S);
    if(PdfName.MR.equals(subtype))
      return new MediaRendition(baseObject);
    else if(PdfName.SR.equals(subtype))
      return new SelectorRendition(baseObject);
    else
      throw new IllegalArgumentException("'baseObject' parameter doesn't represent a valid rendition object.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected Rendition(
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
          PdfName.Rendition,
          subtype
        }
        )
      );
  }

  protected Rendition(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the preferred options the renderer should attempt to honor without affecting its viability
    [PDF:1.7:9.1.1].
  */
  public Viability getPreferences(
    )
  {return new Viability(getBaseDataObject().get(PdfName.BE, PdfDictionary.class));}

  /**
    Gets the minimum requirements the renderer must honor in order to be considered viable
    [PDF:1.7:9.1.1].
  */
  public Viability getRequirements(
    )
  {return new Viability(getBaseDataObject().get(PdfName.MH, PdfDictionary.class));}

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

  // <IPdfNamedObjectWrapper>
  @Override
  public PdfString getName(
    )
  {return retrieveName();}

  @Override
  public PdfDirectObject getNamedBaseObject(
    )
  {return retrieveNamedBaseObject();}
  // </IPdfNamedObjectWrapper>
  // </public>

  // <protected>
  @Override
  protected PdfString retrieveName(
    )
  {
    /*
      NOTE: A rendition dictionary is not required to have a name tree entry. When it does, the
      viewer application should ensure that the name specified in the tree is kept the same as the
      value of the N entry (for example, if the user interface allows the name to be changed).
    */
    return (PdfString)getBaseDataObject().get(PdfName.N);
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}
