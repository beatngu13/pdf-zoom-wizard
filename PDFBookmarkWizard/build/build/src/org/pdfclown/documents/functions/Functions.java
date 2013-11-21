/*
  Copyright 2010-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.functions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.NotImplementedException;

/**
  List of 1-input functions combined in a {@link #getParent() stitching function} [PDF:1.6:3.9.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 04/10/11
*/
@PDF(VersionEnum.PDF13)
public final class Functions
  extends PdfObjectWrapper<PdfArray>
  implements List<Function<?>>
{
  // <class>
  // <dynamic>
  // <fields>
  /**
    Parent function.
  */
  private final Type3Function parent;
  // </fields>

  // <constructors>
  Functions(
    PdfDirectObject baseObject,
    Type3Function parent
    )
  {
    super(baseObject);

    this.parent = parent;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Gets the parent stitching function.
  */
  public Type3Function getParent(
    )
  {return parent;}

  // <List>
  @Override
  public void add(
    int index,
    Function<?> value
    )
  {
    validate(value);
    getBaseDataObject().add(index, value.getBaseObject());
  }

  @Override
  public boolean addAll(
    int index,
    Collection<? extends Function<?>> values
    )
  {
    for(Function<?> value : values)
    {add(index++,value);}

    return true;
  }

  @Override
  public Function<?> get(
    int index
    )
  {return Function.wrap(getBaseDataObject().get(index));}

  @Override
  public int indexOf(
    Object value
    )
  {
    if(!(value instanceof Function<?>))
      return -1;

    return getBaseDataObject().indexOf(((Function<?>)value).getBaseObject());
  }

  @Override
  public int lastIndexOf(
    Object value
    )
  {return indexOf(value); /* NOTE: Functions are expected not to be duplicated. */}

  @Override
  public ListIterator<Function<?>> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<Function<?>> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public Function<?> remove(
    int index
    )
  {return Function.wrap(getBaseDataObject().remove(index));}

  @Override
  public Function<?> set(
    int index,
    Function<?> value
    )
  {
    validate(value);
    return Function.wrap(getBaseDataObject().set(index,value.getBaseObject()));
  }

  @Override
  public List<Function<?>> subList(
    int fromIndex,
    int toIndex
    )
  {throw new NotImplementedException();}

  // <Collection>
  @Override
  public boolean add(
    Function<?> value
    )
  {
    validate(value);
    return getBaseDataObject().add(value.getBaseObject());
  }

  @Override
  public boolean addAll(
    Collection<? extends Function<?>> values
    )
  {
    for(Function<?> value : values)
    {add(value);}

    return true;
  }

  @Override
  public void clear(
    )
  {getBaseDataObject().clear();}

  @Override
  public boolean contains(
    Object value
    )
  {
    if(!(value instanceof Function<?>))
      return false;

    return getBaseDataObject().contains(((Function<?>)value).getBaseObject());
  }

  @Override
  public boolean containsAll(
    Collection<?> values
    )
  {throw new NotImplementedException();}

  @Override
  public boolean equals(
    Object value
    )
  {throw new NotImplementedException();}

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty();}

  @Override
  public boolean remove(
    Object value
    )
  {
    if(!(value instanceof Function<?>))
      return false;

    return getBaseDataObject().remove(((Function<?>)value).getBaseObject());
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
  {return getBaseDataObject().size();}

  @Override
  public Object[] toArray(
    )
  {return toArray(new Function<?>[0]);}

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(
    T[] values
    )
  {
    PdfArray functionObjects = getBaseDataObject();
    if(values.length < functionObjects.size())
    {values = (T[])new Object[functionObjects.size()];}

    for(
      int index = 0,
        length = functionObjects.size();
      index < length;
      index++
      )
    {values[index] = (T)Function.wrap(functionObjects.get(index));}

    return values;
  }

  // <Iterable>
  @Override
  public Iterator<Function<?>> iterator(
    )
  {
    return new Iterator<Function<?>>()
      {
        // <class>
        // <dynamic>
        // <fields>
        /**
          Index of the next item.
        */
        private int index = 0;
        /**
          Collection size.
        */
        private final int size = size();
        // </fields>

        // <interface>
        // <public>
        // <Iterator>
        @Override
        public boolean hasNext(
          )
        {return (index < size);}

        @Override
        public Function<?> next(
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
        // </Iterator>
        // </public>
        // </interface>
        // </dynamic>
        // </class>
      };
  }
  // </Iterable>
  // </Collection>
  // </List>
  // </public>

  // <private>
  /**
    Checks whether the specified function is valid for insertion.

    @param value Function to validate.
    @throws IllegalArgumentException
  */
  private void validate(
    Function<?> value
    )
  {
    if(value.getInputCount() != 1)
      throw new IllegalArgumentException("value parameter MUST be 1-input function.");
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
