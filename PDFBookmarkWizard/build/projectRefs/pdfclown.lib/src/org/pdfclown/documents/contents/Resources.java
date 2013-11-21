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

package org.pdfclown.documents.contents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.ColorSpace;
import org.pdfclown.documents.contents.colorSpaces.Pattern;
import org.pdfclown.documents.contents.colorSpaces.Shading;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.objects.ICompositeMap;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Resources collection [PDF:1.6:3.7.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Resources
  extends PdfObjectWrapper<PdfDictionary>
  implements ICompositeMap<PdfName>
{
  // <class>
  // <static>
  // <interface>
  public static Resources wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Resources(baseObject) : null;}
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public Resources(
    Document context
    )
  {super(context, new PdfDictionary());}

  private Resources(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Resources clone(
    Document context
    )
  {return (Resources)super.clone(context);}

  public ColorSpaceResources getColorSpaces(
    )
  {return new ColorSpaceResources(getBaseDataObject().get(PdfName.ColorSpace, PdfDictionary.class));}

  public ExtGStateResources getExtGStates(
    )
  {return new ExtGStateResources(getBaseDataObject().get(PdfName.ExtGState, PdfDictionary.class));}

  public FontResources getFonts(
    )
  {return new FontResources(getBaseDataObject().get(PdfName.Font, PdfDictionary.class));}

  public PatternResources getPatterns(
    )
  {return new PatternResources(getBaseDataObject().get(PdfName.Pattern, PdfDictionary.class));}

  @PDF(VersionEnum.PDF12)
  public PropertyListResources getPropertyLists(
    )
  {return new PropertyListResources(getBaseDataObject().get(PdfName.Properties, PdfDictionary.class));}

  @PDF(VersionEnum.PDF13)
  public ShadingResources getShadings(
    )
  {return new ShadingResources(getBaseDataObject().get(PdfName.Shading, PdfDictionary.class));}

  public XObjectResources getXObjects(
    )
  {return new XObjectResources(getBaseDataObject().get(PdfName.XObject, PdfDictionary.class));}

  public void setColorSpaces(
    ColorSpaceResources value
    )
  {getBaseDataObject().put(PdfName.ColorSpace,value.getBaseObject());}

  public void setExtGStates(
    ExtGStateResources value
    )
  {getBaseDataObject().put(PdfName.ExtGState,value.getBaseObject());}

  public void setFonts(
    FontResources value
    )
  {getBaseDataObject().put(PdfName.Font,value.getBaseObject());}

  public void setPatterns(
    PatternResources value
    )
  {getBaseDataObject().put(PdfName.Pattern,value.getBaseObject());}

  public void setPropertyLists(
    PropertyListResources value
    )
  {
    checkCompatibility("propertyLists");
    getBaseDataObject().put(PdfName.Properties,value.getBaseObject());
  }

  public void setShadings(
    ShadingResources value
    )
  {
    checkCompatibility("shadings");
    getBaseDataObject().put(PdfName.Shading,value.getBaseObject());
  }

  public void setXObjects(
    XObjectResources value
    )
  {getBaseDataObject().put(PdfName.XObject,value.getBaseObject());}

  // <ICompositeMap>
  @Override
  @SuppressWarnings("unchecked")
  public <T extends PdfObjectWrapper<? extends PdfDataObject>> ResourceItems<T> get(
    Class<T> type
    )
  {
    if(ColorSpace.class.isAssignableFrom(type))
      return (ResourceItems<T>)getColorSpaces();
    else if(ExtGState.class.isAssignableFrom(type))
      return (ResourceItems<T>)getExtGStates();
    else if(Font.class.isAssignableFrom(type))
      return (ResourceItems<T>)getFonts();
    else if(Pattern.class.isAssignableFrom(type))
      return (ResourceItems<T>)getPatterns();
    else if(PropertyList.class.isAssignableFrom(type))
      return (ResourceItems<T>)getPropertyLists();
    else if(Shading.class.isAssignableFrom(type))
      return (ResourceItems<T>)getShadings();
    else if(XObject.class.isAssignableFrom(type))
      return (ResourceItems<T>)getXObjects();
    else
      return null;
  }

  @Override
  public <T extends PdfObjectWrapper<? extends PdfDataObject>> T get(
    Class<T> type,
    PdfName key
    )
  {return get(type).get(key);}
  // </ICompositeMap>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}