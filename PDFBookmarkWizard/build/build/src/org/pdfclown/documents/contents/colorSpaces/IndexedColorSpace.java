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

import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.util.IDataWrapper;
import org.pdfclown.util.NotImplementedException;

/**
  Indexed color space [PDF:1.6:4.5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF11)
public final class IndexedColorSpace
  extends SpecialColorSpace<PdfArray>
{
  // <class>
  // <dynamic>
  // <fields>
  private final Map<Integer,Color<?>> baseColors = new HashMap<Integer,Color<?>>();
  private byte[] baseComponentValues;
  private ColorSpace<?> baseSpace;
  // </fields>

  // <constructors>
  //TODO:IMPL new element constructor!

  IndexedColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Gets the color corresponding to the specified table index resolved according to
    the {@link #getBaseSpace() base space}.
  */
  public Color<?> getBaseColor(
    IndexedColor color
    )
  {
    int colorIndex = color.getIndex();
    Color<?> baseColor = baseColors.get(colorIndex);
    if(baseColor == null)
    {
      ColorSpace<?> baseSpace = getBaseSpace();
      List<PdfDirectObject> components = new ArrayList<PdfDirectObject>();
      {
        int componentCount = baseSpace.getComponentCount();
        int componentValueIndex = colorIndex * componentCount;
        byte[] baseComponentValues = getBaseComponentValues();
        for(
          int componentIndex = 0;
          componentIndex < componentCount;
          componentIndex++
          )
        {
          components.add(
            PdfReal.get((baseComponentValues[componentValueIndex++] & 0xff) / 255d)
            );
        }
      }
      baseColor = baseSpace.getColor(components, null);
    }
    return baseColor;
  }

  /**
    Gets the base color space in which the values in the color table
    are to be interpreted.
  */
  public ColorSpace<?> getBaseSpace(
    )
  {
    if(baseSpace == null)
    {baseSpace = ColorSpace.wrap(getBaseDataObject().get(1));}
    return baseSpace;
  }

  @Override
  public IndexedColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new IndexedColor(components);}

  @Override
  public int getComponentCount(
    )
  {return 1;}

  @Override
  public Color<?> getDefaultColor(
    )
  {return IndexedColor.Default;}

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    return getBaseSpace().getPaint(
      getBaseColor((IndexedColor)color)
      );
  }
  // </public>

  // <private>
  /**
    Gets the color table.
  */
  private byte[] getBaseComponentValues(
    )
  {
    if(baseComponentValues == null)
    {baseComponentValues = ((IDataWrapper)getBaseDataObject().resolve(3)).toByteArray();}
    return baseComponentValues;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
