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

package org.pdfclown.documents.interaction.actions;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Abstract 'go to destination' action.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 09/24/12
*/
@PDF(VersionEnum.PDF11)
public abstract class GoToDestination<T extends Destination>
  extends Action
  implements IGoToAction
{
  // <class>
  // <constructors>
  protected GoToDestination(
    Document context,
    PdfName actionType,
    T destination
    )
  {
    super(context, actionType);
    setDestination(destination);
  }

  protected GoToDestination(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the destination to jump to.
  */
  @SuppressWarnings("unchecked")
  public T getDestination(
    )
  {
    return (T)getDocument().resolveName(
      Destination.class,
      getBaseDataObject().get(PdfName.D)
      );
  }

  /**
    @see #getDestination()
  */
  public void setDestination(
    T value
    )
  {
    if(value == null)
      throw new IllegalArgumentException("Destination MUST be defined.");

    getBaseDataObject().put(PdfName.D, value.getNamedBaseObject());
  }
  // </public>
  // </interface>
  // </class>
}
