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

package org.pdfclown.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pdfclown.documents.Page;
import org.pdfclown.documents.interaction.forms.Field;
import org.pdfclown.files.File;
import org.pdfclown.tokens.ObjectStream;
import org.pdfclown.tokens.XRefStream;

/**
  Object cloner.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
public class Cloner
  extends Visitor
{
  // <class>
  // <classes>
  public static class Filter
  {
    private final String name;

    public Filter(
      String name
      )
    {this.name = name;}

    /**
      Notifies a complete clone operation on an object.

      @param cloner Object cloner.
      @param clone Clone object.
      @param source Source object.
    */
    public void afterClone(
      Cloner cloner,
      PdfObject clone,
      PdfObject source
      )
    {/* NOOP */}

    /**
      Notifies a complete clone operation on a dictionary entry.

      @param cloner Object cloner.
      @param parent Parent clone object.
      @param key Entry key within the parent.
      @param value Clone value.
    */
    public void afterClone(
      Cloner cloner,
      PdfDictionary parent,
      PdfName key,
      PdfDirectObject value
      )
    {/* NOOP */}

    /**
      Notifies a complete clone operation on an array item.

      @param cloner Object cloner.
      @param parent Parent clone object.
      @param index Item index within the parent.
      @param item Clone item.
    */
    public void afterClone(
      Cloner cloner,
      PdfArray parent,
      int index,
      PdfDirectObject item
      )
    {/* NOOP */}

    /**
      Notifies a starting clone operation on a dictionary entry.

      @param cloner Object cloner.
      @param parent Parent clone object.
      @param key Entry key within the parent.
      @param value Source value.
      @return Whether the clone operation can be fulfilled.
    */
    public boolean beforeClone(
      Cloner cloner,
      PdfDictionary parent,
      PdfName key,
      PdfDirectObject value
      )
    {return true;}

    /**
      Notifies a starting clone operation on an array item.

      @param cloner Object cloner.
      @param parent Parent clone object.
      @param index Item index within the parent.
      @param item Source item.
      @return Whether the clone operation can be fulfilled.
    */
    public boolean beforeClone(
      Cloner cloner,
      PdfArray parent,
      int index,
      PdfDirectObject item
      )
    {return true;}

    public String getName(
      )
    {return name;}

    /**
      Gets whether this filter can deal with the given object.

      @param cloner Object cloner.
      @param source Source object.
    */
    public boolean matches(
      Cloner cloner,
      PdfObject source
      )
    {return true;}
  }
  // </classes>

  // <static>
  // <fields>
  private static final Filter NullFilter = new Filter("Default");

  private static List<Filter> commonFilters = new ArrayList<Filter>();
  // </fields>

  // <constructors>
  static
  {
    // Page object.
    commonFilters.add(
      new Filter("Page")
      {
        @Override
        public void afterClone(
          Cloner cloner,
          PdfObject clone,
          PdfObject source
          )
        {
          /*
            NOTE: Inheritable attributes have to be consolidated into the cloned page dictionary in
            order to ensure its consistency.
          */
          PdfDictionary cloneDictionary = (PdfDictionary)clone;
          PdfDictionary sourceDictionary = (PdfDictionary)source;
          for(PdfName key : Page.InheritableAttributeKeys)
          {
            if(!sourceDictionary.containsKey(key))
            {
              PdfDirectObject sourceValue = Page.getInheritableAttribute(sourceDictionary, key);
              if(sourceValue != null)
              {cloneDictionary.put(key, (PdfDirectObject)sourceValue.accept(cloner, null));}
            }
          }
        }

        @Override
        public boolean beforeClone(
          Cloner cloner,
          PdfDictionary parent,
          PdfName key,
          PdfDirectObject value
          )
        {return !PdfName.Parent.equals(key);}

        @Override
        public boolean matches(
          Cloner cloner,
          PdfObject object
          )
        {
          return object instanceof PdfDictionary
            && PdfName.Page.equals(((PdfDictionary)object).get(PdfName.Type));
        }
      }
      );
    // Annotations.
    commonFilters.add(
      new Filter("Annots")
      {
        @Override
        public void afterClone(
          Cloner cloner,
          PdfArray parent,
          int index,
          PdfDirectObject item
          )
        {
          PdfDictionary annotation = (PdfDictionary)item.resolve();
          if(annotation.containsKey(PdfName.FT))
          {cloner.context.getDocument().getForm().getFields().add(Field.wrap(annotation.getReference()));}
        }

        @Override
        public boolean matches(
          Cloner cloner,
          PdfObject object
          )
        {
          if(object instanceof PdfArray)
          {
            PdfArray array = (PdfArray)object;
            if(!array.isEmpty())
            {
              PdfDataObject arrayItem = array.resolve(0);
              if(arrayItem instanceof PdfDictionary)
              {
                PdfDictionary arrayItemDictionary = (PdfDictionary)arrayItem;
                return arrayItemDictionary.containsKey(PdfName.Subtype)
                  && arrayItemDictionary.containsKey(PdfName.Rect);
              }
            }
          }
          return false;
        }
      }
      );
  }
  // </constructors>
  // </static>

  // <dynamic>
  // <fields>
  private File context;
  private final List<Filter> filters = new ArrayList<Filter>(commonFilters);
  // </fields>

  // <constructors>
  public Cloner(
    File context
    )
  {setContext(context);}
  // </constructors>

  // <interface>
  // <public>
  public File getContext(
    )
  {return context;}

  public List<Filter> getFilters(
    )
  {return filters;}

  public void setContext(
    File value
    )
  {
    if(value == null)
      throw new IllegalArgumentException("value required");

    context = value;
  }

  @Override
  public PdfObject visit(
    ObjectStream object,
    Object data
    )
  {throw new UnsupportedOperationException();}

  @Override
  public PdfObject visit(
    PdfArray object,
    Object data
    )
  {
    Filter cloneFilter = matchFilter(object);
    PdfArray clone = (PdfArray)object.clone();
    {
      clone.items = new ArrayList<PdfDirectObject>();
      List<PdfDirectObject> sourceItems = object.items;
      for(int index = 0, length = sourceItems.size(); index < length; index++)
      {
        PdfDirectObject sourceItem = sourceItems.get(index);
        if(cloneFilter.beforeClone(this, clone, index, sourceItem))
        {
          PdfDirectObject cloneItem;
          clone.add(cloneItem = (PdfDirectObject)(sourceItem != null ? sourceItem.accept(this, null) : null));
          cloneFilter.afterClone(this, clone, index, cloneItem);
        }
      }
    }
    cloneFilter.afterClone(this, clone, object);
    return clone;
  }

  @Override
  public PdfObject visit(
    PdfDictionary object,
    Object data
    )
  {
    Filter cloneFilter = matchFilter(object);
    PdfDictionary clone = (PdfDictionary)object.clone();
    {
      clone.entries = new HashMap<PdfName,PdfDirectObject>();
      for(Map.Entry<PdfName,PdfDirectObject> entry : object.entries.entrySet())
      {
        PdfDirectObject sourceValue = entry.getValue();
        if(cloneFilter.beforeClone(this, clone, entry.getKey(), sourceValue))
        {
          PdfDirectObject cloneValue;
          clone.put(entry.getKey(), cloneValue = (PdfDirectObject)(sourceValue != null ? sourceValue.accept(this, null) : null));
          cloneFilter.afterClone(this, clone, entry.getKey(), cloneValue);
        }
      }
    }
    cloneFilter.afterClone(this, clone, object);
    return clone;
  }

  @Override
  public PdfObject visit(
    PdfIndirectObject object,
    Object data
    )
  {return context.getIndirectObjects().addExternal(object, this);}

  @Override
  public PdfObject visit(
    PdfReference object,
    Object data
    )
  {
    return context == object.getFile()
      ? (PdfReference)object.clone() // Local clone.
      : visit(object.getIndirectObject(), data).getReference(); // Alien clone.
  }

  @Override
  public PdfObject visit(
    PdfStream object,
    Object data
    )
  {
    PdfStream clone = (PdfStream)object.clone();
    {
      clone.header = (PdfDictionary)visit(object.header, data);
      clone.body = object.body.clone();
    }
    return clone;
  }

  @Override
  public PdfObject visit(
    XRefStream object,
    Object data
    )
  {throw new UnsupportedOperationException();}
  // </public>

  // <private>
  private Filter matchFilter(
    PdfObject object
    )
  {
    Filter cloneFilter = NullFilter;
    for(Filter filter : filters)
    {
      if(filter.matches(this, object))
      {
        cloneFilter = filter;
        break;
      }
    }
    return cloneFilter;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
