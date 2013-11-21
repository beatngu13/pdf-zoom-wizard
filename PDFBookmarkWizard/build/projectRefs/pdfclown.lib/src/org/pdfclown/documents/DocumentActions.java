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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Document actions [PDF:1.6:8.5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF14)
public final class DocumentActions
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  public DocumentActions(
    Document context
    )
  {super(context, new PdfDictionary());}

  DocumentActions(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public DocumentActions clone(
    Document context
    )
  {return (DocumentActions)super.clone(context);}

  /**
    Gets the action to be performed after printing the document.
  */
  public Action getAfterPrint(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.DP));}

  /**
    Gets the action to be performed after saving the document.
  */
  public Action getAfterSave(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.DS));}

  /**
    Gets the action to be performed before printing the document.
  */
  public Action getBeforePrint(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.WP));}

  /**
    Gets the action to be performed before saving the document.
  */
  public Action getBeforeSave(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.WS));}

  /**
    Gets the action to be performed before closing the document.
  */
  public Action getOnClose(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.DC));}

  /**
    Gets the destination to be displayed or the action to be performed
    after opening the document.
  */
  public PdfObjectWrapper<?> getOnOpen(
    )
  {
    PdfDirectObject onOpenObject = getDocument().getBaseDataObject().get(PdfName.OpenAction);
    if(onOpenObject instanceof PdfDictionary) // Action (dictionary).
      return Action.wrap(onOpenObject);
    else // Destination (array).
      return Destination.wrap(onOpenObject);
  }

  /**
    @see #getAfterPrint()
  */
  public void setAfterPrint(
    Action value
    )
  {getBaseDataObject().put(PdfName.DP, value.getBaseObject());}

  /**
    @see #getAfterSave()
  */
  public void setAfterSave(
    Action value
    )
  {getBaseDataObject().put(PdfName.DS, value.getBaseObject());}

  /**
    @see #getBeforePrint()
  */
  public void setBeforePrint(
    Action value
    )
  {getBaseDataObject().put(PdfName.WP, value.getBaseObject());}

  /**
    @see #getBeforeSave()
  */
  public void setBeforeSave(
    Action value
    )
  {getBaseDataObject().put(PdfName.WS, value.getBaseObject());}

  /**
    @see #getOnClose()
  */
  public void setOnClose(
    Action value
    )
  {getBaseDataObject().put(PdfName.DC, value.getBaseObject());}

  /**
    @see #getOnOpen()
  */
  public void setOnOpen(
    PdfObjectWrapper<?> value
    )
  {
    if(!(value instanceof Action
      || value instanceof LocalDestination))
      throw new IllegalArgumentException("Value MUST be either an Action or a LocalDestination.");

    getDocument().getBaseDataObject().put(PdfName.OpenAction, value.getBaseObject());
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}