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

package org.pdfclown.documents.interaction.forms;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.documents.interaction.actions.JavaScript;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Form field actions [PDF:1.6:8.5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class FieldActions
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  public FieldActions(
    Document context
    )
  {super(context, new PdfDictionary());}

  FieldActions(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public FieldActions clone(
    Document context
    )
  {return (FieldActions)super.clone(context);}

  /**
    Gets a JavaScript action to be performed to recalculate the value
    of this field when that of another field changes.
  */
  public JavaScript getOnCalculate(
    )
  {return (JavaScript)Action.wrap(getBaseDataObject().get(PdfName.C));}

  /**
    Gets a JavaScript action to be performed when the user types a keystroke
    into a text field or combo box or modifies the selection in a scrollable list box.
  */
  public JavaScript getOnChange(
    )
  {return (JavaScript)Action.wrap(getBaseDataObject().get(PdfName.K));}

  /**
    Gets a JavaScript action to be performed before the field is formatted
    to display its current value.
    <p>This action can modify the field's value before formatting.</p>
  */
  public JavaScript getOnFormat(
    )
  {return (JavaScript)Action.wrap(getBaseDataObject().get(PdfName.F));}

  /**
    Gets a JavaScript action to be performed when the field's value is changed.
    This action can check the new value for validity.
  */
  public JavaScript getOnValidate(
    )
  {return (JavaScript)Action.wrap(getBaseDataObject().get(PdfName.V));}

  /**
    @see #getOnCalculate()
  */
  public void setOnCalculate(
    JavaScript value
    )
  {getBaseDataObject().put(PdfName.C, value.getBaseObject());}

  /**
    @see #getOnChange()
  */
  public void setOnChange(
    JavaScript value
    )
  {getBaseDataObject().put(PdfName.K, value.getBaseObject());}

  /**
    @see #getOnFormat()
  */
  public void setOnFormat(
    JavaScript value
    )
  {getBaseDataObject().put(PdfName.F, value.getBaseObject());}

  /**
    @see #getOnValidate()
  */
  public void setOnValidate(
    JavaScript value
    )
  {getBaseDataObject().put(PdfName.V, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}