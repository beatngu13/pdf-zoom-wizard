/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.ILink;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Link annotation [PDF:1.6:8.4.5].
  <p>It represents either a hypertext link to a destination elsewhere in the document
  or an action to be performed.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Link
  extends Annotation
  implements ILink
{
  // <class>
  // <dynamic>
  // <constructors>
  public Link(
    Page page,
    Rectangle2D box,
    String text,
    PdfObjectWrapper<?> target
    )
  {
    super(page, PdfName.Link, box, text);
    setTarget(target);
  }

  Link(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Link clone(
    Document context
    )
  {return (Link)super.clone(context);}

  @Override
  public void setAction(
    Action value
    )
  {
    /*
      NOTE: This entry is not permitted in link annotations if a 'Dest' entry is present.
    */
    if(getBaseDataObject().containsKey(PdfName.Dest)
      && value != null)
    {getBaseDataObject().remove(PdfName.Dest);}

    super.setAction(value);
  }

  // <ILink>
  @Override
  public PdfObjectWrapper<?> getTarget(
    )
  {
    if(getBaseDataObject().containsKey(PdfName.Dest))
      return getDestination();
    else if(getBaseDataObject().containsKey(PdfName.A))
      return getAction();
    else
      return null;
  }

  @Override
  public void setTarget(
    PdfObjectWrapper<?> value
    )
  {
    if(value instanceof Destination)
    {setDestination((Destination)value);}
    else if(value instanceof Action)
    {setAction((Action)value);}
    else
      throw new IllegalArgumentException("It MUST be either a Destination or an Action.");
  }
  // </ILink>
  // </public>

  // <private>
  private Destination getDestination(
    )
  {
    PdfDirectObject destinationObject = getBaseDataObject().get(PdfName.Dest);
    return destinationObject != null
      ? getDocument().resolveName(
        Destination.class,
        destinationObject
        )
      : null;
  }

  private void setDestination(
    Destination value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.Dest);}
    else
    {
      /*
        NOTE: This entry is not permitted in link annotations if an 'A' entry is present.
      */
      if(getBaseDataObject().containsKey(PdfName.A))
      {getBaseDataObject().remove(PdfName.A);}

      getBaseDataObject().put(PdfName.Dest,value.getNamedBaseObject());
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}