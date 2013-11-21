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

package org.pdfclown.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Chunk;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.XRefEntry;
import org.pdfclown.util.NotImplementedException;

/**
  PDF dictionary object [PDF:1.6:3.2.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.0
  @version 0.1.2, 12/28/12
*/
public final class PdfDictionary
  extends PdfDirectObject
  implements Map<PdfName,PdfDirectObject>
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginDictionaryChunk = Encoding.Pdf.encode(Keyword.BeginDictionary);
  private static final byte[] EndDictionaryChunk = Encoding.Pdf.encode(Keyword.EndDictionary);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  Map<PdfName,PdfDirectObject> entries;

  private PdfObject parent;
  private boolean updated;
  private boolean updateable = true;
  private boolean virtual;
  // </fields>

  // <constructors>
  public PdfDictionary(
    )
  {entries = new HashMap<PdfName,PdfDirectObject>();}

  public PdfDictionary(
    int capacity
    )
  {entries = new HashMap<PdfName,PdfDirectObject>(capacity);}

  public PdfDictionary(
    PdfName[] keys,
    PdfDirectObject[] values
    )
  {
    this(values.length);

    setUpdateable(false);
    for(
      int index = 0;
      index < values.length;
      index++
      )
    {put(keys[index], values[index]);}
    setUpdateable(true);
  }

  public PdfDictionary(
    Map<PdfName,PdfDirectObject> entries
    )
  {
    this(entries.size());

    setUpdateable(false);
    for(Entry<PdfName,PdfDirectObject> entry : entries.entrySet())
    {put(entry.getKey(), (PdfDirectObject)include(entry.getValue()));}
    setUpdateable(true);
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  @Override
  public PdfDictionary clone(
    File context
    )
  {return (PdfDictionary)super.clone(context);}

  @Override
  public int compareTo(
    PdfDirectObject obj
    )
  {throw new NotImplementedException();}

  /**
    Gets the value corresponding to the given key, forcing its instantiation as a direct object in
    case of missing entry.

    @param key Key whose associated value is to be returned.
    @param valueClass Class to use for instantiating the value in case of missing entry.
    @since 0.1.2
  */
  public <T extends PdfDataObject> PdfDirectObject get(
    PdfName key,
    Class<T> valueClass
    )
  {return get(key, valueClass, true);}

  /**
    Gets the value corresponding to the given key, forcing its instantiation in case of missing
    entry.

    @param key Key whose associated value is to be returned.
    @param valueClass Class to use for instantiating the value in case of missing entry.
    @param direct Whether the item has to be instantiated directly within its container instead of
    being referenced through an indirect object.
    @since 0.1.2
  */
  public <T extends PdfDataObject> PdfDirectObject get(
    PdfName key,
    Class<T> valueClass,
    boolean direct
    )
  {
    PdfDirectObject value = get(key);
    if(value == null)
    {
      /*
        NOTE: The null-object placeholder MUST NOT perturb the existing structure; therefore:
          - it MUST be marked as virtual in order not to unnecessarily serialize it;
          - it MUST be put into this dictionary without affecting its update status.
      */
      try
      {
        value = (PdfDirectObject)include(direct
          ? valueClass.newInstance()
          : new PdfIndirectObject(getFile(), valueClass.newInstance(), new XRefEntry(0, 0)).getReference());
        entries.put(key, value);
        value.setVirtual(true);
      }
      catch(Exception e)
      {throw new RuntimeException(valueClass.getSimpleName() + " failed to instantiate.", e);}
    }
    return value;
  }

  /**
    Gets the key associated to the specified value.
  */
  public PdfName getKey(
    PdfDirectObject value
    )
  {
    /*
      NOTE: Current PdfDictionary implementation doesn't support bidirectional maps, to say that the
      only currently-available way to retrieve a key from a value is to iterate the whole map (really
      poor performance!).
    */
    for(Map.Entry<PdfName,PdfDirectObject> entry : entries.entrySet())
    {
      if(entry.getValue().equals(value))
        return entry.getKey();
    }
    return null;
  }

  @Override
  public PdfObject getParent(
    )
  {return parent;}

  @Override
  public boolean isUpdateable(
    )
  {return updateable;}

  @Override
  public boolean isUpdated(
    )
  {return updated;}

  /**
    Gets the dereferenced value corresponding to the given key.
    <p>This method takes care to resolve the value returned by {@link #get(Object)}.</p>

    @param key Key whose associated value is to be returned.
    @return null, if the map contains no mapping for this key.
    @since 0.0.8
  */
  public PdfDataObject resolve(
    PdfName key
    )
  {return resolve(get(key));}

  /**
    Gets the dereferenced value corresponding to the given key, forcing its instantiation in case of
    missing entry.
    <p>This method takes care to resolve the value returned by {@link #get(PdfName, Class)}.</p>

    @param key Key whose associated value is to be returned.
    @param valueClass Class to use for instantiating the value in case of missing entry.
    @return null, if the map contains no mapping for this key.
    @since 0.1.2
  */
  @SuppressWarnings("unchecked")
  public <T extends PdfDataObject> T resolve(
    PdfName key,
    Class<T> valueClass
    )
  {return (T)resolve(get(key,valueClass));}

  @Override
  public void setUpdateable(
    boolean value
    )
  {updateable = value;}

  @Override
  public PdfDictionary swap(
    PdfObject other
    )
  {
    PdfDictionary otherDictionary = (PdfDictionary)other;
    Map<PdfName,PdfDirectObject> otherEntries = otherDictionary.entries;
    // Update the other!
    otherDictionary.entries = this.entries;
    otherDictionary.update();
    // Update this one!
    this.entries = otherEntries;
    this.update();
    return this;
  }

  @Override
  public String toString(
    )
  {
    StringBuilder buffer = new StringBuilder();
    {
      // Begin.
      buffer.append("<< ");
      // Entries.
      for(Map.Entry<PdfName,PdfDirectObject> entry : entries.entrySet())
      {
        // Entry...
        // ...key.
        buffer.append(entry.getKey().toString()).append(" ");
        // ...value.
        buffer.append(PdfDirectObject.toString(entry.getValue())).append(" ");
      }
      // End.
      buffer.append(">>");
    }
    return buffer.toString();
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {
    // Begin.
    stream.write(BeginDictionaryChunk);
    // Entries.
    for(Map.Entry<PdfName,PdfDirectObject> entry : entries.entrySet())
    {
      PdfDirectObject value = entry.getValue();
      if(value != null && value.isVirtual())
        continue;

      // Entry...
      // ...key.
      entry.getKey().writeTo(stream, context); stream.write(Chunk.Space);
      // ...value.
      PdfDirectObject.writeTo(stream, context, value); stream.write(Chunk.Space);
    }
    // End.
    stream.write(EndDictionaryChunk);
  }

  // <Map>
  @Override
  public void clear(
    )
  {
    for(PdfName key : new ArrayList<PdfName>(entries.keySet()))
    {remove(key);}
  }

  @Override
  public boolean containsKey(
    Object key
    )
  {return entries.containsKey(key);}

  @Override
  public boolean containsValue(
    Object value
    )
  {return entries.containsValue(value);}

  @Override
  public Set<Map.Entry<PdfName,PdfDirectObject>> entrySet(
    )
  {return entries.entrySet();}

  @Override
  public boolean equals(
    Object object
    )
  {
    return super.equals(object)
      || (object != null
        && object.getClass().equals(getClass())
        && ((PdfDictionary)object).entries.equals(entries));
  }

  @Override
  public PdfDirectObject get(
    Object key
    )
  {return entries.get(key);}

  @Override
  public int hashCode(
    )
  {return entries.hashCode();}

  @Override
  public boolean isEmpty(
    )
  {return entries.isEmpty();}

  @Override
  public Set<PdfName> keySet(
    )
  {return entries.keySet();}

  @Override
  public PdfDirectObject put(
    PdfName key,
    PdfDirectObject value
    )
  {
    PdfDirectObject oldValue;
    if(value == null)
    {oldValue = remove(key);}
    else
    {
      oldValue = entries.put(key, value = (PdfDirectObject)include(value));
      exclude(oldValue);
      update();
    }
    return oldValue;
  }

  @Override
  public void putAll(
    Map<? extends PdfName,? extends PdfDirectObject> entries
    )
  {
    for(Entry<? extends PdfName,? extends PdfDirectObject> entry : entries.entrySet())
    {put(entry.getKey(), entry.getValue());}
  }

  @Override
  public PdfDirectObject remove(
    Object key
    )
  {
    PdfDirectObject oldValue = entries.remove(key);
    exclude(oldValue);
    update();
    return oldValue;
  }

  @Override
  public int size(
    )
  {return entries.size();}

  @Override
  public Collection<PdfDirectObject> values(
    )
  {return entries.values();}
  // </Map>
  // </public>

  // <protected>
  @Override
  protected boolean isVirtual(
    )
  {return virtual;}

  @Override
  protected void setUpdated(
    boolean value
    )
  {updated = value;}

  @Override
  protected void setVirtual(
    boolean value
    )
  {virtual = value;}
  // </protected>

  // <internal>
  @Override
  void setParent(
    PdfObject value
    )
  {parent = value;}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}