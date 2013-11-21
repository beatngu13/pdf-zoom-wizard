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

import java.util.ArrayList;
import java.util.Collection;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.documents.interaction.forms.Field;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfTextString;

/**
  'Toggle the visibility of one or more annotations on the screen' action [PDF:1.6:8.5.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public final class ToggleVisibility
  extends Action
{
  // <class>
  // <dynamic>
  // <constructors>
  /**
    Creates a new action within the given document context.
  */
  public ToggleVisibility(
    Document context,
    Collection<PdfObjectWrapper<?>> objects,
    boolean visible
    )
  {
    super(context, PdfName.Hide);
    setObjects(objects);
    setVisible(visible);
  }

  ToggleVisibility(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ToggleVisibility clone(
    Document context
    )
  {return (ToggleVisibility)super.clone(context);}

  /**
    Gets the annotations (or associated form fields) to be affected.
  */
  public Collection<PdfObjectWrapper<?>> getObjects(
    )
  {
    ArrayList<PdfObjectWrapper<?>> objects = new ArrayList<PdfObjectWrapper<?>>();
    {
      PdfDirectObject objectsObject = getBaseDataObject().get(PdfName.T);
      fillObjects(objectsObject, objects);
    }
    return objects;
  }

  /**
    Gets whether to show the annotations.
  */
  public boolean isVisible(
    )
  {
    PdfBoolean hideObject = (PdfBoolean)getBaseDataObject().get(PdfName.H);
    return hideObject != null
      ? !hideObject.getValue()
      : false;
  }

  /**
    @see #getObjects()
  */
  public void setObjects(
    Collection<PdfObjectWrapper<?>> value
    )
  {
    PdfArray objectsDataObject = new PdfArray();
    for(PdfObjectWrapper<?> item : value)
    {
      if(item instanceof Annotation)
        objectsDataObject.add(
          item.getBaseObject()
          );
      else if(item instanceof Field)
        objectsDataObject.add(
          new PdfTextString(((Field)item).getFullName())
          );
      else
        throw new IllegalArgumentException(
          "Invalid 'Hide' action target type (" + item.getClass().getName() + ").\n"
            + "It MUST be either an annotation or a form field."
          );
    }
    getBaseDataObject().put(PdfName.T, objectsDataObject);
  }

  /**
    @see #isVisible()
  */
  public void setVisible(
    boolean value
    )
  {getBaseDataObject().put(PdfName.H, PdfBoolean.get(!value));}
  // </public>

  // <private>
  private void fillObjects(
    PdfDataObject objectObject,
    Collection<PdfObjectWrapper<?>> objects
    )
  {
    PdfDataObject objectDataObject = PdfObject.resolve(objectObject);
    if(objectDataObject instanceof PdfArray) // Multiple objects.
    {
      for(PdfDirectObject itemObject : (PdfArray)objectDataObject)
      {fillObjects(itemObject,objects);}
    }
    else // Single object.
    {
      if(objectDataObject instanceof PdfDictionary) // Annotation.
        objects.add(
          Annotation.wrap((PdfReference)objectObject)
          );
      else if(objectDataObject instanceof PdfTextString) // Form field (associated to widget annotations).
        objects.add(
          getDocument().getForm().getFields().get(
            ((PdfTextString)objectDataObject).getValue()
            )
          );
      else // Invalid object type.
        throw new RuntimeException(
          "Invalid 'Hide' action target type (" + objectDataObject.getClass().getName() + ").\n"
            + "It should be either an annotation or a form field."
          );
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}