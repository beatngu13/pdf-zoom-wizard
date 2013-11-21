/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interchange.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.util.MapEntry;

/**
  Document information [PDF:1.6:10.2.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Information
  extends PdfObjectWrapper<PdfDictionary>
  implements Map<PdfName,Object>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Information wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Information(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Information(
    Document context
    )
  {super(context, new PdfDictionary());}

  private Information(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Information clone(
    Document context
    )
  {return (Information)super.clone(context);}

  public String getAuthor(
  )
  {return (String)get(PdfName.Author);}

  public Date getCreationDate(
    )
  {return (Date)get(PdfName.CreationDate);}

  public String getCreator(
    )
  {return (String)get(PdfName.Creator);}

  @PDF(VersionEnum.PDF11)
  public String getKeywords(
    )
  {return (String)get(PdfName.Keywords);}

  @PDF(VersionEnum.PDF11)
  public Date getModificationDate(
    )
  {return (Date)get(PdfName.ModDate);}

  public String getProducer(
    )
  {return (String)get(PdfName.Producer);}

  @PDF(VersionEnum.PDF11)
  public String getSubject(
    )
  {return (String)get(PdfName.Subject);}

  @PDF(VersionEnum.PDF11)
  public String getTitle(
    )
  {return (String)get(PdfName.Title);}

  public void setAuthor(
    String value
    )
  {put(PdfName.Author, value);}

  public void setCreationDate(
    Date value
    )
  {put(PdfName.CreationDate, value);}

  public void setCreator(
    String value
    )
  {put(PdfName.Creator, value);}

  public void setKeywords(
    String value
    )
  {put(PdfName.Keywords, value);}

  public void setModificationDate(
    Date value
    )
  {put(PdfName.ModDate, value);}

  public void setProducer(
    String value
    )
  {put(PdfName.Producer, value);}

  public void setSubject(
    String value
    )
  {put(PdfName.Subject, value);}

  public void setTitle(
    String value
    )
  {put(PdfName.Title, value);}

  // <Map>
  @Override
  public void clear(
    )
  {
    getBaseDataObject().clear();
    setModificationDate(new Date());
  }

  @Override
  public boolean containsKey(
    Object key
    )
  {return getBaseDataObject().containsKey(key);}

  @Override
  public boolean containsValue(
    Object value
    )
  {
    for(PdfDirectObject item : getBaseDataObject().values())
    {
      if(value.equals(PdfSimpleObject.getValue(item)))
        return true;
    }
    return false;
  }

  @Override
  public Set<java.util.Map.Entry<PdfName,Object>> entrySet(
    )
  {
    Set<Map.Entry<PdfName,Object>> entrySet = new HashSet<Map.Entry<PdfName,Object>>();
    for(PdfName key : getBaseDataObject().keySet())
    {entrySet.add(new MapEntry<PdfName,Object>(key, get(key)));}
    return entrySet;
  }

  @Override
  public Object get(
    Object key
    )
  {return PdfSimpleObject.getValue(getBaseDataObject().get(key));}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public Set<PdfName> keySet(
    )
  {return getBaseDataObject().keySet();}

  @Override
  public Object put(
    PdfName key,
    Object value
    )
  {
    onChange(key);
    return PdfSimpleObject.getValue(getBaseDataObject().put(key, PdfSimpleObject.get(value)));
  }

  @Override
  public void putAll(
    Map<? extends PdfName,? extends Object> map
    )
  {
    for(Map.Entry<? extends PdfName,? extends Object> entry : map.entrySet())
    {put(entry.getKey(), entry.getValue());}
  }

  @Override
  public Object remove(
    Object key
    )
  {
    onChange((PdfName)key);
    return PdfSimpleObject.getValue(getBaseDataObject().remove(key));
  }

  @Override
  public int size(
    )
  {return getBaseDataObject().size();}

  @Override
  public Collection<Object> values(
    )
  {
    List<Object> values = new ArrayList<Object>();
    for(PdfDirectObject item : getBaseDataObject().values())
    {values.add(PdfSimpleObject.getValue(item));}
    return values;
  }
  // </Map>
  // </public>

  // <private>
  //TODO: Listen to baseDataObject's onChange notification?
  private void onChange(
    PdfName key
    )
  {
    if(!getBaseDataObject().isUpdated() && !PdfName.ModDate.equals(key))
    {setModificationDate(new Date());}
  }
  // </private>
  // </interface>
  // </class>
}