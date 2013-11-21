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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;
import org.pdfclown.objects.PdfTextString;

/**
  Optional content configuration [PDF:1.7:4.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public class LayerConfiguration
  extends PdfObjectWrapper<PdfDictionary>
  implements ILayerConfiguration
{
  /**
    Base state used to initialize the states of all the layers in a document when this configuration
    is applied.
  */
  private enum BaseStateEnum
  {
    /**
      All the layers are enabled.
    */
    On(PdfName.ON, true),
    /**
      All the layers are disabled.
    */
    Off(PdfName.OFF, false),
    /**
      All the layers are left unchanged.
    */
    Unchanged(PdfName.Unchanged, null);

    public static BaseStateEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return BaseStateEnum.On;

      for(BaseStateEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Base state unknown: " + name);
    }

    public static BaseStateEnum valueOf(
      Boolean enabled
      )
    {
      for(BaseStateEnum value : values())
      {
        if((enabled != null && enabled.equals(value.isEnabled())) || enabled == value.isEnabled())
          return value;
      }
      throw new UnsupportedOperationException();
    }

    private PdfName name;
    private Boolean enabled;

    private BaseStateEnum(
      PdfName name,
      Boolean enabled
      )
    {
      this.name = name;
      this.enabled = enabled;
    }

    public PdfName getName(
      )
    {return name;}

    public Boolean isEnabled(
      )
    {return enabled;}
  }

  // <class>
  // <dynamic>
  // <constructors>
  public LayerConfiguration(
    Document context
    )
  {super(context, new PdfDictionary());}

  public LayerConfiguration(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LayerConfiguration clone(
    Document context
    )
  {return (LayerConfiguration)super.clone(context);}

  // <ILayerConfiguration>
  @Override
  public String getCreator(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.Creator));}

  @Override
  public Layers getLayers(
    )
  {return Layers.wrap(getBaseDataObject().get(PdfName.Order, PdfArray.class));}

  @Override
  public ListModeEnum getListMode(
    )
  {return ListModeEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.ListMode));}

  @Override
  public Array<LayerGroup> getOptionGroups(
    )
  {return Array.wrap(LayerGroup.class, getBaseDataObject().get(PdfName.RBGroups, PdfArray.class));}

  @Override
  public String getTitle(
    )
  {return (String)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.Name));}

  @Override
  public Boolean isVisible(
    )
  {return BaseStateEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.BaseState)).isEnabled();}

  @Override
  public void setCreator(
    String value
    )
  {getBaseDataObject().put(PdfName.Creator, PdfTextString.get(value));}

  @Override
  public void setLayers(
    Layers value
    )
  {getBaseDataObject().put(PdfName.Order, value.getBaseObject());}

  @Override
  public void setListMode(
    ListModeEnum value
    )
  {getBaseDataObject().put(PdfName.ListMode, value.getName());}

  @Override
  public void setTitle(
    String value
    )
  {getBaseDataObject().put(PdfName.Name, PdfTextString.get(value));}

  @Override
  public void setVisible(
    Boolean value
    )
  {
    /*
      NOTE: Base state can be altered only in case of alternate configuration; default ones MUST be
      set to default state (that is ON).
    */
    if(!(getBaseObject().getParent() instanceof PdfDictionary)) // Not the default configuration?
    {getBaseDataObject().put(PdfName.BaseState, BaseStateEnum.valueOf(value).getName());}
  }
  // </ILayerConfiguration>
  // </public>

  // <internal>
  boolean isVisible(
    Layer layer
    )
  {
    Boolean defaultVisible = isVisible();
    if(defaultVisible == null || defaultVisible)
      return !getOffLayersObject().contains(layer.getBaseObject());
    else
      return getOnLayersObject().contains(layer.getBaseObject());
  }

  /**
    Sets the usage application for the specified factors.

    @param event Situation in which this usage application should be used. May be
      {@link PdfName#View View}, {@link PdfName#Print Print} or {@link PdfName#Export Export}.
    @param category Layer usage entry to consider when managing the states of the layer.
    @param layer Layer which should have its state automatically managed based on its usage
      information.
    @param retain Whether this usage application has to be kept or removed.
  */
  void setUsageApplication(
    PdfName event,
    PdfName category,
    Layer layer,
    boolean retain
    )
  {
    boolean matched = false;
    PdfArray usages = getBaseDataObject().resolve(PdfName.AS, PdfArray.class);
    for(PdfDirectObject usage : usages)
    {
      PdfDictionary usageDictionary = (PdfDictionary)usage;
      if(usageDictionary.get(PdfName.Event).equals(event)
        && ((PdfArray)usageDictionary.get(PdfName.Category)).contains(category))
      {
        PdfArray usageLayers = usageDictionary.resolve(PdfName.OCGs, PdfArray.class);
        if(usageLayers.contains(layer.getBaseObject()))
        {
          if(!retain)
          {usageLayers.remove(layer.getBaseObject());}
        }
        else
        {
          if(retain)
          {usageLayers.add(layer.getBaseObject());}
        }
        matched = true;
      }
    }
    if(!matched && retain)
    {
      PdfDictionary usageDictionary = new PdfDictionary();
      {
        usageDictionary.put(PdfName.Event, event);
        usageDictionary.resolve(PdfName.Category, PdfArray.class).add(category);
        usageDictionary.resolve(PdfName.OCGs, PdfArray.class).add(layer.getBaseObject());
      }
      usages.add(usageDictionary);
    }
  }

  void setVisible(
    Layer layer,
    boolean value
    )
  {
    PdfDirectObject layerObject = layer.getBaseObject();
    PdfArray offLayersObject = getOffLayersObject();
    PdfArray onLayersObject = getOnLayersObject();
    Boolean defaultVisible = isVisible();
    if(defaultVisible == null)
    {
      if(value && !onLayersObject.contains(layerObject))
      {
        onLayersObject.add(layerObject);
        offLayersObject.remove(layerObject);
      }
      else if(!value && !offLayersObject.contains(layerObject))
      {
        offLayersObject.add(layerObject);
        onLayersObject.remove(layerObject);
      }
    }
    else if(!defaultVisible)
    {
      if(value && !onLayersObject.contains(layerObject))
      {onLayersObject.add(layerObject);}
    }
    else
    {
      if(!value && !offLayersObject.contains(layerObject))
      {offLayersObject.add(layerObject);}
    }
  }
  // <internal>

  // <private>
  /**
    Gets the collection of the layer objects whose state is set to OFF.
  */
  private PdfArray getOffLayersObject(
    )
  {return getBaseDataObject().resolve(PdfName.OFF, PdfArray.class);}

  /**
    Gets the collection of the layer objects whose state is set to ON.
  */
  private PdfArray getOnLayersObject(
    )
  {return getBaseDataObject().resolve(PdfName.ON, PdfArray.class);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
