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

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfTextString;
import org.pdfclown.util.math.Interval;

/**
  Optional content group [PDF:1.7:4.10.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF15)
public final class Layer
  extends LayerEntity
  implements ILayerNode
{
  /**
    Sublayers location within a configuration structure.
  */
  private static class LayersLocation
  {
    /**
      Sublayers ordinal position within the parent sublayers.
    */
    final int index;
    /**
      Parent layer object.
    */
    final PdfDirectObject parentLayerObject;
    /**
      Parent sublayers object.
    */
    final PdfArray parentLayersObject;
    /**
      Upper levels.
    */
    final Stack<Object[]> levels;

    public LayersLocation(
      PdfDirectObject parentLayerObject,
      PdfArray parentLayersObject,
      int index,
      Stack<Object[]> levels
      )
    {
      this.parentLayerObject = parentLayerObject;
      this.parentLayersObject = parentLayersObject;
      this.index = index;
      this.levels = levels;
    }
  }

  /**
    Layer state.
  */
  private enum StateEnum
  {
    /**
      Active.
    */
    On(PdfName.ON, true),
    /**
      Inactive.
    */
    Off(PdfName.OFF, false);

    public static StateEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return StateEnum.On;

      for(StateEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("State unknown: " + name);
    }

    public static StateEnum valueOf(
      Boolean enabled
      )
    {
      if(enabled == null)
        return StateEnum.On;

      for(StateEnum value : values())
      {
        if(enabled.equals(value.isEnabled()))
          return value;
      }
      throw new UnsupportedOperationException();
    }

    private boolean enabled;
    private PdfName name;

    private StateEnum(
      PdfName name,
      boolean enabled
      )
    {
      this.name = name;
      this.enabled = enabled;
    }

    public PdfName getName(
      )
    {return name;}

    public boolean isEnabled(
      )
    {return enabled;}
  }
  // </classes>

  // <static>
  // <fields>
  public static final PdfName TypeName = PdfName.OCG;

  private static final PdfName MembershipName = new PdfName("D-OCMD");
  // </fields>

  // <interface>
  // <public>
  public static Layer wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Layer(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Layer(
    Document context,
    String title
    )
  {
    super(context, PdfName.OCG);
    setTitle(title);

    // Add this layer to the global collection!
    /*
      NOTE: Every layer MUST be included in the global collection [PDF:1.7:4.10.3].
    */
    LayerDefinition definition = context.getLayer();
    if(definition == null)
    {context.setLayer(definition = new LayerDefinition(context));}
    definition.getAllLayersObject().add(getBaseObject());
  }

  private Layer(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Layer clone(
    Document context
    )
  {return (Layer)super.clone(context);}

  /**
    Gets the name of the type of content controlled by the group.
  */
  public String getContentType(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.CreatorInfo).get(PdfName.Subtype));}

  /**
    Gets the name of the application that created this layer.
  */
  public String getCreator(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.CreatorInfo).get(PdfName.Creator));}

  /**
    Gets the language (and possibly locale) of the content controlled by this layer.
  */
  public String getLanguage(
    )
  {return (String)PdfSimpleObject.getValue(getUsageEntry(PdfName.Language).get(PdfName.Lang));}

  /**
    Gets the sublayers.
  */
  @Override
  public Layers getLayers(
    )
  {
    LayersLocation location = findLayersLocation();
    return Layers.wrap(location.parentLayersObject.get(location.index, PdfArray.class));
  }

  @Override
  public LayerMembership getMembership(
    )
  {
    LayerMembership membership;
    PdfDirectObject membershipObject = getBaseDataObject().get(MembershipName);
    if(membershipObject == null)
    {
      membership = new LayerMembership(getDocument());
      membership.setVisibilityPolicy(VisibilityPolicyEnum.AllOn); // NOTE: Forces visibility to depend on all the ascendant layers.
      getBaseDataObject().put(MembershipName, membershipObject = membership.getBaseObject());
    }
    else
    {membership = LayerMembership.wrap(membershipObject);}
    if(membership.getVisibilityLayers().isEmpty())
    {
      membership.getVisibilityLayers().add(this);
      LayersLocation location = findLayersLocation();
      if(location.parentLayerObject != null)
      {membership.getVisibilityLayers().add(new Layer(location.parentLayerObject));}
      for(Object[] level : location.levels)
      {
        PdfDirectObject layerObject = (PdfDirectObject)level[2];
        if(layerObject != null)
        {membership.getVisibilityLayers().add(new Layer(layerObject));}
      }
    }
    return membership;
  }

  @Override
  public List<Layer> getVisibilityLayers(
    )
  {return getMembership().getVisibilityLayers();}

  @Override
  public VisibilityPolicyEnum getVisibilityPolicy(
    )
  {return getMembership().getVisibilityPolicy();}

  /**
    Gets the range of magnifications at which the content in this layer is best viewed.
  */
  public Interval<Double> getZoomRange(
    )
  {
    PdfDictionary zoomDictionary = getUsageEntry(PdfName.Zoom);
    PdfNumber<?> minObject = (PdfNumber<?>)zoomDictionary.resolve(PdfName.min);
    PdfNumber<?> maxObject = (PdfNumber<?>)zoomDictionary.resolve(PdfName.max);
    return new Interval<Double>(
      minObject != null ? minObject.getDoubleValue() : 0,
      maxObject != null ? maxObject.getDoubleValue() : Double.POSITIVE_INFINITY
      );
  }

  /**
    Gets whether this layer is visible when the document is saved by a viewer application to a
    format that does not support optional content.
  */
  public boolean isExportable(
    )
  {return StateEnum.valueOf((PdfName)getUsageEntry(PdfName.Export).get(PdfName.ExportState)).isEnabled();}

  /**
    Gets whether the default visibility of this layer cannot be changed through the user interface
    of a viewer application.
  */
  public boolean isLocked(
    )
  {return getDefaultConfiguration().getBaseDataObject().resolve(PdfName.Locked, PdfArray.class).contains(getBaseObject());}

  /**
    Gets whether this layer is visible when the document is printed from a viewer application.
  */
  public boolean isPrintable(
    )
  {return StateEnum.valueOf((PdfName)getUsageEntry(PdfName.Print).get(PdfName.PrintState)).isEnabled();}

  /**
    Gets whether this layer is visible when the document is opened in a viewer application.
  */
  public boolean isViewable(
    )
  {return StateEnum.valueOf((PdfName)getUsageEntry(PdfName.View).get(PdfName.ViewState)).isEnabled();}

  /**
    Gets whether this layer is initially visible.
  */
  public boolean isVisible(
    )
  {return getDefaultConfiguration().isVisible(this);}

  /**
    @see #getContentType()
  */
  public void setContentType(
    String value
    )
  {getUsageEntry(PdfName.CreatorInfo).put(PdfName.Subtype, PdfName.get(value));}

  /**
    @see #getCreator()
  */
  public void setCreator(
    String value
    )
  {getUsageEntry(PdfName.CreatorInfo).put(PdfName.Creator, PdfTextString.get(value));}

  /**
    @see #isExportable()
  */
  public void setExportable(
    boolean value
    )
  {
    getUsageEntry(PdfName.Export).put(PdfName.ExportState, StateEnum.valueOf(value).getName());
    getDefaultConfiguration().setUsageApplication(PdfName.Export, PdfName.Export, this, true);
  }

  /**
    @see #getLanguage()
  */
  public void setLanguage(
    String value
    )
  {getUsageEntry(PdfName.Language).put(PdfName.Lang, PdfTextString.get(value));}

  /**
    @see #getLayers()
  */
  public void setLayers(
    Layers value
    )
  {
    LayersLocation location = findLayersLocation();
    if(location.index == location.parentLayersObject.size())
    {location.parentLayersObject.add(value.getBaseObject());} // Appends new sublayers.
    else if(location.parentLayersObject.resolve(location.index) instanceof PdfArray)
    {location.parentLayersObject.set(location.index, value.getBaseObject());} // Substitutes old sublayers.
    else
    {location.parentLayersObject.add(location.index, value.getBaseObject());} // Inserts new sublayers.
  }

  /**
    @see #isLocked()
  */
  public void setLocked(
    boolean value
    )
  {
    PdfArray lockedArrayObject = getDefaultConfiguration().getBaseDataObject().resolve(PdfName.Locked, PdfArray.class);
    if(!lockedArrayObject.contains(getBaseObject()))
    {lockedArrayObject.add(getBaseObject());}
  }

  /**
    @see #isPrintable()
  */
  public void setPrintable(
    boolean value
    )
  {
    getUsageEntry(PdfName.Print).put(PdfName.PrintState, StateEnum.valueOf(value).getName());
    getDefaultConfiguration().setUsageApplication(PdfName.Print, PdfName.Print, this, true);
  }

  /**
    @see #isViewable()
  */
  public void setViewable(
    boolean value
    )
  {
    getUsageEntry(PdfName.View).put(PdfName.ViewState, StateEnum.valueOf(value).getName());
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.View, this, true);
  }

  @Override
  public void setVisibilityPolicy(
    VisibilityPolicyEnum value
    )
  {
    if(!value.equals(getMembership().getVisibilityPolicy()))
      throw new UnsupportedOperationException("Single layers cannot manage custom state policies; use LayerMembership instead.");
  }

  /**
    @see #isVisible()
  */
  public void setVisible(
    boolean value
    )
  {getDefaultConfiguration().setVisible(this, value);}

  public void setZoomRange(
    Interval<Double> value
    )
  {
    if(value != null)
    {
      PdfDictionary zoomDictionary = getUsageEntry(PdfName.Zoom);
      zoomDictionary.put(PdfName.min, value.getLow() != 0 ? PdfReal.get(value.getLow()) : null);
      zoomDictionary.put(PdfName.max, value.getHigh() != Double.POSITIVE_INFINITY ? PdfReal.get(value.getHigh()) : null);
    }
    else
    {getUsage().remove(PdfName.Zoom);}
    getDefaultConfiguration().setUsageApplication(PdfName.View, PdfName.Zoom, this, value != null);
  }

  @Override
  public String toString(
    )
  {return getTitle();}

  // <ILayerNode>
  @Override
  public String getTitle(
    )
  {return ((PdfTextString)getBaseDataObject().get(PdfName.Name)).getValue();}

  @Override
  public void setTitle(
    String value
    )
  {getBaseDataObject().put(PdfName.Name, new PdfTextString(value));}
  // </ILayerNode>
  // </public>

  // <private>
  /**
    Finds the location of the sublayers object in the default configuration; in case no sublayers
    object is associated to this object, its virtual position is indicated.
  */
  private LayersLocation findLayersLocation(
    )
  {
    LayersLocation location = findLayersLocation(getDefaultConfiguration());
    if(location == null)
    {
      /*
        NOTE: In case the layer is outside the default structure, it's appended to the root
        collection.
      */
      /*
        TODO: anytime a layer is inserted into a collection, the layer tree must be checked to
        avoid duplicate: in case the layer is already in the tree, it must be moved to the new
        position along with its sublayers.
      */
      Layers rootLayers = getDefaultConfiguration().getLayers();
      rootLayers.add(this);
      location = new LayersLocation(null, rootLayers.getBaseDataObject(), rootLayers.size(), new Stack<Object[]>());
    }
    return location;
  }

  /**
    Finds the location of the sublayers object in the specified configuration; in case no sublayers
    object is associated to this object, its virtual position is indicated.

    @param configuration Configuration context.
    @return <code>null</code>, if this layer is outside the specified configuration.
  */
  @SuppressWarnings("unchecked")
  private LayersLocation findLayersLocation(
    LayerConfiguration configuration
    )
  {
    /*
      NOTE: As layers are only weakly tied to configurations, their sublayers have to be sought
      through the configuration structure tree.
    */
    PdfDirectObject levelLayerObject = null;
    PdfArray levelObject = configuration.getLayers().getBaseDataObject();
    Iterator<PdfDirectObject> levelIterator = levelObject.iterator();
    Stack<Object[]> levelIterators = new Stack<Object[]>();
    PdfDirectObject thisObject = getBaseObject();
    PdfDirectObject currentLayerObject = null;
    while(true)
    {
      if(!levelIterator.hasNext())
      {
        if(levelIterators.isEmpty())
          break;

        Object[] levelItems = levelIterators.pop();
        levelObject = (PdfArray)levelItems[0];
        levelIterator = (Iterator<PdfDirectObject>)levelItems[1];
        levelLayerObject = (PdfDirectObject)levelItems[2];
        currentLayerObject = null;
      }
      else
      {
        PdfDirectObject nodeObject = levelIterator.next();
        PdfDataObject nodeDataObject = PdfObject.resolve(nodeObject);
        if(nodeDataObject instanceof PdfDictionary)
        {
          if(nodeObject.equals(thisObject))
            /*
              NOTE: Sublayers are expressed as an array immediately following the parent layer node.
            */
            return new LayersLocation(levelLayerObject, levelObject, levelObject.indexOf(thisObject) + 1, levelIterators);

          currentLayerObject = nodeObject;
        }
        else if(nodeDataObject instanceof PdfArray)
        {
          levelIterators.push(new Object[]{levelObject, levelIterator, levelLayerObject});
          levelObject = (PdfArray)nodeDataObject;
          levelIterator = levelObject.iterator();
          levelLayerObject = currentLayerObject;
          currentLayerObject = null;
        }
      }
    }
    return null;
  }

  private LayerConfiguration getDefaultConfiguration(
    )
  {return getDocument().getLayer().getDefaultConfiguration();}

  private PdfDictionary getUsage(
    )
  {return getBaseDataObject().resolve(PdfName.Usage, PdfDictionary.class);}

  private PdfDictionary getUsageEntry(
    PdfName key
    )
  {return getUsage().resolve(key, PdfDictionary.class);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}