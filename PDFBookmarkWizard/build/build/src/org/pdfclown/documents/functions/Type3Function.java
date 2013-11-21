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

package org.pdfclown.documents.functions;

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.util.math.Interval;

/**
  Stitching function producing a single new 1-input function from the combination of the subdomains
  of {@link #getFunctions() several 1-input functions} [PDF:1.6:3.9.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class Type3Function
  extends Function<PdfDictionary>
{
  // <class>
  // <dynamic>
  // <constructors>
  //TODO:implement function creation!

  Type3Function(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public double[] calculate(
    double[] inputs
    )
  {
    // FIXME: Auto-generated method stub
    return null;
  }

  @Override
  public Type3Function clone(
    Document context
    )
  {return (Type3Function)super.clone(context);}

  /**
    Gets the {@link Function#getDomains() domain} partition bounds whose resulting intervals are respectively applied
    to each {@link #getFunctions() function}.
  */
  public List<Double> getDomainBounds(
    )
  {
    List<Double> domainBounds = new ArrayList<Double>();
    {
      PdfArray domainBoundsObject = (PdfArray)getDictionary().resolve(PdfName.Bounds);
      for(PdfDirectObject domainBoundObject : domainBoundsObject)
      {domainBounds.add(((PdfNumber<?>)domainBoundObject).getDoubleValue());}
    }
    return domainBounds;
  }

  /**
    Gets the mapping of each {@link #getDomainBounds() subdomain} into the domain of the corresponding
    {@link #getFunctions() function}.
  */
  public List<Interval<Double>> getDomainEncodes(
    )
  {return getIntervals(PdfName.Encode, null);}

  /**
    Gets the 1-input functions making up this stitching function.
    <p>The output dimensionality of all functions must be the same.</p>
  */
  public Functions getFunctions(
    )
  {return new Functions(getDictionary().get(PdfName.Functions), this);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
