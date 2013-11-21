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

package org.pdfclown.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.util.NotImplementedException;

/**
  Document pages collection [PDF:1.6:3.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public final class Pages
  extends PdfObjectWrapper<PdfDictionary>
  implements List<Page>
{
  /*
    TODO:IMPL A B-tree algorithm should be implemented to optimize the inner layout
    of the page tree (better insertion/deletion performance). In this case, it would
    be necessary to keep track of the modified tree nodes for incremental update.
  */
  // <class>
  // <dynamic>
  // <constructors>
  Pages(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.Kids,
          PdfName.Count
        },
        new PdfDirectObject[]
        {
          PdfName.Pages,
          new PdfArray(),
          PdfInteger.Default
        }
        )
      );
  }

  Pages(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Pages clone(
    Document context
    )
  {return (Pages)super.clone(context);}

  // <List>
  @Override
  public void add(
    int index,
    Page page
    )
  {commonAddAll(index,Arrays.asList(page));}

  @Override
  public boolean addAll(
    int index,
    Collection<? extends Page> pages
    )
  {return commonAddAll(index,pages);}

  @Override
  public Page get(
    int index
    )
  {
    /*
      NOTE: As stated in [PDF:1.6:3.6.2], to retrieve pages is a matter of diving
      inside a B-tree. To keep it as efficient as possible, this implementation
      does NOT adopt recursion to deepen its search, opting for an iterative strategy
      instead.
    */
    int pageOffset = 0;
    PdfDictionary parent = getBaseDataObject();
    PdfArray kids = (PdfArray)parent.resolve(PdfName.Kids);
    for(
      int i = 0;
      i < kids.size();
      i++
      )
    {
      PdfReference kidReference = (PdfReference)kids.get(i);
      PdfDictionary kid = (PdfDictionary)kidReference.getDataObject();
      // Is current kid a page object?
      if(kid.get(PdfName.Type).equals(PdfName.Page)) // Page object.
      {
        // Did we reach the searched position?
        if(pageOffset == index) // Vertical scan (we finished).
        {
          // We got it!
          return Page.wrap(kidReference);
        }
        else // Horizontal scan (go past).
        {
          // Cumulate current page object count!
          pageOffset++;
        }
      }
      else // Page tree node.
      {
        // Does the current subtree contain the searched page?
        if(((PdfInteger)kid.get(PdfName.Count)).getRawValue() + pageOffset > index) // Vertical scan (deepen the search).
        {
          // Go down one level!
          parent = kid;
          kids = (PdfArray)parent.resolve(PdfName.Kids);
          i = -1;
        }
        else // Horizontal scan (go past).
        {
          // Cumulate current subtree count!
          pageOffset += ((PdfInteger)kid.get(PdfName.Count)).getRawValue();
        }
      }
    }
    return null;
  }

  @Override
  public int indexOf(
    Object page
    )
  {return ((Page)page).getIndex();}

  @Override
  public int lastIndexOf(
    Object page
    )
  {
    /*
      NOTE: Each page object should NOT appear more than once inside the same document.
    */
    return indexOf(page);
  }

  @Override
  public ListIterator<Page> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<Page> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public Page remove(
    int index
    )
  {
    Page page = get(index);
    remove(page);

    return page;
  }

  @Override
  public Page set(
    int index,
    Page page
    )
  {
    Page old = remove(index);
    add(index,page);

    return old;
  }

  @Override
  public List<Page> subList(
    int fromIndex,
    int toIndex
    )
  {
  /*
  TODO:IMPL this implementation is incoherent with the subList contract --> move to another location!
  */
    ArrayList<Page> pages = new ArrayList<Page>(toIndex - fromIndex);
    int i = fromIndex;
    while(i < toIndex)
    {pages.add(get(i++));}

    return pages;
  }

  // <Collection>
  @Override
  public boolean add(
    Page page
    )
  {return commonAddAll(-1,Arrays.asList(page));}

  @Override
  public boolean addAll(
    Collection<? extends Page> pages
    )
  {return commonAddAll(-1,pages);}

  @Override
  public void clear(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean contains(
    Object page
    )
  {throw new NotImplementedException();}

  @Override
  public boolean containsAll(
    Collection<?> pages
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
    Object page
    )
  {
    Page pageObj = (Page)page;
    PdfDictionary pageData = pageObj.getBaseDataObject();
    // Get the parent tree node!
    PdfDirectObject parent = pageData.get(PdfName.Parent);
    PdfDictionary parentData = (PdfDictionary)parent.resolve();
    // Get the parent's page collection!
    PdfDirectObject kids = parentData.get(PdfName.Kids);
    PdfArray kidsData = (PdfArray)kids.resolve();
    // Remove the page!
    kidsData.remove(pageObj.getBaseObject());

    // Unbind the page from its parent!
    pageData.put(PdfName.Parent,null);

    // Decrementing the pages counters...
    do
    {
      // Get the page collection counter!
      PdfInteger countObject = (PdfInteger)parentData.get(PdfName.Count);
      // Decrement the counter at the current level!
      parentData.put(PdfName.Count, PdfInteger.get(countObject.getValue()-1));

      // Iterate upward!
      parent = parentData.get(PdfName.Parent);
      parentData = (PdfDictionary)PdfObject.resolve(parent);
    } while(parent != null);

    return true;
  }

  @Override
  public boolean removeAll(
    Collection<?> pages
    )
  {
    /*
      NOTE: The interface contract doesn't prescribe any relation among the removing-collection's
      items, so we cannot adopt the optimized approach of the add*(...) methods family,
      where adding-collection's items are explicitly ordered.
    */
    boolean changed = false;
    for(Object page : pages)
    {changed |= remove(page);}

    return changed;
  }

  @Override
  public boolean retainAll(
    Collection<?> pages
    )
  {throw new NotImplementedException();}

  @Override
  public int size(
    )
  {return ((PdfInteger)getBaseDataObject().get(PdfName.Count)).getRawValue();}

  @Override
  public Page[] toArray(
    )
  {throw new NotImplementedException();}

  @Override
  public <T> T[] toArray(
    T[] pages
    )
  {throw new NotImplementedException();}

  // <Iterable>
  @Override
  public Iterator<Page> iterator(
    )
  {
    return new Iterator<Page>()
    {
      /**
        Index of the next item.
      */
      private int index = 0;
      /**
        Collection size.
      */
      private final int size = size();

      /**
        Current level index.
      */
      private int levelIndex = 0;
      /**
        Stacked level indexes.
      */
      private final Stack<Integer> levelIndexes = new Stack<Integer>();
      /**
        Current parent tree node.
      */
      private PdfDictionary parent = getBaseDataObject();
      /**
        Current child tree nodes.
      */
      private PdfArray kids = (PdfArray)parent.resolve(PdfName.Kids);

      @Override
      public boolean hasNext(
        )
      {return (index < size);}

      @Override
      public Page next(
        )
      {
        if(!hasNext())
          throw new NoSuchElementException();

        /*
          NOTE: As stated in [PDF:1.6:3.6.2], page retrieval is a matter of diving
          inside a B-tree.
          This is a special adaptation of the get() algorithm necessary to keep
          a low overhead throughout the page tree scan (using the get() method
          would have implied a nonlinear computational cost).
        */
        /*
          NOTE: Algorithm:
          1. [Vertical, down] We have to go downward the page tree till we reach
          a page (leaf node).
          2. [Horizontal] Then we iterate across the page collection it belongs to,
          repeating step 1 whenever we find a subtree.
          3. [Vertical, up] When leaf-nodes scan is complete, we go upward solving
          parent nodes, repeating step 2.
        */
        while(true)
        {
          // Did we complete current page-tree-branch level?
          if(kids.size() == levelIndex) // Page subtree complete.
          {
            // 3. Go upward one level.
            // Restore node index at the current level!
            levelIndex = levelIndexes.pop() + 1; // Next node (partially scanned level).
            // Move upward!
            parent = (PdfDictionary)parent.resolve(PdfName.Parent);
            kids = (PdfArray)parent.resolve(PdfName.Kids);
          }
          else // Page subtree incomplete.
          {
            PdfReference kidReference = (PdfReference)kids.get(levelIndex);
            PdfDictionary kid = (PdfDictionary)kidReference.getDataObject();
            // Is current kid a page object?
            if(kid.get(PdfName.Type).equals(PdfName.Page)) // Page object.
            {
              // 2. Page found.
              index++; // Absolute page index.
              levelIndex++; // Current level node index.

              return Page.wrap(kidReference);
            }
            else // Page tree node.
            {
              // 1. Go downward one level.
              // Save node index at the current level!
              levelIndexes.push(levelIndex);
              // Move downward!
              parent = kid;
              kids = (PdfArray)parent.resolve(PdfName.Kids);
              levelIndex = 0; // First node (new level).
            }
          }
        }
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
  /**
    Add a collection of pages at the specified position.
    @param index Addition position. To append, use value -1.
    @param pages Collection of pages to add.
  */
  private boolean commonAddAll(
    int index,
    Collection<? extends Page> pages
    )
  {
    PdfDirectObject parent;
    PdfDictionary parentData;
    PdfDirectObject kids;
    PdfArray kidsData;
    int offset;
    // Append operation?
    if(index == -1) // Append operation.
    {
      // Get the parent tree node!
      parent = getBaseObject();
      parentData = getBaseDataObject();
      // Get the parent's page collection!
      kids = parentData.get(PdfName.Kids);
      kidsData = (PdfArray)PdfObject.resolve(kids);
      offset = 0; // Not used.
    }
    else // Insert operation.
    {
      // Get the page currently at the specified position!
      Page pivotPage = get(index);
      // Get the parent tree node!
      parent = pivotPage.getBaseDataObject().get(PdfName.Parent);
      parentData = (PdfDictionary)parent.resolve();
      // Get the parent's page collection!
      kids = parentData.get(PdfName.Kids);
      kidsData = (PdfArray)kids.resolve();
      // Get the insertion's relative position within the parent's page collection!
      offset = kidsData.indexOf(pivotPage.getBaseObject());
    }

    // Adding the pages...
    for(Page page : pages)
    {
      // Append?
      if(index == -1) // Append.
      {
        // Append the page to the collection!
        kidsData.add(page.getBaseObject());
      }
      else // Insert.
      {
        // Insert the page into the collection!
        kidsData.add(
          offset++,
          page.getBaseObject()
          );
      }
      // Bind the page to the collection!
      page.getBaseDataObject().put(PdfName.Parent,parent);
    }

    // Incrementing the pages counters...
    do
    {
      // Get the page collection counter!
      PdfInteger countObject = (PdfInteger)parentData.get(PdfName.Count);
      // Increment the counter at the current level!
      parentData.put(PdfName.Count, PdfInteger.get(countObject.getValue()+pages.size()));

      // Iterate upward!
      parent = parentData.get(PdfName.Parent);
      parentData = (PdfDictionary)PdfObject.resolve(parent);
    } while(parent != null);

    return true;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}