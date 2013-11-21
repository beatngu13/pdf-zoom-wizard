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
import org.pdfclown.documents.interaction.navigation.page.Transition;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  'Control drawing during a sequence of actions' action [PDF:1.6:8.5.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class DoTransition
  extends Action
{
  // <class>
  // <dynamic>
  // <constructors>
  /**
    Creates a new transition action within the given document context.
  */
  public DoTransition(
    Document context,
    Transition transition
    )
  {
    super(context, PdfName.Trans);
    setTransition(transition);
  }

  DoTransition(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public DoTransition clone(
    Document context
    )
  {return (DoTransition)super.clone(context);}

  /**
    Gets the transition effect to be used for the update of the display.
  */
  public Transition getTransition(
    )
  {return Transition.wrap(getBaseDataObject().get(PdfName.Trans));}

  /**
    @see #getTransition()
  */
  public void setTransition(
    Transition value
    )
  {getBaseDataObject().put(PdfName.Trans, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}