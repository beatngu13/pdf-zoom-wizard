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

package org.pdfclown.documents.contents.layers;

import java.util.List;
import java.util.ListIterator;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.NotImplementedException;

/**
  Optional content group collection.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 01/04/13
*/
@PDF(VersionEnum.PDF15)
public final class Layers
  extends Array<ILayerNode>
  implements ILayerNode
{
  // <class>
  // <classes>
  private interface INodeEvaluator
  {
    int evaluate(
      int nodeIndex,
      int baseIndex
      );
  }

  private static class ItemWrapper
    implements IWrapper<ILayerNode>
  {
    @Override
    public ILayerNode wrap(
      PdfDirectObject baseObject
      )
    {return LayerNode.wrap(baseObject);}
  }
  // </classes>

  // <static>
  // <fields>
  private static final ItemWrapper Wrapper = new ItemWrapper();
  // </fields>

  // <interface>
  // <public>
  public static Layers wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Layers(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Layers(
    Document context
    )
  {this(context, null);}

  public Layers(
    Document context,
    String title
    )
  {
    super(context, Wrapper);
    setTitle(title);
  }

  private Layers(
    PdfDirectObject baseObject
    )
  {super(Wrapper, baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Layers clone(
    Document context
    )
  {return (Layers)super.clone(context);}

  @Override
  public void add(
    int index,
    ILayerNode item
    )
  {super.add(getBaseIndex(index), item);}

  @Override
  public ILayerNode get(
    int index
    )
  {return super.get(getBaseIndex(index));}

  @Override
  public int indexOf(
    Object item
    )
  {return getNodeIndex(super.indexOf(item));}

  @Override
  public int lastIndexOf(
    Object item
    )
  {return getNodeIndex(super.lastIndexOf(item));}

  @Override
  public ListIterator<ILayerNode> listIterator(
    )
  {throw new NotImplementedException();}

  @Override
  public ListIterator<ILayerNode> listIterator(
    int index
    )
  {throw new NotImplementedException();}

  @Override
  public ILayerNode remove(
    int index
    )
  {
    int baseIndex = getBaseIndex(index);
    ILayerNode removedItem = super.remove(baseIndex);
    if(removedItem instanceof Layer
      && baseIndex < super.size())
    {
      /*
        NOTE: Sublayers MUST be removed as well.
      */
      if(getBaseDataObject().resolve(baseIndex) instanceof PdfArray)
      {getBaseDataObject().remove(baseIndex);}
    }
    return removedItem;
  }

  @Override
  public ILayerNode set(
    int index,
    ILayerNode item
    )
  {return super.set(getBaseIndex(index), item);}

  @Override
  public int size(
    )
  {
    return evaluate(new INodeEvaluator()
    {
      @Override
      public int evaluate(
        int currentNodeIndex,
        int currentBaseIndex
        )
      {
        if(currentBaseIndex == -1)
          return currentNodeIndex;
        else
          return -1;
      }
    }) + 1;
  }

  @Override
  public List<ILayerNode> subList(
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
  {throw new NotImplementedException();}

  @Override
  public String toString(
    )
  {return getTitle();}

  // <ILayerNode>
  @Override
  public Layers getLayers(
    )
  {return this;}

  @Override
  public String getTitle(
    )
  {
    if(getBaseDataObject().isEmpty())
      return null;

    PdfDirectObject firstObject = getBaseDataObject().get(0);
    return firstObject instanceof PdfTextString ? ((PdfTextString)firstObject).getValue() : null;
  }

  @Override
  public void setTitle(
    String value
    )
  {
    PdfTextString titleObject = PdfTextString.get(value);
    PdfArray baseDataObject = getBaseDataObject();
    PdfDirectObject firstObject = (baseDataObject.isEmpty() ? null : baseDataObject.get(0));
    if(firstObject instanceof PdfTextString)
    {
      if(titleObject != null)
      {baseDataObject.set(0, titleObject);}
      else
      {baseDataObject.remove(0);}
    }
    else if(titleObject != null)
    {baseDataObject.add(0, titleObject);}
  }
  // </ILayerNode>
  // </public>

  // <private>
  /**
    Gets the positional information resulting from the collection evaluation.

    @param evaluator Expression used to evaluate the positional matching.
  */
  private int evaluate(
    INodeEvaluator evaluator
    )
  {
    /*
      NOTE: Layer hierarchies are represented through a somewhat flatten structure which needs
      to be evaluated in order to match nodes in their actual place.
    */
    PdfArray baseDataObject = getBaseDataObject();
    int nodeIndex = -1;
    boolean groupAllowed = true;
    for(
      int baseIndex = 0,
        baseLength = super.size();
      baseIndex < baseLength;
      baseIndex++
      )
    {
      PdfDataObject itemDataObject = baseDataObject.resolve(baseIndex);
      if(itemDataObject instanceof PdfDictionary
        || (itemDataObject instanceof PdfArray && groupAllowed))
      {
        nodeIndex++;
        int evaluation = evaluator.evaluate(nodeIndex, baseIndex);
        if(evaluation > -1)
          return evaluation;
      }
      groupAllowed = !(itemDataObject instanceof PdfDictionary);
    }
    return evaluator.evaluate(nodeIndex, -1);
  }

  private int getBaseIndex(
    final int nodeIndex
    )
  {
    return evaluate(new INodeEvaluator()
    {
      @Override
      public int evaluate(
        int currentNodeIndex,
        int currentBaseIndex
        )
      {
        if(currentNodeIndex == nodeIndex)
          return currentBaseIndex;
        else
          return -1;
      }
    });
  }

  private int getNodeIndex(
    final int baseIndex
    )
  {
    return evaluate(new INodeEvaluator()
    {
      @Override
      public int evaluate(
        int currentNodeIndex,
        int currentBaseIndex
        )
      {
        if(currentBaseIndex == baseIndex)
          return currentNodeIndex;
        else
          return -1;
      }
    });
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}