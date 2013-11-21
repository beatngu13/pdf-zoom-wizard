/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.colorSpaces;

import java.util.Iterator;
import java.util.List;

import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfReal;

/**
  Color value defined by numeric-level components.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 02/04/12
*/
public abstract class LeveledColor
  extends Color<PdfArray>
{
  // <class>
  // <dynamic>
  // <constructors>
  protected LeveledColor(
    ColorSpace<?> colorSpace,
    PdfDirectObject baseObject
    )
  {super(colorSpace,baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public final boolean equals(
    Object object
    )
  {
    if(object == null
      || !object.getClass().equals(getClass()))
      return false;

    Iterator<PdfDirectObject> objectIterator = ((LeveledColor)object).getBaseDataObject().iterator();
    Iterator<PdfDirectObject> thisIterator = getBaseDataObject().iterator();
    while(thisIterator.hasNext())
    {
      if(!thisIterator.next().equals(objectIterator.next()))
        return false;
    }
    return true;
  }

  @Override
  public final List<PdfDirectObject> getComponents(
    )
  {return getBaseDataObject();}

  @Override
  public final int hashCode(
    )
  {
    int hashCode = 0;
    for(PdfDirectObject component : getBaseDataObject())
    {hashCode ^= component.hashCode();}
    return hashCode;
  }
  // </public>

  // <protected>
  /**
    Gets the specified color component.

    @param index Component index.
  */
  protected final double getComponentValue(
    int index
    )
  {return ((PdfNumber<?>)getComponents().get(index)).getDoubleValue();}

  /**
    @see #getComponentValue(int)
  */
  protected final void setComponentValue(
    int index,
    double value
    )
  {getComponents().set(index, PdfReal.get(normalizeComponent(value)));}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}
