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
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Interactive form (AcroForm) [PDF:1.6:8.6.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class Form
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Form wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Form(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Form(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Fields},
        new PdfDirectObject[]
        {new PdfArray()}
        )
      );
  }

  private Form(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Form clone(
    Document context
    )
  {return (Form)super.clone(context);}

  /**
    Gets the fields collection.
  */
  public Fields getFields(
    )
  {return new Fields(getBaseDataObject().get(PdfName.Fields, PdfArray.class));}

  /**
    Gets the default resources used by fields.
  */
  public Resources getResources(
    )
  {return Resources.wrap(getBaseDataObject().get(PdfName.DR, PdfDictionary.class));}

  /**
    @see #getFields()
  */
  public void setFields(
    Fields value
    )
  {getBaseDataObject().put(PdfName.Fields, value.getBaseObject());}

  /**
    @see #getResources()
  */
  public void setResources(
    Resources value
    )
  {getBaseDataObject().put(PdfName.DR, value.getBaseObject());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}