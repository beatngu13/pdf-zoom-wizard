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
import org.pdfclown.documents.interaction.annotations.Widget;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.util.EnumUtils;

/**
  Combo box [PDF:1.6:8.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class ComboBox
  extends ChoiceField
{
  // <class>
  // <dynamic>
  // <constructors>
  /**
    Creates a new combobox within the given document context.
  */
  public ComboBox(
    String name,
    Widget widget
    )
  {
    super(name, widget);
    setFlags(EnumUtils.mask(getFlags(), FlagsEnum.Combo, true));
  }

  ComboBox(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ComboBox clone(
    Document context
    )
  {return (ComboBox)super.clone(context);}

  /**
    Gets whether the text is editable.
  */
  public boolean isEditable(
    )
  {return getFlags().contains(FlagsEnum.Edit);}

  /**
    Gets whether the edited text is spell checked.
  */
  public boolean isSpellChecked(
    )
  {return !getFlags().contains(FlagsEnum.DoNotSpellCheck);}

  /**
    @see #isEditable()
  */
  public void setEditable(
    boolean value
    )
  {setFlags(EnumUtils.mask(getFlags(), FlagsEnum.Edit, value));}

  /**
    @see #isSpellChecked()
  */
  public void setSpellChecked(
    boolean value
    )
  {setFlags(EnumUtils.mask(getFlags(), FlagsEnum.DoNotSpellCheck, !value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}