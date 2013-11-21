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

import java.util.EnumSet;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Abstract 'go to non-local destination' action.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 09/24/12
*/
@PDF(VersionEnum.PDF11)
public abstract class GoToNonLocal<T extends Destination>
  extends GoToDestination<T>
{
  // <class>
  // <constructors>
  protected GoToNonLocal(
    Document context,
    PdfName actionType,
    FileSpecification<?> destinationFile,
    T destination
    )
  {
    super(context, actionType, destination);
    setDestinationFile(destinationFile);
  }

  protected GoToNonLocal(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the file in which the destination is located.
  */
  public FileSpecification<?> getDestinationFile(
    )
  {return FileSpecification.wrap(getBaseDataObject().get(PdfName.F));}

  /**
    Gets the action options.
  */
  public EnumSet<OptionsEnum> getOptions(
    )
  {
    EnumSet<OptionsEnum> options = EnumSet.noneOf(OptionsEnum.class);
    PdfDirectObject optionObject = getBaseDataObject().get(PdfName.NewWindow);
    if(optionObject != null
      && ((PdfBoolean)optionObject).getValue())
    {options.add(OptionsEnum.NewWindow);}
    return options;
  }

  /**
    @see #getDestinationFile()
  */
  public void setDestinationFile(
    FileSpecification<?> value
    )
  {getBaseDataObject().put(PdfName.F, value != null ? value.getBaseObject() : null);}

  /**
    @see #getOptions()
  */
  public void setOptions(
    EnumSet<OptionsEnum> value
    )
  {
    if(value.contains(OptionsEnum.NewWindow))
    {getBaseDataObject().put(PdfName.NewWindow,PdfBoolean.True);}
    else if(value.contains(OptionsEnum.SameWindow))
    {getBaseDataObject().put(PdfName.NewWindow,PdfBoolean.False);}
    else
    {getBaseDataObject().remove(PdfName.NewWindow);} // NOTE: Forcing the absence of this entry ensures that the viewer application should behave in accordance with the current user preference.
  }
  // </public>
  // </interface>
  // </class>
}
