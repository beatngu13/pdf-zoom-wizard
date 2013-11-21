/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.util.MapEntry;
import org.pdfclown.util.NotImplementedException;

/**
  Abstract tree [PDF:1.6:3.8.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public abstract class Tree<
    TKey extends PdfSimpleObject<?>,
    TValue extends PdfObjectWrapper<? extends PdfDataObject>
    >
  extends PdfObjectWrapper<PdfDictionary>
  implements Map<TKey,TValue>
{
  /*
    NOTE: This implementation is an adaptation of the B-tree algorithm described in "Introduction to
    Algorithms" [1], 2nd ed (Cormen, Leiserson, Rivest, Stein) published by MIT Press/McGraw-Hill.
    PDF trees represent a special subset of B-trees whereas actual keys are concentrated in leaf
    nodes and proxied by boundary limits across their paths. This simplifies some handling but
    requires keeping node limits updated whenever a change occurs in the leaf nodes composition.

    [1] http://en.wikipedia.org/wiki/Introduction_to_Algorithms
  */
  // <class>
  // <types>
  /**
    Node children.
  */
  private static final class Children
  {
    private static final class Info
    {
      private static final Info KidsInfo = new Info(1, TreeLowOrder);
      private static final Info PairsInfo = new Info(2, TreeLowOrder); // NOTE: Paired children are combinations of 2 contiguous items.

      private static Info get(
        PdfName typeName
        )
      {return typeName.equals(PdfName.Kids) ? KidsInfo : PairsInfo;}

      /** Number of (contiguous) children defining an item. */
      int itemSize;
      /** Maximum number of children. */
      int maxSize;
      /** Minimum number of children. */
      int minSize;

      public Info(
        int itemSize,
        int lowOrder
        )
      {
        this.itemSize = itemSize;
        this.minSize = itemSize * lowOrder;
        this.maxSize = minSize * 2;
      }
    }

    /**
      Gets the given node's children.

      @param node Parent node.
      @param pairs Pairs key.
    */
    public static Children get(
      PdfDictionary node,
      PdfName pairsKey
      )
    {
      PdfName childrenTypeName;
      if(node.containsKey(PdfName.Kids))
      {childrenTypeName = PdfName.Kids;}
      else if(node.containsKey(pairsKey))
      {childrenTypeName = pairsKey;}
      else
        throw new RuntimeException("Malformed tree node.");

      PdfArray children = (PdfArray)node.resolve(childrenTypeName);
      return new Children(node, children, childrenTypeName);
    }

    /** Children's collection */
    public final PdfArray items;
    /** Node's children info. */
    public final Info info;
    /** Parent node. */
    public final PdfDictionary parent;
    /** Node's children type. */
    public final PdfName typeName;

    private Children(
      PdfDictionary parent,
      PdfArray items,
      PdfName typeName
      )
    {
      this.parent = parent;
      this.items = items;
      this.typeName = typeName;
      this.info = Info.get(typeName);
    }

    /**
      Gets whether the collection size has reached its maximum.
    */
    public boolean isFull(
      )
    {return items.size() >= info.maxSize;}

    /**
      Gets whether this collection represents a leaf node.
    */
    public boolean isLeaf(
      )
    {return !typeName.equals(PdfName.Kids);}

    /**
      Gets whether the collection size is more than its maximum.
    */
    public boolean isOversized(
      )
    {return items.size() > info.maxSize;}

    /**
      Gets whether the collection size is less than its minimum.
    */
    public boolean isUndersized(
      )
    {return items.size() < info.minSize;}

    /**
      Gets whether the collection size is within the order limits.
    */
    @SuppressWarnings("unused")
    public boolean isValid(
      )
    {return !(isUndersized() || isOversized());}
  }

  /**
    Key-value pairs collection filler.
  */
  private interface IFiller<TCollection extends Collection<?>>
  {
    void add(
      PdfArray pairs,
      int offset
      );
    TCollection getCollection(
      );
  }
  // </types>

  // <static>
  // <fields>
  /**
    Minimum number of items in non-root nodes.
    Note that the tree (high) order is assumed twice as much (see {@link Children.Info#Info(int, int)}).
  */
  private static final int TreeLowOrder = 5;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private PdfName pairsKey;
  // </fields>

  // <constructors>
  protected Tree(
    Document context
    )
  {
    super(context, new PdfDictionary());
    initialize();
  }

  protected Tree(
    PdfDirectObject baseObject
    )
  {
    super(baseObject);
    initialize();
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the key associated to the specified value.
  */
  public TKey getKey(
    TValue value
    )
  {
    /*
      NOTE: Current implementation doesn't support bidirectional maps, to say that the only
      currently-available way to retrieve a key from a value is to iterate the whole map (really
      poor performance!).
    */
    for(Map.Entry<TKey,TValue> entry : entrySet())
    {
      if(entry.getValue().equals(value))
        return entry.getKey();
    }
    return null;
  }

  // <Map>
  @Override
  public void clear(
    )
  {clear(getBaseDataObject());}

  @Override
  public boolean containsKey(
    Object key
    )
  {
    /*
      NOTE: Here we assume that any named entry has a non-null value.
    */
    return get(key) != null;
  }

  @Override
  public boolean containsValue(
    Object value
    )
  {throw new NotImplementedException();}

  @Override
  public Set<Map.Entry<TKey,TValue>> entrySet(
    )
  {
    IFiller<Set<Map.Entry<TKey,TValue>>> filler = new IFiller<Set<Map.Entry<TKey,TValue>>>()
      {
        private final Set<Map.Entry<TKey,TValue>> entrySet = new TreeSet<Map.Entry<TKey,TValue>>();

        @Override
        @SuppressWarnings("unchecked")
        public void add(
          PdfArray pairs,
          int offset
          )
        {
          TKey key = (TKey)pairs.get(offset);
          TValue value = wrapValue(pairs.get(offset + 1));
          entrySet.add(new MapEntry<TKey,TValue>(key, value));
        }

        @Override
        public Set<Map.Entry<TKey,TValue>> getCollection(
          )
        {return entrySet;}
      };
    fill(filler, getBaseDataObject());

    return filler.getCollection();
  }

  @Override
  public boolean equals(
    Object object
    )
  {throw new NotImplementedException();}

  @Override
  @SuppressWarnings("unchecked")
  public TValue get(
    Object key
    )
  {
    TKey keyObject = (TKey)key;
    PdfDictionary parent = getBaseDataObject();
    while(true)
    {
      Children children = Children.get(parent, pairsKey);
      if(children.isLeaf()) // Leaf node.
      {
        int low = 0, high = children.items.size() - children.info.itemSize;
        while(true)
        {
          if(low > high)
            return null;

          int mid = (mid = ((low + high) / 2)) - (mid % 2);
          int comparison = keyObject.compareTo(children.items.get(mid));
          if(comparison < 0)
          {high = mid - 2;}
          else if(comparison > 0)
          {low = mid + 2;}
          else
          {
            // We got it!
            return wrapValue(children.items.get(mid + 1));
          }
        }
      }
      else // Intermediate node.
      {
        int low = 0, high = children.items.size() - children.info.itemSize;
        while(true)
        {
          if(low > high)
            return null;

          int mid = (low + high) / 2;
          PdfDictionary kid = (PdfDictionary)children.items.resolve(mid);
          PdfArray limits = (PdfArray)kid.resolve(PdfName.Limits);
          if(keyObject.compareTo(limits.get(0)) < 0)
          {high = mid - 1;}
          else if(keyObject.compareTo(limits.get(1)) > 0)
          {low = mid + 1;}
          else
          {
            // Go down one level!
            parent = kid;
            break;
          }
        }
      }
    }
  }

  @Override
  public int hashCode(
    )
  {throw new NotImplementedException();}

  @Override
  public boolean isEmpty(
    )
  {
    PdfDictionary rootNode = getBaseDataObject();
    PdfArray children = (PdfArray)rootNode.resolve(pairsKey);
    if(children == null) // Intermediate node.
    {children = (PdfArray)rootNode.resolve(PdfName.Kids);}
    return children == null || children.isEmpty();
  }

  @Override
  public Set<TKey> keySet(
    )
  {
    IFiller<Set<TKey>> filler = new IFiller<Set<TKey>>()
      {
        private final Set<TKey> keySet = new TreeSet<TKey>();

        @Override
        @SuppressWarnings("unchecked")
        public void add(
          PdfArray pairs,
          int offset
          )
        {keySet.add((TKey)pairs.get(offset));}

        @Override
        public Set<TKey> getCollection(
          )
        {return keySet;}
      };
    fill(filler, getBaseDataObject());

    return filler.getCollection();
  }

  @Override
  public TValue put(
    TKey key,
    TValue value
    )
  {
    // Get the root node!
    PdfDictionary root = getBaseDataObject();

    // Ensuring the root node isn't full...
    {
      Children rootChildren = Children.get(root, pairsKey);
      if(rootChildren.isFull())
      {
        // Transfer the root contents into the new leaf!
        PdfDictionary leaf = new PdfDictionary().swap(root);
        PdfArray rootChildrenObject = new PdfArray(new PdfDirectObject[]{getFile().register(leaf)});
        root.put(PdfName.Kids, rootChildrenObject);
        // Split the leaf!
        splitFullNode(
          rootChildrenObject,
          0, // Old root's position within new root's kids.
          rootChildren.typeName
          );
      }
    }

    // Set the entry under the root node!
    return put(key, value, root);
  }

  @Override
  public void putAll(
    Map<? extends TKey,? extends TValue> entries
    )
  {
    for(Map.Entry<? extends TKey,? extends TValue> entry : entries.entrySet())
    {put(entry.getKey(), entry.getValue());}
  }

  @Override
  @SuppressWarnings("unchecked")
  public TValue remove(
    Object key
    )
  {
    TKey keyObject = (TKey)key;
    PdfDictionary node = getBaseDataObject();
    Stack<PdfReference> nodeReferenceStack = new Stack<PdfReference>();
    while(true)
    {
      Children nodeChildren = Children.get(node, pairsKey);
      if(nodeChildren.isLeaf()) // Leaf node.
      {
        int low = 0, high = nodeChildren.items.size() - nodeChildren.info.itemSize;
        while(true)
        {
          if(low > high) // No match.
            return null;

          int mid = (mid = ((low + high) / 2)) - (mid % 2);
          int comparison = keyObject.compareTo(nodeChildren.items.get(mid));
          if(comparison < 0) // Key before.
          {high = mid - 2;}
          else if(comparison > 0) // Key after.
          {low = mid + 2;}
          else // Key matched.
          {
            // We got it!
            TValue oldValue = wrapValue(nodeChildren.items.remove(mid + 1)); // Removes value.
            nodeChildren.items.remove(mid); // Removes key.
            if(mid == 0 || mid == nodeChildren.items.size()) // Limits changed.
            {
              // Update key limits!
              updateNodeLimits(nodeChildren);

              // Updating key limits on ascendants...
              PdfReference rootReference = (PdfReference)getBaseObject();
              PdfReference nodeReference;
              while(!nodeReferenceStack.isEmpty() && !(nodeReference = nodeReferenceStack.pop()).equals(rootReference))
              {
                PdfArray parentChildren = (PdfArray)nodeReference.getParent();
                int nodeIndex = parentChildren.indexOf(nodeReference);
                if(nodeIndex == 0 || nodeIndex == parentChildren.size() - 1)
                {
                  PdfDictionary parent = (PdfDictionary)parentChildren.getParent();
                  updateNodeLimits(parent, parentChildren, PdfName.Kids);
                }
                else
                  break;
              }
            }
            return oldValue;
          }
        }
      }
      else // Intermediate node.
      {
        int low = 0, high = nodeChildren.items.size() - nodeChildren.info.itemSize;
        while(true)
        {
          if(low > high) // Outside the limit range.
            return null;

          int mid = (low + high) / 2;
          PdfReference kidReference = (PdfReference)nodeChildren.items.get(mid);
          PdfDictionary kid = (PdfDictionary)kidReference.getDataObject();
          PdfArray limits = (PdfArray)kid.resolve(PdfName.Limits);
          if(keyObject.compareTo(limits.get(0)) < 0) // Before the lower limit.
          {high = mid - 1;}
          else if(keyObject.compareTo(limits.get(1)) > 0) // After the upper limit.
          {low = mid + 1;}
          else // Limit range matched.
          {
            Children kidChildren = Children.get(kid, pairsKey);
            if(kidChildren.isUndersized())
            {
              /*
                NOTE: Rebalancing is required as minimum node size invariant is violated.
              */
              PdfDictionary leftSibling = null;
              Children leftSiblingChildren = null;
              if(mid > 0)
              {
                leftSibling = (PdfDictionary)nodeChildren.items.resolve(mid - 1);
                leftSiblingChildren = Children.get(leftSibling, pairsKey);
              }
              PdfDictionary rightSibling = null;
              Children rightSiblingChildren = null;
              if(mid < nodeChildren.items.size() - 1)
              {
                rightSibling = (PdfDictionary)nodeChildren.items.resolve(mid + 1);
                rightSiblingChildren = Children.get(rightSibling, pairsKey);
              }

              if(leftSiblingChildren != null && !leftSiblingChildren.isUndersized())
              {
                // Move the last child subtree of the left sibling to be the first child subtree of the kid!
                for(int index = 0, endIndex = leftSiblingChildren.info.itemSize; index < endIndex; index++)
                {kidChildren.items.add(0, leftSiblingChildren.items.remove(leftSiblingChildren.items.size() - 1));}
                // Update left sibling's key limits!
                updateNodeLimits(leftSiblingChildren);
              }
              else if(rightSiblingChildren != null && !rightSiblingChildren.isUndersized())
              {
                // Move the first child subtree of the right sibling to be the last child subtree of the kid!
                for(int index = 0, endIndex = rightSiblingChildren.info.itemSize; index < endIndex; index++)
                {kidChildren.items.add(rightSiblingChildren.items.remove(0));}
                // Update right sibling's key limits!
                updateNodeLimits(rightSiblingChildren);
              }
              else
              {
                if(leftSibling != null)
                {
                  // Merging with the left sibling...
                  for(int index = leftSiblingChildren.items.size(); index-- > 0;)
                  {kidChildren.items.add(0, leftSiblingChildren.items.remove(index));}
                  nodeChildren.items.remove(mid - 1);
                  leftSibling.getReference().delete();
                }
                else if(rightSibling != null)
                {
                  // Merging with the right sibling...
                  for(int index = rightSiblingChildren.items.size(); index-- > 0;)
                  {kidChildren.items.add(rightSiblingChildren.items.remove(0));}
                  nodeChildren.items.remove(mid + 1);
                  rightSibling.getReference().delete();
                }
                if(nodeChildren.items.size() == 1)
                {
                  // Collapsing root...
                  nodeChildren.items.remove(0);
                  for(int index = kidChildren.items.size(); index-- > 0;)
                  {nodeChildren.items.add(kidChildren.items.remove(0));}
                  kid.getReference().delete();
                  kid = node;
                  kidReference = kid.getReference();
                  kidChildren = nodeChildren;
                }
              }
              // Update key limits!
              updateNodeLimits(kidChildren);
            }
            // Go down one level!
            nodeReferenceStack.push(kidReference);
            node = kid;
            break;
          }
        }
      }
    }
  }

  @Override
  public int size(
    )
  {return size(getBaseDataObject());}

  @Override
  public Collection<TValue> values(
    )
  {
    IFiller<Collection<TValue>> filler = new IFiller<Collection<TValue>>()
      {
        private final Collection<TValue> values = new ArrayList<TValue>();

        @Override
        public void add(
          PdfArray pairs,
          int offset
          )
        {values.add(wrapValue(pairs.get(offset + 1)));}

        @Override
        public Collection<TValue> getCollection(
          )
        {return values;}
      };
    fill(filler, getBaseDataObject());

    return filler.getCollection();
  }
  // </Map>
  // </public>

  // <protected>
  /**
    Gets the name of the key-value pairs entries.
  */
  protected abstract PdfName getPairsKey(
    );

  /**
    Wraps a base object within its corresponding high-level representation.
  */
  protected abstract TValue wrapValue(
    PdfDirectObject baseObject
    );
  // </protected>

  // <private>
  /**
    Removes all the given node's children.
    <p>As this method doesn't apply balancing, it's suitable for clearing root nodes only.</p>
    <p>Removal affects only tree nodes: referenced value objects are preserved to avoid inadvertently
    breaking possible references to them from somewhere else.</p>

    @param node Current node.
  */
  private void clear(
    PdfDictionary node
    )
  {
    Children children = Children.get(node, pairsKey);
    if(!children.isLeaf())
    {
      for(PdfDirectObject child : children.items)
      {
        clear((PdfDictionary)child.resolve());
        getFile().unregister((PdfReference)child);
      }
      node.put(pairsKey, node.remove(children.typeName)); // Recycles the array as the intermediate node transforms to leaf.
    }
    children.items.clear();
    node.remove(PdfName.Limits);
  }

  private <TCollection extends Collection<?>> void fill(
    IFiller<TCollection> filler,
    PdfDictionary node
    )
  {
    PdfArray kidsObject = (PdfArray)node.resolve(PdfName.Kids);
    if(kidsObject == null) // Leaf node.
    {
      PdfArray pairsObject = (PdfArray)node.resolve(pairsKey);
      for(
        int index = 0,
          length = pairsObject.size();
        index < length;
        index += 2
        )
      {filler.add(pairsObject,index);}
    }
    else // Intermediate node.
    {
      for(PdfDirectObject kidObject : kidsObject)
      {fill(filler, (PdfDictionary)kidObject.resolve());}
    }
  }

  private void initialize(
    )
  {
    pairsKey = getPairsKey();

    PdfDictionary baseDataObject = getBaseDataObject();
    if(baseDataObject.isEmpty())
    {
      baseDataObject.setUpdateable(false);
      baseDataObject.put(pairsKey, new PdfArray()); // NOTE: Initial root is by definition a leaf node.
      baseDataObject.setUpdateable(true);
    }
  }

  /**
    Puts an entry under the given tree node.

    @param key New entry's key.
    @param value New entry's value.
    @param nodeReference Current node reference.
  */
  private TValue put(
    TKey key,
    TValue value,
    PdfDictionary node
    )
  {
    TValue oldValue;
    Children children = Children.get(node, pairsKey);
    if(children.isLeaf()) // Leaf node.
    {
      int childrenSize = children.items.size();
      int low = 0, high = childrenSize - children.info.itemSize;
      while(true)
      {
        if(low > high)
        {
          oldValue = null;
          // Insert the entry!
          children.items.add(low, key);
          children.items.add(++low, value.getBaseObject());
          break;
        }

        int mid = (mid = ((low + high) / 2)) - (mid % 2);
        if(mid >= childrenSize)
        {
          oldValue = null;
          // Append the entry!
          children.items.add(key);
          children.items.add(value.getBaseObject());
          break;
        }

        int comparison = key.compareTo(children.items.get(mid));
        if(comparison < 0) // Before.
        {high = mid - 2;}
        else if(comparison > 0) // After.
        {low = mid + 2;}
        else // Matching entry.
        {
          oldValue = wrapValue(children.items.get(mid + 1));
          // Overwrite the entry!
          children.items.set(mid, key);
          children.items.set(++mid, value.getBaseObject());
          break;
        }
      }

      // Update the key limits!
      updateNodeLimits(children);
    }
    else // Intermediate node.
    {
      int low = 0, high = children.items.size() - children.info.itemSize;
      while(true)
      {
        boolean matched = false;
        int mid = (low + high) / 2;
        PdfReference kidReference = (PdfReference)children.items.get(mid);
        PdfDictionary kid = (PdfDictionary)kidReference.getDataObject();
        PdfArray limits = (PdfArray)kid.resolve(PdfName.Limits);
        if(key.compareTo(limits.get(0)) < 0) // Before the lower limit.
        {high = mid - 1;}
        else if(key.compareTo(limits.get(1)) > 0) // After the upper limit.
        {low = mid + 1;}
        else // Limit range matched.
        {matched = true;}

        if(matched // Limit range matched.
          || low > high) // No limit range match.
        {
          Children kidChildren = Children.get(kid, pairsKey);
          if(kidChildren.isFull())
          {
            // Split the node!
            splitFullNode(
              children.items,
              mid,
              kidChildren.typeName
              );
            // Is the key before the split node?
            if(key.compareTo(((PdfArray)kid.resolve(PdfName.Limits)).get(0)) < 0)
            {
              kidReference = (PdfReference)children.items.get(mid);
              kid = (PdfDictionary)kidReference.getDataObject();
            }
          }

          oldValue = put(key, value, kid);
          // Update the key limits!
          updateNodeLimits(children);
          break;
        }
      }
    }
    return oldValue;
  }

  /**
    Gets the given node's entries count.

    @param node Current node.
  */
  private int size(
    PdfDictionary node
    )
  {
    PdfArray children = (PdfArray)node.resolve(pairsKey);
    if(children != null) // Leaf node.
      return (children.size() / 2);
    else // Intermediate node.
    {
      children = (PdfArray)node.resolve(PdfName.Kids);
      int size = 0;
      for(PdfDirectObject child : children)
      {size += size((PdfDictionary)child.resolve());}
      return size;
    }
  }

  /**
    Splits a full node.
    <p>A new node is inserted at the full node's position, receiving the lower half of its children.
    </p>

    @param nodes Parent nodes.
    @param fullNodeIndex Full node's position among the parent nodes.
    @param childrenTypeName Full node's children type.
  */
  private void splitFullNode(
    PdfArray nodes,
    int fullNodeIndex,
    PdfName childrenTypeName
    )
  {
    // Get the full node!
    PdfDictionary fullNode = (PdfDictionary)nodes.resolve(fullNodeIndex);
    PdfArray fullNodeChildren = (PdfArray)fullNode.resolve(childrenTypeName);

    // Create a new (sibling) node!
    PdfDictionary newNode = new PdfDictionary();
    PdfArray newNodeChildren = new PdfArray();
    newNode.put(childrenTypeName, newNodeChildren);
    // Insert the new node just before the full!
    nodes.add(fullNodeIndex,getFile().register(newNode)); // NOTE: Nodes MUST be indirect objects.

    // Transferring exceeding children to the new node...
    for(int index = 0, length = Children.Info.get(childrenTypeName).minSize; index < length; index++)
    {newNodeChildren.add(fullNodeChildren.remove(0));}

    // Update the key limits!
    updateNodeLimits(newNode, newNodeChildren, childrenTypeName);
    updateNodeLimits(fullNode, fullNodeChildren, childrenTypeName);
  }

  /**
    Sets the key limits of the given node.

    @param children Node children.
  */
  private void updateNodeLimits(
    Children children
    )
  {updateNodeLimits(children.parent, children.items, children.typeName);}

  /**
    Sets the key limits of the given node.

    @param node Node to update.
    @param children Node children.
    @param childrenTypeName Node's children type.
  */
  private void updateNodeLimits(
    PdfDictionary node,
    PdfArray children,
    PdfName childrenTypeName
    )
  {
    PdfDirectObject lowLimit, highLimit;
    if(childrenTypeName.equals(PdfName.Kids))
    {
      // Non-leaf root node?
      if(node == getBaseDataObject())
        return; // NOTE: Non-leaf root nodes DO NOT specify limits.

      lowLimit = ((PdfArray)((PdfDictionary)children.resolve(0)).resolve(PdfName.Limits)).get(0);
      highLimit = ((PdfArray)((PdfDictionary)children.resolve(children.size()-1)).resolve(PdfName.Limits)).get(1);
    }
    else if(childrenTypeName.equals(pairsKey))
    {
      lowLimit = children.get(0);
      highLimit = children.get(children.size()-2);
    }
    else // NOTE: Should NEVER happen.
      throw new UnsupportedOperationException(childrenTypeName + " is NOT a supported child type.");

    PdfArray limits = (PdfArray)node.get(PdfName.Limits);
    if(limits != null)
    {
      limits.set(0, lowLimit);
      limits.set(1, highLimit);
    }
    else
    {
      node.put(
        PdfName.Limits,
        new PdfArray(
          new PdfDirectObject[]{
            lowLimit,
            highLimit
            }
          )
        );
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}