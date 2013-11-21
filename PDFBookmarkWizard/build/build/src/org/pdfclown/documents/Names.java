/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.actions.JavaScript;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.multimedia.Rendition;
import org.pdfclown.objects.ICompositeMap;
import org.pdfclown.objects.NameTree;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfString;

/**
  Name dictionary [PDF:1.6:3.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class Names
  extends PdfObjectWrapper<PdfDictionary>
  implements ICompositeMap<PdfString>
{
  // <class>
  // <dynamic>
  // <constructors>
  public Names(
    Document context
    )
  {super(context, new PdfDictionary());}

  Names(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Names clone(
    Document context
    )
  {return (Names)super.clone(context);}

  /**
    Gets the named destinations.
  */
  @PDF(VersionEnum.PDF12)
  public NamedDestinations getDestinations(
    )
  {return new NamedDestinations(getBaseDataObject().get(PdfName.Dests, PdfDictionary.class, false));}

  /**
    Gets the named embedded files.
  */
  @PDF(VersionEnum.PDF14)
  public NamedEmbeddedFiles getEmbeddedFiles(
    )
  {return new NamedEmbeddedFiles(getBaseDataObject().get(PdfName.EmbeddedFiles, PdfDictionary.class, false));}

  /**
    Gets the named JavaScript actions.
  */
  @PDF(VersionEnum.PDF13)
  public NamedJavaScripts getJavaScripts(
    )
  {return new NamedJavaScripts(getBaseDataObject().get(PdfName.JavaScript, PdfDictionary.class, false));}

  /**
    Gets the named renditions.
  */
  @PDF(VersionEnum.PDF15)
  public NamedRenditions getRenditions(
    )
  {return new NamedRenditions(getBaseDataObject().get(PdfName.Renditions, PdfDictionary.class, false));}

  /**
    @see #getDestinations()
  */
  public void setDestinations(
    NamedDestinations value
    )
  {getBaseDataObject().put(PdfName.Dests, value.getBaseObject());}

  /**
    @see #getEmbeddedFiles()
  */
  public void setEmbeddedFiles(
    NamedEmbeddedFiles value
    )
  {getBaseDataObject().put(PdfName.EmbeddedFiles, value.getBaseObject());}

  /**
    @see #getJavaScripts()
  */
  public void setJavaScripts(
    NamedJavaScripts value
    )
  {getBaseDataObject().put(PdfName.JavaScript, value.getBaseObject());}

  /**
    @see #getRenditions()
  */
  public void setRenditions(
    NamedRenditions value
    )
  {getBaseDataObject().put(PdfName.Renditions, value.getBaseObject());}

  // <ICompositeMap>
  @Override
  @SuppressWarnings("unchecked")
  public <T extends PdfObjectWrapper<? extends PdfDataObject>> NameTree<T> get(
    Class<T> type
    )
  {
    if(Destination.class.isAssignableFrom(type))
      return (NameTree<T>)getDestinations();
    else if(FileSpecification.class.isAssignableFrom(type))
      return (NameTree<T>)getEmbeddedFiles();
    else if(JavaScript.class.isAssignableFrom(type))
      return (NameTree<T>)getJavaScripts();
    else if(Rendition.class.isAssignableFrom(type))
      return (NameTree<T>)getRenditions();
    else
      return null;
  }

  @Override
  public <T extends PdfObjectWrapper<? extends PdfDataObject>> T get(
    Class<T> type,
    PdfString key
    )
  {return get(type).get(key);}
  // </ICompositeMap>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}