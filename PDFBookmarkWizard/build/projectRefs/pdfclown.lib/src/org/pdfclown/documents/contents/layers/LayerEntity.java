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

import java.util.List;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.PropertyList;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Layer entity.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/21/12
*/
public abstract class LayerEntity
  extends PropertyList
{
  // <class>
  // <classes>
  /**
    Membership visibility policy [PDF:1.7:4.10.1].
  */
  public enum VisibilityPolicyEnum
  {
    /**
      Visible only if all of the visibility layers are ON.
    */
    AllOn(PdfName.AllOn),
    /**
      Visible if any of the visibility layers are ON.
    */
    AnyOn(PdfName.AnyOn),
    /**
      Visible if any of the visibility layers are OFF.
    */
    AnyOff(PdfName.AnyOff),
    /**
      Visible only if all of the visibility layers are OFF.
    */
    AllOff(PdfName.AllOff);

    public static VisibilityPolicyEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return VisibilityPolicyEnum.AnyOn;

      for(VisibilityPolicyEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Visibility policy unknown: " + name);
    }

    private PdfName name;

    private VisibilityPolicyEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  protected LayerEntity(
    Document context,
    PdfName typeName
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {typeName}
      ));
  }

  protected LayerEntity(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the default membership.
    This collection corresponds to the hierarchical relation between this layer entity and its
    ascendants.
  */
  public LayerMembership getMembership(
    )
  {return null;}

  /**
    Gets the layers whose states determine the visibility of content controlled by this entity.
  */
  public List<Layer> getVisibilityLayers(
    )
  {return null;}

  /**
    Gets the visibility policy of this entity.
  */
  public VisibilityPolicyEnum getVisibilityPolicy(
    )
  {return VisibilityPolicyEnum.AllOn;}

  /**
    @see #getVisibilityPolicy()
  */
  public void setVisibilityPolicy(
    VisibilityPolicyEnum value
    )
  {}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
