/*
  Copyright 2010-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.PropertyList;
import org.pdfclown.documents.contents.PropertyListResources;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Abstract content marker [PDF:1.6:10.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF12)
public abstract class ContentMarker
  extends Operation
  implements IResourceReference<PropertyList>
{
  // <class>
  // <dynamic>
  // <constructors>
  protected ContentMarker(
    PdfName tag
    )
  {this(tag, null);}

  protected ContentMarker(
    PdfName tag,
    PdfDirectObject properties
    )
  {
    super(null, tag);
    if(properties != null)
    {
      operands.add(properties);
      operator = getPropertyListOperator();
    }
    else
    {operator = getSimpleOperator();}
  }

  protected ContentMarker(
    String operator,
    List<PdfDirectObject> operands
    )
  {super(operator, operands);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the private information meaningful to the program (application or plugin extension)
    creating the marked content. It can be either an inline {@link PropertyList}
    or the {@link PdfName name} of an external PropertyList resource.

    @see PropertyListResources
    @see #getProperties(IContentContext)
  */
  public Object getProperties(
    )
  {
    PdfDirectObject propertiesObject = operands.get(1);
    if(propertiesObject == null)
      return null;
    else if(propertiesObject instanceof PdfName)
      return propertiesObject;
    else if(propertiesObject instanceof PdfDictionary)
      return PropertyList.wrap(propertiesObject);
    else
      throw new UnsupportedOperationException("Property list type unknown: " + propertiesObject.getClass().getName());
  }

  /**
    Gets the private information meaningful to the program (application or plugin extension)
    creating the marked content.

    @param context Content context.
  */
  public PropertyList getProperties(
    IContentContext context
    )
  {
    Object properties = getProperties();
    return properties instanceof PdfName
      ? context.getResources().getPropertyLists().get(properties)
      : (PropertyList)properties;
  }

  /**
    Gets the marker indicating the role or significance of the marked content.
  */
  public PdfName getTag(
    )
  {return (PdfName)operands.get(0);}

  /**
    @see #getProperties()
  */
  public void setProperties(
    Object value
    )
  {
    if(value == null)
    {
      operator = getSimpleOperator();
      if(operands.size() > 1)
      {operands.remove(1);}
    }
    else
    {
      PdfDirectObject operand;
      if(value instanceof PdfName)
      {operand = (PdfName)value;}
      else if(value instanceof PropertyList)
      {operand = ((PropertyList)value).getBaseDataObject();}
      else
        throw new IllegalArgumentException("value MUST be a PdfName or a PropertyList.");

      operator = getPropertyListOperator();
      if(operands.size() > 1)
      {operands.set(1, operand);}
      else
      {operands.add(operand);}
    }
  }

  /**
    @see #getTag()
  */
  public void setTag(
    PdfName value
    )
  {operands.set(0,value);}

  // <IResourceReference>
  @Override
  public PdfName getName(
    )
  {
    Object properties = getProperties();
    return (properties instanceof PdfName ? (PdfName)properties : null);
  }

  @Override
  public PropertyList getResource(
    IContentContext context
    )
  {return getProperties(context);}

  @Override
  public void setName(
    PdfName value
    )
  {setProperties(value);}
  // </IResourceReference>
  // </public>

  // <protected>
  protected abstract String getPropertyListOperator(
    );

  protected abstract String getSimpleOperator(
    );
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}