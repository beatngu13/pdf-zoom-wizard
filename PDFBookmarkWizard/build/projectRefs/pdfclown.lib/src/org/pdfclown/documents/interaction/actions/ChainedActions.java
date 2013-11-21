/*
  Copyright 2008-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.NotImplementedException;

/**
  Chained actions [PDF:1.6:8.5.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.1, 04/10/11
*/
@PDF(VersionEnum.PDF12)
public final class ChainedActions
  extends PdfObjectWrapper<PdfDataObject>
  implements List<Action>
{
  /*
    NOTE: Chained actions may be either singular or multiple (within an array).
    This implementation hides such a complexity to the user, smoothly exposing
    just the most general case (array) yet preserving its internal state.
  */
  // <class>
  // <dynamic>
  // <fields>
  /**
    Parent action.
  */
  private final Action parent;
  // </fields>

  // <constructors>
  ChainedActions(
    PdfDirectObject baseObject,
    Action parent
    )
  {
    super(baseObject);

    this.parent = parent;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ChainedActions clone(
    Document context
    )
  {throw new NotImplementedException();} // TODO:verify

  /**
    Gets the parent action.
  */
  public Action getParent(
    )
  {return parent;}

  // <List>
  @Override
  public void add(
    int index,
    Action value
    )
  {ensureArray().add(index,value.getBaseObject());}

  @Override
  public boolean addAll(
    int index,
    Collection<? extends Action> values
    )
  {
    for(Action value : values)
    {add(index++,value);}

    return true;
  }

  @Override
  public Action get(
    int index
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
    {
      if(index != 0)
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");

      return Action.wrap(getBaseObject());
    }
    else // Multiple actions.
      return Action.wrap(((PdfArray)baseDataObject).get(index));
  }

  @Override
  public int indexOf(
    Object value
    )
  {
    if(!(value instanceof Action))
      return -1;

    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
      return (((Action)value).getBaseObject().equals(getBaseObject()) ? 0 : -1);
    else // Multiple actions.
      return ((PdfArray)baseDataObject).indexOf(((Action)value).getBaseObject());
  }

  @Override
  public int lastIndexOf(
    Object value
    )
  {return indexOf(value); /* NOTE: Actions are expected not to be duplicated. */}

  @Override
  public ListIterator<Action> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<Action> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public Action remove(
    int index
    )
  {return Action.wrap(ensureArray().remove(index));
  }

  @Override
  public Action set(
    int index,
    Action value
    )
  {return Action.wrap(ensureArray().set(index,value.getBaseObject()));}

  @Override
  public List<Action> subList(
    int fromIndex,
    int toIndex
    )
  {throw new NotImplementedException();}

  // <Collection>
  @Override
  public boolean add(
    Action value
    )
  {return ensureArray().add(value.getBaseObject());}

  @Override
  public boolean addAll(
    Collection<? extends Action> values
    )
  {
    for(Action value : values)
    {add(value);}

    return true;
  }

  @Override
  public void clear(
    )
  {ensureArray().clear();}

  @Override
  public boolean contains(
    Object value
    )
  {
    if(!(value instanceof Action))
      return false;

    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
      return ((Action)value).getBaseObject().equals(getBaseObject());
    else // Multiple actions.
      return ((PdfArray)baseDataObject).contains(((Action)value).getBaseObject());
  }

  @Override
  public boolean containsAll(
    Collection<?> values
    )
  {throw new NotImplementedException();}

  @Override
  public boolean equals(
    Object object
    )
  {throw new NotImplementedException();}

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
      return false;
    else // Multiple actions.
      return ((PdfArray)baseDataObject).isEmpty();
  }

  @Override
  public boolean remove(
    Object value
    )
  {
    if(!(value instanceof Action))
      return false;

    return ensureArray().remove(((Action)value).getBaseObject());
  }

  @Override
  public boolean removeAll(
    Collection<?> values
    )
  {throw new NotImplementedException();}

  @Override
  public boolean retainAll(
    Collection<?> values
    )
  {throw new NotImplementedException();}

  @Override
  public int size(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
      return 1;
    else // Multiple actions.
      return ((PdfArray)baseDataObject).size();
  }

  @Override
  public Object[] toArray(
    )
  {return toArray(new Action[0]);}

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(
    T[] values
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
    {
      if(values.length == 0)
      {values = (T[])new Object[1];}

      values[0] = (T)Action.wrap(getBaseObject());
    }
    else // Multiple actions.
    {
      PdfArray actionObjects = (PdfArray)baseDataObject;
      if(values.length < actionObjects.size())
      {values = (T[])new Object[actionObjects.size()];}

      for(
        int index = 0,
          length = actionObjects.size();
        index < length;
        index++
        )
      {values[index] = (T)Action.wrap(actionObjects.get(index));}
    }
    return values;
  }

  // <Iterable>
  @Override
  public Iterator<Action> iterator(
    )
  {
    return new Iterator<Action>()
    {
      /** Index of the next item. */
      private int index = 0;
      /** Collection size. */
      private final int size = size();

      @Override
      public boolean hasNext(
        )
      {return (index < size);}

      @Override
      public Action next(
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
  // </Iterable>
  // </Collection>
  // </List>
  // </public>

  // <private>
  private PdfArray ensureArray(
    )
  {
    PdfDataObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfDictionary) // Single action.
    {
      PdfArray actionsArray = new PdfArray();
      actionsArray.add(getBaseObject());
      setBaseObject(actionsArray);
      parent.getBaseDataObject().put(PdfName.Next,actionsArray);

      baseDataObject = actionsArray;
    }
    return (PdfArray)baseDataObject;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}