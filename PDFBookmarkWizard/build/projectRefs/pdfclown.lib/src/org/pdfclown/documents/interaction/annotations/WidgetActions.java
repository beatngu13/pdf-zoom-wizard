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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

/**
  Widget actions [PDF:1.6:8.5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class WidgetActions
  extends AnnotationActions
{
  // <class>
  // <dynamic>
  // <constructors>
  public WidgetActions(
    Annotation parent
    )
  {super(parent);}

  WidgetActions(
    Annotation parent,
    PdfDirectObject baseObject
    )
  {super(parent, baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public WidgetActions clone(
    Document context
    )
  {throw new NotImplementedException();} // TODO: verify parent reference.

  /**
    Gets the action to be performed when the annotation loses the input focus.
  */
  public Action getOnBlur(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.Bl));}

  /**
    Gets the action to be performed when the annotation receives the input focus.
  */
  public Action getOnFocus(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.Fo));}

  /**
    @see #getOnBlur()
  */
  public void setOnBlur(
    Action value
    )
  {getBaseDataObject().put(PdfName.Bl, value.getBaseObject());}

  /**
    @see #getOnFocus()
  */
  public void setOnFocus(
    Action value
    )
  {getBaseDataObject().put(PdfName.Fo, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}