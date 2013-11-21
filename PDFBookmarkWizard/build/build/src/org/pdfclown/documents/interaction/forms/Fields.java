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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.NotImplementedException;

/**
  Interactive form fields [PDF:1.6:8.6.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class Fields
  extends PdfObjectWrapper<PdfArray>
  implements Map<String,Field>
{
  // <class>
  // <dynamic>
  // <constructors>
  public Fields(
    Document context
    )
  {super(context, new PdfArray());}

  Fields(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  public boolean add(
    Field value
    )
  {return getBaseDataObject().add(value.getBaseObject());}

  @Override
  public Fields clone(
    Document context
    )
  {return (Fields)super.clone(context);}

  // <Map>
  @Override
  public void clear(
    )
  {getBaseDataObject().clear();}

  @Override
  public boolean containsKey(
    Object key
    )
  //TODO: avoid getter (use raw matching).
  {return get(key) != null;}

  @Override
  public boolean containsValue(
    Object value
    )
  {throw new NotImplementedException();}

  @Override
  public Set<Map.Entry<String,Field>> entrySet(
    )
  {throw new NotImplementedException();}

  @Override
  public Field get(
    Object key
    )
  {
    /*
      TODO: It is possible for different field dictionaries to have the SAME fully qualified field
      name if they are descendants of a common ancestor with that name and have no
      partial field names (T entries) of their own. Such field dictionaries are different
      representations of the same underlying field; they should differ only in properties
      that specify their visual appearance. In particular, field dictionaries with the same
      fully qualified field name must have the same field type (FT), value (V), and default
      value (DV).
     */
    PdfReference valueFieldReference = null;
    {
      Iterator<String> partialNamesIterator = Arrays.asList(((String)key).split("\\.")).iterator();
      Iterator<PdfDirectObject> fieldObjectsIterator = getBaseDataObject().iterator();
      while(partialNamesIterator.hasNext())
      {
        String partialName = partialNamesIterator.next();
        valueFieldReference = null;
        while(fieldObjectsIterator != null && fieldObjectsIterator.hasNext())
        {
          PdfReference fieldReference = (PdfReference)fieldObjectsIterator.next();
          PdfDictionary fieldDictionary = (PdfDictionary)fieldReference.getDataObject();
          PdfTextString fieldName = (PdfTextString)fieldDictionary.get(PdfName.T);
          if(fieldName != null && fieldName.getValue().equals(partialName))
          {
            valueFieldReference = fieldReference;
            PdfArray kidFieldObjects = (PdfArray)fieldDictionary.resolve(PdfName.Kids);
            fieldObjectsIterator = (kidFieldObjects == null ? null : kidFieldObjects.iterator());
            break;
          }
        }
        if(valueFieldReference == null)
          break;
      }
    }
    return Field.wrap(valueFieldReference);
  }

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public Set<String> keySet(
    )
  {
    throw new NotImplementedException();
  //TODO: retrieve all the full names (keys)!!!
  }

  @Override
  public Field put(
    String key,
    Field value
    )
  {throw new NotImplementedException();
/*
TODO:put the field into the correct position, based on the full name (key)!!!
*/
  }

  @Override
  public void putAll(
    Map<? extends String,? extends Field> entries
    )
  {throw new NotImplementedException();}

  @Override
  public Field remove(
    Object key
    )
  {
    Field field = get(key);
    if(field == null)
      return null;

    PdfArray fieldObjects;
    {
      PdfReference fieldParentReference = (PdfReference)field.getBaseDataObject().get(PdfName.Parent);
      if(fieldParentReference == null)
      {fieldObjects = getBaseDataObject();}
      else
      {fieldObjects = (PdfArray)((PdfDictionary)fieldParentReference.getDataObject()).resolve(PdfName.Kids);}
    }
    return (fieldObjects.remove(field.getBaseObject()) ? field : null);
  }

  @Override
  public int size(
    )
  {return values().size();}

  @Override
  public Collection<Field> values(
    )
  {
    List<Field> values = new ArrayList<Field>();
    retrieveValues(getBaseDataObject(), values);

    return values;
  }
  // </Map>
  // </public>

  // <private>
  private void retrieveValues(
    PdfArray fieldObjects,
    List<Field> values
    )
  {
    for(PdfDirectObject fieldObject : fieldObjects)
    {
      PdfReference fieldReference = (PdfReference)fieldObject;
      PdfArray kidReferences = (PdfArray)((PdfDictionary)fieldReference.getDataObject()).resolve(PdfName.Kids);
      PdfDictionary kidObject;
      if(kidReferences == null)
      {kidObject = null;}
      else
      {kidObject = (PdfDictionary)((PdfReference)kidReferences.get(0)).getDataObject();}
      // Terminal field?
      if(kidObject == null // Merged single widget annotation.
        || (!kidObject.containsKey(PdfName.FT) // Multiple widget annotations.
          && kidObject.containsKey(PdfName.Subtype)
          && kidObject.get(PdfName.Subtype).equals(PdfName.Widget)))
      {values.add(Field.wrap(fieldReference));}
      else // Non-terminal field.
      {retrieveValues(kidReferences, values);}
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}