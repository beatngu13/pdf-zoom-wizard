/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

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
  Optional content membership [PDF:1.7:4.10.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class LayerMembership
  extends LayerEntity
{
  // <class>
  // <classes>
  /**
    Layers whose states determine the visibility of content controlled by a membership.
  */
  private static class VisibilityLayers
    extends PdfObjectWrapper<PdfDirectObject>
    implements List<Layer>
  {
    // <fields>
    private final LayerMembership membership;
    // </fields>

    // <constructors>
    private VisibilityLayers(
      LayerMembership membership
      )
    {
      super(membership.getBaseDataObject().get(PdfName.OCGs));
      this.membership = membership;
    }
    // </constructors>

    // <interface>
    // <public>
    @Override
    public VisibilityLayers clone(
      Document context
      )
    {return (VisibilityLayers)super.clone(context);}

    // <List>
    @Override
    public boolean add(
      Layer item
      )
    {return ensureArray().add(item.getBaseObject());}

    @Override
    public void add(
      int index,
      Layer item
      )
    {ensureArray().add(index, item.getBaseObject());}

    @Override
    public boolean addAll(
      Collection<? extends Layer> items
      )
    {
      for(Layer item : items)
      {add(item);}
      return !items.isEmpty();
    }

    @Override
    public boolean addAll(
      int index,
      Collection<? extends Layer> items
      )
    {
      for(Layer item : items)
      {add(index++, item);}
      return !items.isEmpty();
    }

    @Override
    public void clear(
      )
    {ensureArray().clear();}

    @Override
    public boolean contains(
      Object item
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return false;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
        return ((Layer)item).getBaseObject().equals(getBaseObject());
      else // Multiple layers.
        return ((PdfArray)baseDataObject).contains(
          ((Layer)item).getBaseObject()
          );
    }

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
    public Layer get(
      int index
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return null;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
      {
        if(index != 0)
          throw new IndexOutOfBoundsException();

        return Layer.wrap(getBaseObject());
      }
      else // Multiple layers.
        return Layer.wrap(((PdfArray)baseDataObject).get(index));
    }

    @Override
    public int indexOf(
      Object item
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return -1;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
        return ((Layer)item).getBaseObject().equals(getBaseObject()) ? 0 : -1;
      else // Multiple layers.
        return ((PdfArray)baseDataObject).indexOf(((Layer)item).getBaseObject());
    }

    @Override
    public boolean isEmpty(
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return true;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
        return false;
      else // Multiple layers.
        return ((PdfArray)baseDataObject).isEmpty();
    }

    @Override
    public Iterator<Layer> iterator(
      )
    {
      return new Iterator<Layer>()
      {
        private int index = 0;
        private final int size = size();

        @Override
        public boolean hasNext(
          )
        {return (index < size);}

        @Override
        public Layer next(
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
    public int lastIndexOf(
      Object item
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return -1;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
        return ((Layer)item).getBaseObject().equals(getBaseObject()) ? 0 : -1;
      else // Multiple layers.
        return ((PdfArray)baseDataObject).lastIndexOf(((Layer)item).getBaseObject());
    }

    @Override
    public ListIterator<Layer> listIterator(
      )
    {throw new NotImplementedException();}

    @Override
    public ListIterator<Layer> listIterator(
      int index
      )
    {throw new NotImplementedException();}

    @Override
    public boolean remove(
      Object item
      )
    {return ensureArray().remove(((Layer)item).getBaseObject());}

    @Override
    public Layer remove(
      int index
      )
    {return Layer.wrap(ensureArray().remove(index));}

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
    public Layer set(
      int index,
      Layer item
      )
    {return Layer.wrap(ensureArray().set(index, item.getBaseObject()));}

    @Override
    public int size(
      )
    {
      PdfDataObject baseDataObject = getBaseDataObject();
      if(baseDataObject == null) // No layer.
        return 0;
      else if(baseDataObject instanceof PdfDictionary) // Single layer.
        return 1;
      else // Multiple layers.
        return ((PdfArray)baseDataObject).size();
    }

    @Override
    public List<Layer> subList(
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
      T[] a
      )
    {throw new NotImplementedException();}
    // </List>
    // </public>

    // <private>
    private PdfArray ensureArray(
      )
    {
      PdfDirectObject baseDataObject = getBaseDataObject();
      if(!(baseDataObject instanceof PdfArray))
      {
        PdfArray array = new PdfArray();
        if(baseDataObject != null)
        {array.add(baseDataObject);}
        setBaseObject(baseDataObject = array);
        membership.getBaseDataObject().put(PdfName.OCGs, getBaseObject());
      }
      return (PdfArray)baseDataObject;
    }
    // </private>
    // </interface>
  }
  // </classes>

  // <static>
  // <fields>
  public static final PdfName TypeName = PdfName.OCMD;
  // </fields>

  // <interface>
  // <public>
  public static LayerMembership wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new LayerMembership(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public LayerMembership(
    Document context
    )
  {super(context, TypeName);}

  private LayerMembership(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LayerMembership clone(
    Document context
    )
  {return (LayerMembership)super.clone(context);}

  @Override
  public LayerMembership getMembership(
    )
  {return this;}

  @Override
  public List<Layer> getVisibilityLayers(
    )
  {return new VisibilityLayers(this);}

  @Override
  public VisibilityPolicyEnum getVisibilityPolicy(
    )
  {return VisibilityPolicyEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.P));}

  @Override
  public void setVisibilityPolicy(
    VisibilityPolicyEnum value
    )
  {getBaseDataObject().put(PdfName.P, value.getName());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
