/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.navigation.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.IPredicate;
import org.pdfclown.util.NotImplementedException;

/**
  Article beads [PDF:1.7:8.3.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF11)
public class ArticleElements
  extends PdfObjectWrapper<PdfDictionary>
  implements List<ArticleElement>
{
  // <class>
  // <classes>
  private static final class ElementCounter
    extends ElementEvaluator
  {
    public int getCount(
      )
    {return index + 1;}
  }

  private static class ElementEvaluator
    implements IPredicate
  {
    /**
      Current position.
    */
    protected int index = -1;

    @Override
    public boolean evaluate(
      Object object
      )
    {
      index++;
      return false;
    }
  }

  private static final class ElementGetter
    extends ElementEvaluator
  {
    private PdfDictionary bead;
    private final int beadIndex;

    public ElementGetter(
      int beadIndex
      )
    {this.beadIndex = beadIndex;}

    @Override
    public boolean evaluate(
      Object object
      )
    {
      super.evaluate(object);
      if(index == beadIndex)
      {
        bead = (PdfDictionary)object;
        return true;
      }
      return false;
    }

    public PdfDictionary getBead(
      )
    {return bead;}
  }

  private static final class ElementIndexer
    extends ElementEvaluator
  {
    private final PdfDictionary searchedBead;

    public ElementIndexer(
      PdfDictionary searchedBead
      )
    {this.searchedBead = searchedBead;}

    @Override
    public boolean evaluate(
      Object object
      )
    {
      super.evaluate(object);
      return object.equals(searchedBead);
    }

    public int getIndex(
      )
    {return index;}
  }

  private static final class ElementListBuilder
    extends ElementEvaluator
  {
    public List<ArticleElement> elements = new ArrayList<ArticleElement>();

    @Override
    public boolean evaluate(
      Object object
      )
    {
      elements.add(ArticleElement.wrap((PdfDirectObject)object));
      return false;
    }

    public List<ArticleElement> getElements(
      )
    {return elements;}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static ArticleElements wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ArticleElements(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  private ArticleElements(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  // <List>
  @Override
  public boolean add(
    final ArticleElement object
    )
  {
    PdfDictionary itemBead = object.getBaseDataObject();
    PdfDictionary firstBead = getFirstBead();
    if(firstBead != null) // Non-empty list.
    {link(itemBead, firstBead);}
    else // Empty list.
    {
      setFirstBead(itemBead);
      link(itemBead, itemBead);
    }
    return true;
  }

  @Override
  public void add(
    int index,
    ArticleElement object
    )
  {
    if(index < 0)
      throw new IndexOutOfBoundsException();

    ElementGetter getter = new ElementGetter(index);
    iterate(getter);
    PdfDictionary bead = getter.getBead();
    if(bead == null)
    {add(object);}
    else
    {link(object.getBaseDataObject(), bead);}
  }

  @Override
  public boolean addAll(
    Collection<? extends ArticleElement> objects
    )
  {throw new NotImplementedException();}

  @Override
  public boolean addAll(
    int index,
    Collection<? extends ArticleElement> objects
    )
  {throw new NotImplementedException();}

  @Override
  public void clear(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean contains(
    Object object
    )
  {return indexOf(object) >= 0;}

  @Override
  public boolean containsAll(
    Collection<?> objects
    )
  {throw new NotImplementedException();}

  @Override
  public ArticleElement get(
    int index
    )
  {
    if(index < 0)
      throw new IndexOutOfBoundsException();

    ElementGetter getter = new ElementGetter(index);
    iterate(getter);
    PdfDictionary bead = getter.getBead();
    if(bead == null)
      throw new IndexOutOfBoundsException();

    return ArticleElement.wrap(bead.getReference());
  }

  @Override
  public int indexOf(
    Object object
    )
  {
    if(object == null)
      return -1; // NOTE: By definition, no bead can be null.

    ElementIndexer indexer = new ElementIndexer(((ArticleElement)object).getBaseDataObject());
    iterate(indexer);
    return indexer.getIndex();
  }

  @Override
  public boolean isEmpty(
    )
  {return getFirstBead() == null;}

  @Override
  public Iterator<ArticleElement> iterator(
    )
  {
    return new Iterator<ArticleElement>()
    {
      private PdfDirectObject currentObject = null;
      private final PdfDirectObject firstObject = getBaseDataObject().get(PdfName.F);
      private PdfDirectObject nextObject = firstObject;

      @Override
      public boolean hasNext(
        )
      {return nextObject != null;}

      @Override
      public ArticleElement next(
        )
      {
        if(!hasNext())
          throw new NoSuchElementException();

        currentObject = nextObject;
        nextObject = ((PdfDictionary)currentObject.resolve()).get(PdfName.N);
        if(nextObject == firstObject) // Looping back.
        {nextObject = null;}

        return ArticleElement.wrap(currentObject);
      }

      @Override
      public void remove(
        )
      {throw new UnsupportedOperationException();}
    };
  }

  @Override
  public int lastIndexOf(
    Object object
    )
  {return indexOf(object);} // NOTE: We assume duplicated bead references don't make sense.

  @Override
  public ListIterator<ArticleElement> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<ArticleElement> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public boolean remove(
    Object object
    )
  {
    if(!contains(object))
      return false;

    unlink(((ArticleElement)object).getBaseDataObject());
    return true;
  }

  @Override
  public ArticleElement remove(
    int index
    )
  {
    ArticleElement item = get(index);
    unlink(item.getBaseDataObject());
    return item;
  }

  @Override
  public boolean removeAll(
    Collection<?> objects
    )
  {
    boolean changed = false;
    for(Object object : objects)
    {
      if(remove(object))
      {changed = true;}
    }
    return changed;
  }

  @Override
  public boolean retainAll(
    Collection<?> objects
    )
  {
    for(ArticleElement item : this)
    {
      if(!objects.contains(item))
      {unlink(item.getBaseDataObject());}
    }
    return false;
  }

  @Override
  public ArticleElement set(
    int index,
    ArticleElement object
    )
  {throw new NotImplementedException();}

  @Override
  public int size(
    )
  {
    ElementCounter counter = new ElementCounter();
    iterate(counter);
    return counter.getCount();
  }

  @Override
  public List<ArticleElement> subList(
    int fromIndex,
    int toIndex
    )
  {throw new NotImplementedException();}

  @Override
  public Object[] toArray(
    )
  {return toArray(new ArticleElement[0]);}

  @Override
  public <T> T[] toArray(
    T[] objects
    )
  {
    ElementListBuilder builder = new ElementListBuilder();
    iterate(builder);
    if(objects.length < builder.getElements().size())
    {objects = (T[])new Object[builder.getElements().size()];}
    builder.getElements().toArray(objects);
    return objects;
  }
  // </List>
  // </public>

  // <private>
  private PdfDictionary getFirstBead(
    )
  {return (PdfDictionary)getBaseDataObject().resolve(PdfName.F);}

  private void iterate(
    IPredicate predicate
    )
  {
    PdfDictionary firstBead = getFirstBead();
    PdfDictionary bead = firstBead;
    while(bead != null)
    {
      if(predicate.evaluate(bead))
        break;

      bead = (PdfDictionary)bead.resolve(PdfName.N);
      if(bead == firstBead)
        break;
    }
  }

  /**
    Links the given item.
  */
  private void link(
    PdfDictionary item,
    PdfDictionary next
    )
  {
    PdfDictionary previous = (PdfDictionary)next.resolve(PdfName.V);
    if(previous == null)
    {previous = next;}

    item.put(PdfName.N, next.getReference());
    next.put(PdfName.V, item.getReference());
    if(previous != item)
    {
      item.put(PdfName.V, previous.getReference());
      previous.put(PdfName.N, item.getReference());
    }
  }

  private void setFirstBead(
    PdfDictionary value
    )
  {
    PdfDictionary oldValue = (PdfDictionary)PdfObject.resolve(getBaseDataObject().put(PdfName.F, PdfObject.unresolve(value)));
    if(value != null)
    {value.put(PdfName.T, getBaseObject());}
    if(oldValue != null)
    {oldValue.remove(PdfName.T);}
  }

  /**
    Unlinks the given item.
    It assumes the item is contained in this list.
  */
  private void unlink(
    PdfDictionary item
    )
  {
    PdfDictionary prevBead = (PdfDictionary)item.remove(PdfName.V).resolve();
    PdfDictionary nextBead = (PdfDictionary)item.remove(PdfName.N).resolve();
    if(prevBead != item) // Still some elements.
    {
      prevBead.put(PdfName.N, nextBead.getReference());
      nextBead.put(PdfName.V, prevBead.getReference());
      if(item == getFirstBead())
      {setFirstBead(nextBead);}
    }
    else // No more elements.
    {setFirstBead(null);}
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
