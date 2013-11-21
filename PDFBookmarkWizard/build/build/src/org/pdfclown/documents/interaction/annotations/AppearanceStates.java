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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.MapEntry;
import org.pdfclown.util.NotImplementedException;

/**
  Appearance states [PDF:1.6:8.4.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public final class AppearanceStates
  extends PdfObjectWrapper<PdfDataObject>
  implements Map<PdfName,FormXObject>
{
  // <class>
  // <dynamic>
  // <fields>
  private final Appearance appearance;

  private final PdfName statesKey;
  // </fields>

  // <constructors>
  AppearanceStates(
    PdfName statesKey,
    Appearance appearance
    )
  {
    super(appearance.getBaseDataObject().get(statesKey));

    this.appearance = appearance;
    this.statesKey = statesKey;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public AppearanceStates clone(
    Document context
    )
  {throw new NotImplementedException();} // TODO: verify appearance reference.

  /**
    Gets the appearance associated to these states.
  */
  public Appearance getAppearance(
    )
  {return appearance;}

//TODO
  /**
    Gets the key associated to a given value.
  */
//   public PdfName getKey(
//     FormXObject value
//     )
//   {return getBaseDataObject().getKey(value.getBaseObject());}

  // <Map>
  @Override
  public void clear(
    )
  {ensureDictionary().clear();}

  @Override
  public boolean containsKey(
    Object key
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return false;
    else if(baseDataObject instanceof PdfStream) // Single state.
      return (key == null);
    else // Multiple state.
      return ((PdfDictionary)baseDataObject).containsKey(key);
  }

  @Override
  public boolean containsValue(
    Object value
    )
  {
    if(!(value instanceof FormXObject))
      return false;

    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return false;
    else if(baseDataObject instanceof PdfStream) // Single state.
      return ((FormXObject)value).getBaseObject().equals(getBaseObject());
    else // Multiple state.
      return ((PdfDictionary)baseDataObject).containsValue(
        ((FormXObject)value).getBaseObject()
        );
  }

  @Override
  public Set<Map.Entry<PdfName,FormXObject>> entrySet(
    )
  {
    HashSet<Map.Entry<PdfName,FormXObject>> entrySet = new HashSet<Map.Entry<PdfName,FormXObject>>();
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No state.
      { /* NOOP. */ }
      else if(baseDataObject instanceof PdfStream) // Single state.
      {
        entrySet.add(
          new MapEntry<PdfName,FormXObject>(
            null,
            FormXObject.wrap(getBaseObject())
            )
          );
      }
      else // Multiple state.
      {
        for(Map.Entry<PdfName,PdfDirectObject> entry : ((PdfDictionary)baseDataObject).entrySet())
        {
          entrySet.add(
            new MapEntry<PdfName,FormXObject>(
              entry.getKey(),
              FormXObject.wrap(entry.getValue())
              )
            );
        }
      }
    }
    return entrySet;
  }

  @Override
  public boolean equals(
    Object object
    )
  {throw new NotImplementedException();}

  @Override
  public FormXObject get(
    Object key
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return null;
    else if(key == null)
    {
      if(baseDataObject instanceof PdfStream) // Single state.
        return FormXObject.wrap(getBaseObject());
      else // Multiple state, but invalid key.
        return null;
    }
    else // Multiple state.
      return FormXObject.wrap(((PdfDictionary)baseDataObject).get(key));
  }

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return true;
    else if(baseDataObject instanceof PdfStream) // Single state.
      return false;
    else // Multiple state.
      return ((PdfDictionary)baseDataObject).isEmpty();
  }

  @Override
  public Set<PdfName> keySet(
    )
  {throw new NotImplementedException();}

  @Override
  public FormXObject put(
    PdfName key,
    FormXObject value
    )
  {
    PdfDirectObject previousValue;
    if(key == null) // Single state.
    {
      setBaseObject(value.getBaseObject());
      previousValue = appearance.getBaseDataObject().put(statesKey,getBaseObject());
    }
    else // Multiple state.
    {previousValue = ensureDictionary().put(key,value.getBaseObject());}

    if(PdfObject.resolve(previousValue) instanceof PdfStream)
      return FormXObject.wrap(previousValue);
    else
      return null;
  }

  @Override
  public void putAll(
    Map<? extends PdfName,? extends FormXObject> entries
    )
  {throw new NotImplementedException();}

  @Override
  public FormXObject remove(
    Object key
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return null;
    else
    {
      PdfDirectObject previousValue;
      if(baseDataObject instanceof PdfStream) // Single state.
      {
        if(key == null)
        {
          setBaseObject(null);
          previousValue = appearance.getBaseDataObject().remove(statesKey);
        }
        else // Invalid key.
        {previousValue = null;}
      }
      else // Multiple state.
      {previousValue = ((PdfDictionary)baseDataObject).remove(key);}

      if(PdfObject.resolve(previousValue) instanceof PdfStream)
        return FormXObject.wrap(previousValue);
      else
        return null;
    }
  }

  @Override
  public int size(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject == null) // No state.
      return 0;
    else if(baseDataObject instanceof PdfStream) // Single state.
      return 1;
    else // Multiple state.
      return ((PdfDictionary)baseDataObject).size();
  }

  @Override
  public Collection<FormXObject> values(
    )
  {throw new NotImplementedException();}
  // </Map>
  // </public>

  // <private>
  private PdfDictionary ensureDictionary(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(!(baseDataObject instanceof PdfDictionary))
    {
      /*
        NOTE: Single states are erased as they have no valid key
        to be consistently integrated within the dictionary.
      */
      setBaseObject((PdfDirectObject)(baseDataObject = new PdfDictionary()));
      appearance.getBaseDataObject().put(statesKey,(PdfDictionary)baseDataObject);
    }
    return (PdfDictionary)baseDataObject;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}