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

package org.pdfclown.documents.interaction.actions;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Action to be performed by the viewer application [PDF:1.6:8.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF11)
public class Action
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps an action base object into an action object.

    @param baseObject Action base object.
    @return Action object associated to the base object.
  */
  public static final Action wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfDictionary dataObject = (PdfDictionary)baseObject.resolve();
    PdfName actionType = (PdfName)dataObject.get(PdfName.S);
    if(actionType == null
      || (dataObject.containsKey(PdfName.Type)
          && !dataObject.get(PdfName.Type).equals(PdfName.Action)))
      return null;

    if(actionType.equals(PdfName.GoTo))
      return new GoToLocal(baseObject);
    else if(actionType.equals(PdfName.GoToR))
      return new GoToRemote(baseObject);
    else if(actionType.equals(PdfName.GoToE))
      return new GoToEmbedded(baseObject);
    else if(actionType.equals(PdfName.Launch))
      return new Launch(baseObject);
    else if(actionType.equals(PdfName.Thread))
      return new GoToThread(baseObject);
    else if(actionType.equals(PdfName.URI))
      return new GoToURI(baseObject);
    else if(actionType.equals(PdfName.Sound))
      return new PlaySound(baseObject);
    else if(actionType.equals(PdfName.Movie))
      return new PlayMovie(baseObject);
    else if(actionType.equals(PdfName.Hide))
      return new ToggleVisibility(baseObject);
    else if(actionType.equals(PdfName.Named))
    {
      PdfName actionName = (PdfName)dataObject.get(PdfName.N);
      if(actionName.equals(PdfName.NextPage))
        return new GoToNextPage(baseObject);
      else if(actionName.equals(PdfName.PrevPage))
        return new GoToPreviousPage(baseObject);
      else if(actionName.equals(PdfName.FirstPage))
        return new GoToFirstPage(baseObject);
      else if(actionName.equals(PdfName.LastPage))
        return new GoToLastPage(baseObject);
      else // Custom named action.
        return new NamedAction(baseObject);
    }
    else if(actionType.equals(PdfName.SubmitForm))
      return new SubmitForm(baseObject);
    else if(actionType.equals(PdfName.ResetForm))
      return new ResetForm(baseObject);
    else if(actionType.equals(PdfName.ImportData))
      return new ImportData(baseObject);
    else if(actionType.equals(PdfName.JavaScript))
      return new JavaScript(baseObject);
    else if(actionType.equals(PdfName.SetOCGState))
      return new SetLayerState(baseObject);
    else if(actionType.equals(PdfName.Rendition))
      return new Render(baseObject);
    else if(actionType.equals(PdfName.Trans))
      return new DoTransition(baseObject);
    else if(actionType.equals(PdfName.GoTo3DView))
      return new GoTo3dView(baseObject);
    else // Custom action.
      return new Action(baseObject);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new action within the given document context.
  */
  protected Action(
    Document context,
    PdfName actionType
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
          PdfName.Action,
          actionType
        }
        )
      );
  }

  protected Action(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Action clone(
    Document context
    )
  {return (Action)super.clone(context);}

  /**
    Gets the actions to be performed after the current one.
  */
  @PDF(VersionEnum.PDF12)
  public ChainedActions getActions(
    )
  {
    PdfDirectObject nextObject = getBaseDataObject().get(PdfName.Next);
    return nextObject != null ? new ChainedActions(nextObject, this) : null;
  }

  /**
    @see #getActions()
  */
  public void setActions(
    ChainedActions value
    )
  {getBaseDataObject().put(PdfName.Next,value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}