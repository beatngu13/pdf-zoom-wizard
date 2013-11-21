/*
  Copyright 2011-2013 Stefano Chizzolini. http://www.pdfclown.org

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pdfclown.documents.Document;
import org.pdfclown.util.NotImplementedException;

/**
  Collection of sequentially-arranged object wrappers.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 01/04/13
 */
public class Array<TItem extends IPdfObjectWrapper>
  extends PdfObjectWrapper<PdfArray>
  implements List<TItem>
{
  // <class>
  // <classes>
  /**
    Item instancer.
  */
  public interface IWrapper<TItem>
  {
    TItem wrap(
      PdfDirectObject baseObject
      );
  }

  private static class DefaultWrapper<TItem>
    implements IWrapper<TItem>
  {
    private Method itemConstructor;

    DefaultWrapper(
      Class<TItem> itemClass
      )
    {
      try
      {itemConstructor = itemClass.getMethod("wrap", PdfDirectObject.class);}
      catch(SecurityException e)
      {throw e;}
      catch(NoSuchMethodException e)
      {throw new RuntimeException(e);}
    }

    @SuppressWarnings("unchecked")
    @Override
    public TItem wrap(
      PdfDirectObject baseObject
      )
    {
      try
      {return (TItem)itemConstructor.invoke(null, baseObject);}
      catch(Exception e)
      {throw new RuntimeException(e);}
    }
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  /**
    Wraps an existing base array using the default wrapper for wrapping its items.

    @param itemClass Item class.
    @param baseObject Base array. MUST be a {@link PdfReference reference} every time available.
  */
  public static <TItem extends IPdfObjectWrapper> Array<TItem> wrap(
    Class<TItem> itemClass,
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Array<TItem>(itemClass, baseObject) : null;}

  /**
    Wraps an existing base array using the specified wrapper for wrapping its items.

    @param itemWrapper Item wrapper.
    @param baseObject Base array. MUST be a {@link PdfReference reference} every time available.
  */
  public static <TItem extends IPdfObjectWrapper> Array<TItem> wrap(
    IWrapper<TItem> itemWrapper,
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Array<TItem>(itemWrapper, baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private final IWrapper<TItem> itemWrapper;
  // </fields>

  // <constructors>
  /**
    Wraps a new base array using the default wrapper for wrapping its items.

    @param context Document context.
    @param itemClass Item class.
  */
  public Array(
    Document context,
    Class<TItem> itemClass
    )
  {
    this(
      context,
      itemClass,
      new PdfArray()
      );
  }

  /**
    Wraps a new base array using the specified wrapper for wrapping its items.

    @param context Document context.
    @param itemWrapper Item wrapper.
  */
  public Array(
    Document context,
    IWrapper<TItem> itemWrapper
    )
  {
    this(
      context,
      itemWrapper,
      new PdfArray()
      );
  }

  /**
    Wraps the specified base array using the default wrapper for wrapping its items.

    @param context Document context.
    @param itemClass Item class.
    @param baseDataObject Base array.
  */
  public Array(
    Document context,
    Class<TItem> itemClass,
    PdfArray baseDataObject
    )
  {
    this(
      context,
      new DefaultWrapper<TItem>(itemClass),
      baseDataObject
      );
  }

  /**
    Wraps the specified base array using the specified wrapper for wrapping its items.

    @param context Document context.
    @param itemWrapper Item wrapper.
    @param baseDataObject Base array.
  */
  public Array(
    Document context,
    IWrapper<TItem> itemWrapper,
    PdfArray baseDataObject
    )
  {
    super(context, baseDataObject);
    this.itemWrapper = itemWrapper;
  }

  /**
    Wraps an existing base array using the default wrapper for wrapping its items.

    @param itemClass Item class.
    @param baseObject Base array. MUST be a {@link PdfReference reference} every time available.
  */
  protected Array(
    Class<TItem> itemClass,
    PdfDirectObject baseObject
    )
  {
    this(
      new DefaultWrapper<TItem>(itemClass),
      baseObject
      );
  }

  /**
    Wraps an existing base array using the specified wrapper for wrapping its items.

    @param itemWrapper Item wrapper.
    @param baseObject Base array. MUST be a {@link PdfReference reference} every time available.
  */
  protected Array(
    IWrapper<TItem> itemWrapper,
    PdfDirectObject baseObject
    )
  {
    super(baseObject);
    this.itemWrapper = itemWrapper;
  }
  // </constructors>

  // <interface>
  // <public>
  @SuppressWarnings({"unchecked"})
  @Override
  public Array<TItem> clone(
    Document context
    )
  {return (Array<TItem>)super.clone(context);}

  // <List>
  @Override
  public boolean add(
    TItem item
    )
  {return getBaseDataObject().add(item.getBaseObject());}

  @Override
  public void add(
    int index,
    TItem item
    )
  {getBaseDataObject().add(index, item.getBaseObject());}

  @Override
  public boolean addAll(
    Collection<? extends TItem> items
    )
  {
    for(TItem item : items)
    {add(item);}
    return !items.isEmpty();
  }

  @Override
  public boolean addAll(
    int index,
    Collection<? extends TItem> items
    )
  {
    for(TItem item : items)
    {add(index, item);}
    return !items.isEmpty();
  }

  @Override
  public void clear(
    )
  {
    int index = size();
    while(index-- > 0)
    {remove(index);}
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean contains(
    Object item
    )
  {return getBaseDataObject().contains(((TItem)item).getBaseObject());}

  @Override
  public boolean containsAll(
    Collection<?> items
    )
  {
    for(Object item : items)
    {
      if(!contains(item))
        return false;
    }
    return true;
  }

  @Override
  public TItem get(
    int index
    )
  {return itemWrapper.wrap(getBaseDataObject().get(index));}

  @Override
  @SuppressWarnings("unchecked")
  public int indexOf(
    Object item
    )
  {return getBaseDataObject().indexOf(((TItem)item).getBaseObject());}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public Iterator<TItem> iterator(
    )
  {
    return new Iterator<TItem>()
    {
      /**
        Index of the next item.
      */
      private int index = 0;
      /**
        Collection size.
      */
      private final int size = size();

      @Override
      public boolean hasNext(
        )
      {return index < size;}

      @Override
      public TItem next(
        )
      {
        if(!hasNext())
          throw new NoSuchElementException();

        return get(index++);
      }

      @Override
      public void remove(
        )
      {throw new UnsupportedOperationException();}
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public int lastIndexOf(
    Object item
    )
  {return getBaseDataObject().lastIndexOf(((TItem)item).getBaseObject());}

  @Override
  public ListIterator<TItem> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<TItem> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  @SuppressWarnings("unchecked")
  public boolean remove(
    Object item
    )
  {return getBaseDataObject().remove(((TItem)item).getBaseObject());}

  @Override
  public TItem remove(
    int index
    )
  {return itemWrapper.wrap(getBaseDataObject().remove(index));}

  @Override
  public boolean removeAll(
    Collection<?> items
    )
  {
    boolean changed = false;
    for(Object item : items)
    {changed |= remove(item);}
    return changed;
  }

  @Override
  public boolean retainAll(
    Collection<?> items
    )
  {throw new NotImplementedException();}

  @Override
  public TItem set(
    int index,
    TItem item
    )
  {return itemWrapper.wrap(getBaseDataObject().set(index, item.getBaseObject()));}

  @Override
  public int size(
    )
  {return getBaseDataObject().size();}

  @Override
  public List<TItem> subList(
    int fromIndex,
    int toIndex
    )
  {throw new NotImplementedException();}

  @Override
  public Object[] toArray(
    )
  {throw new NotImplementedException();}

  @Override
  public <T> T[] toArray(
    T[] array
    )
  {
    List<TItem> items = new ArrayList<TItem>();
    for(PdfDirectObject object : getBaseDataObject())
    {items.add(itemWrapper.wrap(object));}
    return items.toArray(array);
  }
  // </List>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
