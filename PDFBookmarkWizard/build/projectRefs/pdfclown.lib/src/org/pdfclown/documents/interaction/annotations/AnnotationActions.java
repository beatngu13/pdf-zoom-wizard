/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.util.NotImplementedException;

/**
  Annotation actions [PDF:1.6:8.5.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public class AnnotationActions
  extends PdfObjectWrapper<PdfDictionary>
  implements Map<PdfName,Action>
{
  // <class>
  // <dynamic>
  // <fields>
  private final Annotation parent;
  // </fields>

  // <constructors>
  public AnnotationActions(
    Annotation parent
    )
  {
    super(parent.getDocument(), new PdfDictionary());
    this.parent = parent;
  }

  AnnotationActions(
    Annotation parent,
    PdfDirectObject baseObject
    )
  {
    super(baseObject);
    this.parent = parent;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public AnnotationActions clone(
    Document context
    )
  {throw new NotImplementedException();} // TODO: verify parent reference.

  /**
    Gets the action to be performed when the annotation is activated.
  */
  public Action getOnActivate(
    )
  {return parent.getAction();}

  /**
    Gets the action to be performed when the cursor enters the annotation's active area.
  */
  public Action getOnEnter(
    )
  {return get(PdfName.E);}

  /**
    Gets the action to be performed when the cursor exits the annotation's active area.
  */
  public Action getOnExit(
    )
  {return get(PdfName.X);}

  /**
    Gets the action to be performed when the mouse button is pressed
    inside the annotation's active area.
  */
  public Action getOnMouseDown(
    )
  {return get(PdfName.D);}

  /**
    Gets the action to be performed when the mouse button is released
    inside the annotation's active area.
  */
  public Action getOnMouseUp(
    )
  {return get(PdfName.U);}

  /**
    Gets the action to be performed when the page containing the annotation is closed.
  */
  public Action getOnPageClose(
    )
  {return get(PdfName.PC);}

  /**
    Gets the action to be performed when the page containing the annotation
    is no longer visible in the viewer application's user interface.
  */
  public Action getOnPageInvisible(
    )
  {return get(PdfName.PI);}

  /**
    Gets the action to be performed when the page containing the annotation is opened.
  */
  public Action getOnPageOpen(
    )
  {return get(PdfName.PO);}

  /**
    Gets the action to be performed when the page containing the annotation
    becomes visible in the viewer application's user interface.
  */
  public Action getOnPageVisible(
    )
  {return get(PdfName.PV);}

  /**
    @see #getOnActivate()
  */
  public void setOnActivate(
    Action value
    )
  {parent.setAction(value);}

  /**
    @see #getOnEnter()
  */
  public void setOnEnter(
    Action value
    )
  {put(PdfName.E, value);}

  /**
    @see #getOnExit()
  */
  public void setOnExit(
    Action value
    )
  {put(PdfName.X, value);}

  /**
    @see #getOnMouseDown()
  */
  public void setOnMouseDown(
    Action value
    )
  {put(PdfName.D, value);}

  /**
    @see #getOnMouseUp()
  */
  public void setOnMouseUp(
    Action value
    )
  {put(PdfName.U, value);}

  /**
    @see #getOnPageClose()
  */
  public void setOnPageClose(
    Action value
    )
  {put(PdfName.PC, value);}

  /**
    @see #getOnPageInvisible()
  */
  public void setOnPageInvisible(
    Action value
    )
  {put(PdfName.PI, value);}

  /**
    @see #getOnPageOpen()
  */
  public void setOnPageOpen(
    Action value
    )
  {put(PdfName.PO, value);}

  /**
    @see #getOnPageVisible()
  */
  public void setOnPageVisible(
    Action value
    )
  {put(PdfName.PV, value);}

  // <Map>
  @Override
  public void clear(
    )
  {
    getBaseDataObject().clear();
    setOnActivate(null);
  }

  @Override
  public boolean containsKey(
    Object key
    )
  {
    return getBaseDataObject().containsKey(key)
      || (PdfName.A.equals(key) && parent.getBaseDataObject().containsKey(key));
  }

  @Override
  public boolean containsValue(
    Object value
    )
  {
    return value != null
      && (getBaseDataObject().containsValue(((Action)value).getBaseObject())
        || (value.equals(getOnActivate())));
  }

  @Override
  public Set<java.util.Map.Entry<PdfName, Action>> entrySet(
    )
  {throw new NotImplementedException();}

  @Override
  public Action get(
    Object key
    )
  {return Action.wrap(getBaseDataObject().get(key));}

  @Override
  public boolean isEmpty(
    )
  {return getBaseDataObject().isEmpty() && getOnActivate() == null;}

  @Override
  public Set<PdfName> keySet(
    )
  {
    HashSet<PdfName> keySet = new HashSet<PdfName>(getBaseDataObject().keySet());
    if(parent.getBaseDataObject().containsKey(PdfName.A))
    {keySet.add(PdfName.A);}

    return keySet();
  }

  @Override
  public Action put(
    PdfName key,
    Action value
    )
  {return Action.wrap(getBaseDataObject().put(key, value != null ? value.getBaseObject() : null));}

  @Override
  public void putAll(
    Map<? extends PdfName, ? extends Action> entries
    )
  {throw new NotImplementedException();}

  @Override
  public Action remove(
    Object key
    )
  {
    Action oldValue;
    if(PdfName.A.equals(key) && parent.getBaseDataObject().containsKey(key))
    {
      oldValue = getOnActivate();
      setOnActivate(null);
    }
    else
    {oldValue = Action.wrap(getBaseDataObject().remove(key));}
    return oldValue;
  }

  @Override
  public int size(
    )
  {
    return getBaseDataObject().size()
      + (parent.getBaseDataObject().containsKey(PdfName.A) ? 1 : 0);
  }

  @Override
  public Collection<Action> values(
    )
  {
    Collection<Action> values;
    {
      Collection<PdfDirectObject> objects = getBaseDataObject().values();
      values = new ArrayList<Action>(objects.size());
      for(PdfDirectObject object : objects)
      {values.add(Action.wrap(object));}
      Action action = getOnActivate();
      if(action != null)
      {values.add(action);}
    }
    return values;
  }
  // </Map>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}