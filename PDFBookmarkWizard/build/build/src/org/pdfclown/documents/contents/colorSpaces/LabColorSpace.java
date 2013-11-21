/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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
import java.util.Iterator;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.Interval;

/**
  CIE-based ABC double-transformation-stage color space, where A, B and C represent the L*, a* and b*
  components of a CIE 1976 L*a*b* space [PDF:1.6:4.5.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/09/11
*/
@PDF(VersionEnum.PDF11)
public final class LabColorSpace
  extends CIEBasedColorSpace
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  LabColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public LabColorSpace clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public LabColor getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {return new LabColor(components);}

  @Override
  public int getComponentCount(
    )
  {return 3;}

  @Override
  public LabColor getDefaultColor(
    )
  {
    List<Interval<Double>> ranges = getRanges();

    return new LabColor(
      ranges.get(0).getLow(),
      ranges.get(1).getLow(),
      ranges.get(2).getLow()
      );
  }

  /**
    Gets the (inclusive) ranges of the color components.
    <p>Component values falling outside the specified range are adjusted
    to the nearest valid value.</p>
  */
  //TODO:generalize to all the color spaces!
  public List<Interval<Double>> getRanges(
    )
  {
    ArrayList<Interval<Double>> ranges = new ArrayList<Interval<Double>>();
    {
      // 1. L* component.
      ranges.add(
        new Interval<Double>(0d, 100d)
        );

      PdfArray rangesObject = (PdfArray)getDictionary().get(PdfName.Range);
      if(rangesObject == null)
      {
        // 2. a* component.
        ranges.add(
          new Interval<Double>(-100d, 100d)
          );
        // 3. b* component.
        ranges.add(
          new Interval<Double>(-100d, 100d)
          );
      }
      else
      {
        // 2/3. a*/b* components.
        Iterator<PdfDirectObject> rangesObjectIterator = rangesObject.iterator();
        while(rangesObjectIterator.hasNext())
        {
          ranges.add(
            new Interval<Double>(
              ((PdfNumber<?>)rangesObjectIterator.next()).getDoubleValue(),
              ((PdfNumber<?>)rangesObjectIterator.next()).getDoubleValue()
              )
            );
        }
      }
    }
    return ranges;
  }

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    // FIXME: temporary hack
    return new java.awt.Color(0,0,0);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}