/*
  Copyright 2009-2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.util;

import java.util.Hashtable;
import java.util.Map;

/**
  Bidirectional bijective map.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.0
*/
public class BiMap<K,V>
  extends Hashtable<K,V>
{
  // <class>
  // <static>
  // <fields>
  private static final long serialVersionUID = 1L;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private final Hashtable<V,K> inverseMap = new Hashtable<V,K>();
  // </fields>

  // <constructors>
  public BiMap(
    )
  {super();}

  public BiMap(
    int initialCapacity
    )
  {super(initialCapacity);}

  public BiMap(
    Map<? extends K,? extends V> map
    )
  {
    super();
    putAll(map);
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public boolean contains(
    Object value
    )
  {return containsValue(value);}

  @Override
  public boolean containsValue(
    Object value
    )
  {return inverseMap.containsKey(value);}

  public K getKey(
    V value
    )
  {return inverseMap.get(value);}

  @Override
  public V put(
    K key,
    V value
    )
  {
    V oldValue = super.put(key, value);
    if(oldValue != null)
    {inverseMap.remove(oldValue);}

    inverseMap.put(value, key);

    return oldValue;
  }

  @Override
  public void putAll(Map<? extends K,? extends V> map)
  {
    if(map == null)
      return;

    for(Map.Entry<? extends K,? extends V> entry : map.entrySet())
    {put(entry.getKey(),entry.getValue());}
  }

  public void putAllInverse(Map<? extends V,? extends K> map)
  {
    if(map == null)
      return;

    for(Map.Entry<? extends V,? extends K> entry : map.entrySet())
    {put(entry.getValue(),entry.getKey());}
  }

  @Override
  public V remove(Object key)
  {
    V value = super.remove(key);
    if(value != null)
    {inverseMap.remove(value);}

    return value;
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}