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

package org.pdfclown.documents.contents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.layers.Layer;
import org.pdfclown.documents.contents.layers.LayerMembership;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Private information meaningful to the program (application or plugin extension)
  creating the marked content [PDF:1.6:10.5.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public class PropertyList
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps the specified base object into a property list object.

    @param baseObject Base object of a property list object.
    @return Property list object corresponding to the base object.
  */
  public static PropertyList wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfName type = (PdfName)((PdfDictionary)baseObject.resolve()).get(PdfName.Type);
    if(Layer.TypeName.equals(type))
      return Layer.wrap(baseObject);
    else if(LayerMembership.TypeName.equals(type))
      return LayerMembership.wrap(baseObject);
    else
      return new PropertyList(baseObject);
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public PropertyList(
    Document context,
    PdfDictionary baseDataObject
    )
  {super(context, baseDataObject);}

  protected PropertyList(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PropertyList clone(
    Document context
    )
  {return (PropertyList)super.clone(context);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}