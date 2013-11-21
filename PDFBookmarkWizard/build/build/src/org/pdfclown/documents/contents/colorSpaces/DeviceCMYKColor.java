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

import java.util.Arrays;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.util.NotImplementedException;

/**
  Device Cyan-Magenta-Yellow-Key color value [PDF:1.6:4.5.3].
  <p>The 'Key' component is renamed 'Black' to avoid semantic ambiguities.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 02/04/12
*/
@PDF(VersionEnum.PDF11)
public final class DeviceCMYKColor
  extends DeviceColor
{
  // <class>
  // <static>
  // <fields>
  public static final DeviceCMYKColor Black = new DeviceCMYKColor(0,0,0,1);
  public static final DeviceCMYKColor White = new DeviceCMYKColor(0,0,0,0);

  public static final DeviceCMYKColor Default = Black;
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the color corresponding to the specified components.

    @param components Color components to convert.
    @since 0.1.0
   */
  public static DeviceCMYKColor get(
    PdfArray components
    )
  {
    return (components != null
      ? new DeviceCMYKColor(components)
      : Default
      );
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public DeviceCMYKColor(
    double c,
    double m,
    double y,
    double k
    )
  {
    this(
      Arrays.asList(
        PdfReal.get(normalizeComponent(c)),
        PdfReal.get(normalizeComponent(m)),
        PdfReal.get(normalizeComponent(y)),
        PdfReal.get(normalizeComponent(k))
        )
      );
  }

  DeviceCMYKColor(
    List<? extends PdfDirectObject> components
    )
  {
    super(
      DeviceCMYKColorSpace.Default,
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

  /**
    Gets the cyan component.
  */
  public double getC(
    )
  {return getComponentValue(0);}

  /**
    Gets the black (key) component.
  */
  public double getK(
    )
  {return getComponentValue(3);}

  /**
    Gets the magenta component.
  */
  public double getM(
    )
  {return getComponentValue(1);}

  /**
    Gets the yellow component.
  */
  public double getY(
    )
  {return getComponentValue(2);}

  /**
    @see #getC()
  */
  public void setC(
    double value
    )
  {setComponentValue(0, value);}

  /**
    @see #getK()
  */
  public void setK(
    double value
    )
  {setComponentValue(3, value);}

  /**
    @see #getM()
  */
  public void setM(
    double value
    )
  {setComponentValue(1, value);}

  /**
    @see #getY()
  */
  public void setY(
    double value
    )
  {setComponentValue(2, value);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}