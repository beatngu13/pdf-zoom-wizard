/*
  Copyright 2006-2013 Stefano Chizzolini. http://www.pdfclown.org

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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Chunk;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.XRefEntry;
import org.pdfclown.util.NotImplementedException;

/**
  PDF array object, that is a one-dimensional collection of (possibly-heterogeneous) objects
  arranged sequentially [PDF:1.7:3.2.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.0
  @version 0.1.2, 01/09/13
*/
public final class PdfArray
  extends PdfDirectObject
  implements List<PdfDirectObject>
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginArrayChunk = Encoding.Pdf.encode(Keyword.BeginArray);
  private static final byte[] EndArrayChunk = Encoding.Pdf.encode(Keyword.EndArray);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  ArrayList<PdfDirectObject> items;

  private PdfObject parent;
  private boolean updated;
  private boolean updateable = true;
  private boolean virtual;
  // </fields>

  // <constructors>
  public PdfArray(
    )
  {this(10);}

  public PdfArray(
    int capacity
    )
  {items = new ArrayList<PdfDirectObject>(capacity);}

  public PdfArray(
    PdfDirectObject... items
    )
  {
    this(items.length);

    setUpdateable(false);
    for(PdfDirectObject item : items)
    {add(item);}
    setUpdateable(true);
  }

  public PdfArray(
    List<? extends PdfDirectObject> items
    )
  {
    this(items.size());

    setUpdateable(false);
    addAll(items);
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
  public PdfArray clone(
    File context
    )
  {return (PdfArray)super.clone(context);}

  @Override
  public int compareTo(
    PdfDirectObject obj
    )
  {throw new NotImplementedException();}

  /**
    Gets the value corresponding to the given index, forcing its instantiation as a direct object in
    case of missing entry.

    @param index Index of the item to return.
    @param itemClass Class to use for instantiating the item in case of missing entry.
    @since 0.1.2
  */
  public <T extends PdfDataObject> PdfDirectObject get(
    int index,
    Class<T> itemClass
    )
  {return get(index, itemClass, true);}

  /**
    Gets the value corresponding to the given index, forcing its instantiation in case of missing
    entry.

    @param index Index of the item to return.
    @param itemClass Class to use for instantiating the item in case of missing entry.
    @param direct Whether the item has to be instantiated directly within its container instead of
    being referenced through an indirect object.
    @since 0.1.2
  */
  public <T extends PdfDataObject> PdfDirectObject get(
    int index,
    Class<T> itemClass,
    boolean direct
    )
  {
    PdfDirectObject item;
    if(index == size()
      || (item = get(index)) == null
      || !item.resolve().getClass().equals(itemClass))
    {
      /*
        NOTE: The null-object placeholder MUST NOT perturb the existing structure; therefore:
          - it MUST be marked as virtual in order not to unnecessarily serialize it;
          - it MUST be put into this array without affecting its update status.
      */
      try
      {
        item = (PdfDirectObject)include(direct
          ? itemClass.newInstance()
          : new PdfIndirectObject(getFile(), itemClass.newInstance(), new XRefEntry(0, 0)).getReference());
        if(index == size())
        {items.add(item);}
        else if(item == null)
        {items.set(index, item);}
        else
        {items.add(index, item);}
        item.setVirtual(true);
      }
      catch(Exception e)
      {throw new RuntimeException(itemClass.getSimpleName() + " failed to instantiate.", e);}
    }
    return item;
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
    Gets the dereferenced value corresponding to the given index.
    <p>This method takes care to resolve the value returned by {@link #get(int)}.</p>

    @param index Index of the item to return.
    @since 0.0.8
   */
  public PdfDataObject resolve(
    int index
    )
  {return resolve(get(index));}

  /**
    Gets the dereferenced value corresponding to the given index, forcing its instantiation in case
    of missing entry.
    <p>This method takes care to resolve the value returned by {@link #get(int, Class)}.</p>

    @param index Index of the item to return.
    @param itemClass Class to use for instantiating the item in case of missing entry.
    @since 0.1.2
  */
  @SuppressWarnings("unchecked")
  public <T extends PdfDataObject> T resolve(
    int index,
    Class<T> itemClass
    )
  {return (T)resolve(get(index, itemClass));}

  @Override
  public void setUpdateable(
    boolean value
    )
  {updateable = value;}

  @Override
  public PdfArray swap(
    PdfObject other
    )
  {
    PdfArray otherArray = (PdfArray)other;
    ArrayList<PdfDirectObject> otherItems = otherArray.items;
    // Update the other!
    otherArray.items = this.items;
    otherArray.update();
    // Update this one!
    this.items = otherItems;
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
      buffer.append("[ ");
      // Elements.
      for(PdfDirectObject item : items)
      {buffer.append(PdfDirectObject.toString(item)).append(" ");}
      // End.
      buffer.append("]");
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
    stream.write(BeginArrayChunk);
    // Items.
    for(PdfDirectObject item : items)
    {
      if(item != null && item.isVirtual())
        continue;

      PdfDirectObject.writeTo(stream, context, item); stream.write(Chunk.Space);
    }
    // End.
    stream.write(EndArrayChunk);
  }

  // <List>
  @Override
  public void add(
    int index,
    PdfDirectObject item
    )
  {
    items.add(index, (PdfDirectObject)include(item));
    update();
  }

  @Override
  public boolean addAll(
    int index,
    Collection<? extends PdfDirectObject> items
    )
  {
    for(PdfDirectObject item : items)
    {add(index++, item);}
    return true;
  }

  @Override
  public PdfDirectObject get(
    int index
    )
  {return items.get(index);}

  @Override
  public int indexOf(
    Object item
    )
  {return items.indexOf(item);}

  @Override
  public int lastIndexOf(
    Object item
    )
  {return items.lastIndexOf(item);}

  @Override
  public ListIterator<PdfDirectObject> listIterator(
    )
  {return items.listIterator();}

  @Override
  public ListIterator<PdfDirectObject> listIterator(
    int index
    )
  {return items.listIterator(index);}

  @Override
  public PdfDirectObject remove(
    int index
    )
  {
    PdfDirectObject oldItem = items.remove(index);
    exclude(oldItem);
    update();
    return oldItem;
  }

  @Override
  public PdfDirectObject set(
    int index,
    PdfDirectObject item
    )
  {
    PdfDirectObject oldItem = items.set(index, item = (PdfDirectObject)include(item));
    exclude(oldItem);
    update();
    return oldItem;
  }

  @Override
  public List<PdfDirectObject> subList(
    int fromIndex,
    int toIndex
    )
  {return items.subList(fromIndex,toIndex);}

  // <Collection>
  @Override
  public boolean add(
    PdfDirectObject item
    )
  {
    items.add(item = (PdfDirectObject)include(item));
    update();
    return true;
  }

  @Override
  public boolean addAll(
    Collection<? extends PdfDirectObject> items
    )
  {
    for(PdfDirectObject item : items)
    {add(item);}
    return true;
  }

  @Override
  public void clear(
    )
  {
    while(!items.isEmpty())
    {remove(0);}
  }

  @Override
  public boolean contains(
    Object item
    )
  {return items.contains(item);}

  @Override
  public boolean containsAll(
    Collection<?> items
    )
  {return this.items.containsAll(items);}

  @Override
  public boolean equals(
    Object object
    )
  {
    return super.equals(object)
      || (object != null
        && object.getClass().equals(getClass())
        && ((PdfArray)object).items.equals(items));
  }

  @Override
  public int hashCode(
    )
  {return items.hashCode();}

  @Override
  public boolean isEmpty(
    )
  {return items.isEmpty();}

  @Override
  public boolean remove(
    Object item
    )
  {
    if(!items.remove(item))
      return false;

    exclude((PdfDirectObject)item);
    update();
    return true;
  }

  @Override
  public boolean removeAll(
    Collection<?> items
    )
  {
    for(Object item : items)
    {remove(item);}
    return true;
  }

  @Override
  public boolean retainAll(
    Collection<?> items
    )
  {
    int index = 0;
    while(index < this.items.size())
    {
      if(!items.contains(get(index)))
      {remove(index);}
      else
      {index++;}
    }
    return true;
  }

  @Override
  public int size(
    )
  {return items.size();}

  @Override
  public Object[] toArray(
    )
  {return items.toArray();}

  @Override
  public <T> T[] toArray(
    T[] items
    )
  {return this.items.toArray(items);}

  // <Iterable>
  @Override
  public Iterator<PdfDirectObject> iterator(
    )
  {return items.iterator();}
  // </Iterable>
  // </Collection>
  // </List>
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