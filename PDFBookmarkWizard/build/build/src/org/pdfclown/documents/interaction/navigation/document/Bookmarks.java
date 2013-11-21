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

package org.pdfclown.documents.interaction.navigation.document;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.util.NotImplementedException;

/**
  Collection of bookmarks [PDF:1.6:8.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public final class Bookmarks
  extends PdfObjectWrapper<PdfDictionary>
  implements List<Bookmark>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static Bookmarks wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Bookmarks(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Bookmarks(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.Count
        },
        new PdfDirectObject[]
        {
          PdfName.Outlines,
          PdfInteger.Default
        }
        )
      );
  }

  private Bookmarks(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Bookmarks clone(
    Document context
    )
  {return (Bookmarks)super.clone(context);}

  // <List>
  @Override
  public void add(
    int index,
    Bookmark bookmark
    )
  {throw new NotImplementedException();}

  @Override
  public boolean addAll(
    int index,
    Collection<? extends Bookmark> bookmarks
    )
  {throw new NotImplementedException();}

  @Override
  public Bookmark get(
    int index
    )
  {
    PdfReference bookmarkObject = (PdfReference)getBaseDataObject().get(PdfName.First);
    while(index > 0)
    {
      bookmarkObject = (PdfReference)((PdfDictionary)bookmarkObject.getDataObject()).get(PdfName.Next);
      // Did we go past the collection range?
      if(bookmarkObject == null)
        throw new IndexOutOfBoundsException();

      index--;
    }

    return new Bookmark(bookmarkObject);
  }

  @Override
  public int indexOf(
    Object bookmark
    )
  {throw new NotImplementedException();}

  @Override
  public int lastIndexOf(
    Object bookmark
    )
  {return indexOf(bookmark);}

  @Override
  public ListIterator<Bookmark> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<Bookmark> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public Bookmark remove(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public Bookmark set(
    int index,
    Bookmark bookmark
    )
  {throw new NotImplementedException();}

  @Override
  public List<Bookmark> subList(
    int fromIndex,
    int toIndex
    )
  {throw new NotImplementedException();}

  // <Collection>
  @Override
  public boolean add(
    Bookmark bookmark
    )
  {
    /*
      NOTE: Bookmarks imported from alien PDF files MUST be cloned
      before being added.
    */
    bookmark.getBaseDataObject().put(PdfName.Parent,getBaseObject());

    PdfInteger countObject = ensureCountObject();
    // Is it the first bookmark?
    if(countObject.getValue() == 0) // First bookmark.
    {
      getBaseDataObject().put(PdfName.First, bookmark.getBaseObject());
      getBaseDataObject().put(PdfName.Last, bookmark.getBaseObject());
      getBaseDataObject().put(PdfName.Count, PdfInteger.get(countObject.getValue()+1));
    }
    else // Non-first bookmark.
    {
      PdfReference oldLastBookmarkReference = (PdfReference)getBaseDataObject().get(PdfName.Last);
      getBaseDataObject().put(PdfName.Last,bookmark.getBaseObject()); // Added bookmark is the last in the collection...
      ((PdfDictionary)oldLastBookmarkReference.getDataObject()).put(PdfName.Next,bookmark.getBaseObject()); // ...and the next of the previously-last bookmark.
      bookmark.getBaseDataObject().put(PdfName.Prev,oldLastBookmarkReference);
      /*
        NOTE: The Count entry is a relative number (whose sign represents
        the node open state).
      */
      getBaseDataObject().put(PdfName.Count, PdfInteger.get(countObject.getValue() + (int)Math.signum(countObject.getValue())));
    }

    return true;
  }

  @Override
  public boolean addAll(
    Collection<? extends Bookmark> bookmarks
    )
  {throw new NotImplementedException();}

  @Override
  public void clear(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean contains(
    Object bookmark
    )
  {throw new NotImplementedException();}

  @Override
  public boolean containsAll(
    Collection<?> bookmarks
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
  {throw new NotImplementedException();}

  @Override
  public boolean remove(
    Object bookmark
    )
  {throw new NotImplementedException();}

  @Override
  public boolean removeAll(
    Collection<?> bookmarks
    )
  {throw new NotImplementedException();}

  @Override
  public boolean retainAll(
    Collection<?> bookmarks
    )
  {throw new NotImplementedException();}

  @Override
  public int size(
    )
  {
    /*
      NOTE: The Count entry may be absent [PDF:1.6:8.2.2].
    */
    PdfInteger countObject = (PdfInteger)getBaseDataObject().get(PdfName.Count);
    if(countObject == null)
      return 0;

    return countObject.getRawValue();
  }

  @Override
  public Bookmark[] toArray(
    )
  {throw new NotImplementedException();}

  @Override
  public <T> T[] toArray(
    T[] values
    )
  {throw new NotImplementedException();}

  // <Iterable>
  @Override
  public Iterator<Bookmark> iterator(
    )
  {
    return new Iterator<Bookmark>()
    {
      private PdfDirectObject currentBookmarkObject = null;
      private PdfDirectObject nextBookmarkObject = getBaseDataObject().get(PdfName.First);

      @Override
      public boolean hasNext(
        )
      {return (nextBookmarkObject != null);}

      @Override
      public Bookmark next(
        )
      {
        if(!hasNext())
          throw new NoSuchElementException();

        currentBookmarkObject = nextBookmarkObject;
        nextBookmarkObject = ((PdfDictionary)currentBookmarkObject.resolve()).get(PdfName.Next);

        return new Bookmark(currentBookmarkObject);
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

  // <protected>
  /**
    Gets the count object, forcing its creation if it doesn't exist.
  */
  protected PdfInteger ensureCountObject(
    )
  {
    /*
      NOTE: The Count entry may be absent [PDF:1.6:8.2.2].
    */
    PdfInteger countObject = (PdfInteger)getBaseDataObject().get(PdfName.Count);
    if(countObject == null)
    {getBaseDataObject().put(PdfName.Count, countObject = PdfInteger.Default);}

    return countObject;
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}