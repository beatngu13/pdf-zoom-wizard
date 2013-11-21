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

package org.pdfclown.documents.contents.colorSpaces;

import java.awt.Paint;
import java.util.List;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Color space [PDF:1.6:4.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
public abstract class ColorSpace<TDataObject extends PdfDirectObject>
  extends PdfObjectWrapper<TDataObject>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps the specified color space base object into a color space object.

    @param baseObject Base object of a color space object.
    @return Color space object corresponding to the base object.
  */
  public static ColorSpace<?> wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    // Get the data object corresponding to the color space!
    PdfDataObject baseDataObject = baseObject.resolve();
    /*
      NOTE: A color space is defined by an array object whose first element
      is a name object identifying the color space family [PDF:1.6:4.5.2].
      For families that do not require parameters, the color space CAN be
      specified simply by the family name itself instead of an array.
    */
    PdfName name = (PdfName)(baseDataObject instanceof PdfArray
      ? ((PdfArray)baseDataObject).get(0)
      : baseDataObject);
    if(name.equals(PdfName.DeviceRGB))
      return new DeviceRGBColorSpace(baseObject);
    else if(name.equals(PdfName.DeviceCMYK))
      return new DeviceCMYKColorSpace(baseObject);
    else if(name.equals(PdfName.DeviceGray))
      return new DeviceGrayColorSpace(baseObject);
    else if(name.equals(PdfName.CalRGB))
      return new CalRGBColorSpace(baseObject);
    else if(name.equals(PdfName.CalGray))
      return new CalGrayColorSpace(baseObject);
    else if(name.equals(PdfName.ICCBased))
      return new ICCBasedColorSpace(baseObject);
    else if(name.equals(PdfName.Lab))
      return new LabColorSpace(baseObject);
    else if(name.equals(PdfName.DeviceN))
      return new DeviceNColorSpace(baseObject);
    else if(name.equals(PdfName.Indexed))
      return new IndexedColorSpace(baseObject);
    else if(name.equals(PdfName.Pattern))
      return new PatternColorSpace(baseObject);
    else if(name.equals(PdfName.Separation))
      return new SeparationColorSpace(baseObject);
    else
      throw new UnsupportedOperationException("Color space " + name + " unknown.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected ColorSpace(
    Document context,
    TDataObject baseDataObject
    )
  {super(context, baseDataObject);}

  protected ColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the <b>color value</b> corresponding to the specified components
    interpreted according to this color space [PDF:1.6:4.5.1].

    @param components Color components.
    @param context Content context.
    @since 0.1.0
  */
  public abstract Color<?> getColor(
    List<PdfDirectObject> components,
    IContentContext context
    );

  /**
    Gets the number of components used to represent a color value.
  */
  public abstract int getComponentCount(
    );

  /**
    Gets the <b>initial color value</b> within this color space.
  */
  public abstract Color<?> getDefaultColor(
    );

  /**
    Gets the rendering representation of the specified color value.

    @param color Color value to convert into an equivalent rendering representation.
  */
  public abstract Paint getPaint(
    Color<?> color
    );
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}