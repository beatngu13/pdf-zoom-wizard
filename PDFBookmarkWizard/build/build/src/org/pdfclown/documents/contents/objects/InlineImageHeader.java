/*
  Copyright 2007-2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
  Inline image entries (anonymous) operation [PDF:1.6:4.8.6].
  <p>This is a figurative operation necessary to constrain the inline image entries section
  within the content stream model.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.0
*/
@PDF(VersionEnum.PDF10)
public final class InlineImageHeader
  extends Operation
  implements Map<PdfName,PdfDirectObject>
{
  // <class>
  // <dynamic>
  // <constructors>
  // [FIX:0.0.4:2] Null operator.
  public InlineImageHeader(
    List<PdfDirectObject> operands
    )
  {super("",operands);}
  // </constructors>

  // <interface>
  // <public>
  // <Map>
  @Override
  public void clear(
    )
  {operands.clear();}

  @Override
  public boolean containsKey(
    Object key
    )
  {return getKeyIndex(key) != null;}

  @Override
  public boolean containsValue(
    Object value
    )
  {
    for(
      int index = 1,
        length = operands.size() - 1;
      index < length;
      index += 2
      )
    {
      if(operands.get(index).equals(value))
        return true;
    }
    return false;
  }

  @Override
  public Set<Map.Entry<PdfName,PdfDirectObject>> entrySet(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean equals(
    Object object
    )
  {throw new NotImplementedException();}

  @Override
  public PdfDirectObject get(
    Object key
    )
  {
    Integer index = getKeyIndex(key);
    if(index == null)
      return null;

    return operands.get(index+1);
  }

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {return operands.isEmpty();}

  @Override
  public Set<PdfName> keySet(
    )
  {
    Set<PdfName> keys = new HashSet<PdfName>();
    for(
      int index = 0,
        length = operands.size() - 1;
      index < length;
      index += 2
      )
    {keys.add((PdfName)operands.get(index));}
    return keys;
  }

  @Override
  public PdfDirectObject put(
    PdfName key,
    PdfDirectObject value
    )
  {
    PdfDirectObject oldValue;
    {
      Integer index = getKeyIndex(key);
      if(index == null)
      {
        oldValue = null;

        operands.add(key);
        operands.add(value);
      }
      else
      {
        oldValue = operands.get(index+1);

        operands.set(index,key);
        operands.set(index+1,value);
      }
    }
    return oldValue;
  }

  @Override
  public void putAll(
    Map<? extends PdfName,? extends PdfDirectObject> entries
    )
  {
    for(Map.Entry<? extends PdfName,? extends PdfDirectObject> entry : entries.entrySet())
    {put(entry.getKey(),entry.getValue());}
  }

  @Override
  public PdfDirectObject remove(
    Object key
    )
  {
    PdfDirectObject oldValue;
    {
      Integer index = getKeyIndex(key);
      if(index == null)
      {oldValue = null;}
      else
      {
        oldValue = operands.get(index+1);

        operands.remove(index);
        operands.remove(index);
      }
    }
    return oldValue;
  }

  @Override
  public int size(
    )
  {return operands.size() / 2;}

  @Override
  public Collection<PdfDirectObject> values(
    )
  {
    Collection<PdfDirectObject> values = new ArrayList<PdfDirectObject>();
    for(
      int index = 1,
        length = operands.size() - 1;
      index < length;
      index += 2
      )
    {values.add(operands.get(index));}
    return values;
  }
  // </Map>
  // </public>

  // <private>
  private Integer getKeyIndex(
    Object key
    )
  {
    for(
      int index = 0,
        length = operands.size() - 1;
      index < length;
      index += 2
      )
    {
      if(operands.get(index).equals(key))
        return index;
    }
    return null;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}