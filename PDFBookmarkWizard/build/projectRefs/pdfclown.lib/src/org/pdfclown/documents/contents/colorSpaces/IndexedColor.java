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

import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.util.NotImplementedException;

/**
  Indexed color value [PDF:1.6:4.5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 08/23/12
*/
@PDF(VersionEnum.PDF11)
public final class IndexedColor
  extends Color<PdfArray>
{
  // <class>
  // <static>
  // <fields>
  public static final IndexedColor Default = new IndexedColor(0);
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the color corresponding to the specified components.

    @param components Color components to convert.
    @since 0.1.0
   */
  public static IndexedColor get(
    PdfArray components
    )
  {
    return (components != null
      ? new IndexedColor(components)
      : Default
      );
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public IndexedColor(
    int index
    )
  {this(Arrays.asList(PdfInteger.get(index)));}

  IndexedColor(
    List<? extends PdfDirectObject> components
    )
  {
    super(
      null, //TODO:consider color space reference!
      new PdfArray(components)
      );
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public List<PdfDirectObject> getComponents(
    )
  {return getBaseDataObject();}

  /**
    Gets the color index.
  */
  public int getIndex(
    )
  {return ((PdfInteger)getBaseDataObject().get(0)).getValue().intValue();}

  /**
    @see #getIndex()
  */
  public void setIndex(
    int value
    )
  {getBaseDataObject().set(0, PdfInteger.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
