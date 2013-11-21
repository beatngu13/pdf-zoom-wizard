/*
  Copyright 2007-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.objects.NameTree;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;

/**
  Named destinations [PDF:1.6:3.6.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public final class NamedDestinations
  extends NameTree<Destination>
{
  // <class>
  // <dynamic>
  // <constructors>
  public NamedDestinations(
    Document context
    )
  {super(context);}

  public NamedDestinations(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public NamedDestinations clone(
    Document context
    )
  {return (NamedDestinations)super.clone(context);}
  // </public>

  // <protected>
  @Override
  protected Destination wrapValue(
    PdfDirectObject baseObject
    )
  {
    /*
      NOTE: A named destination may be either an array defining the destination,
      or a dictionary with a D entry whose value is such an array [PDF:1.6:8.2.1].
    */
    PdfDirectObject destinationObject;
    {
      PdfDataObject baseDataObject = PdfObject.resolve(baseObject);
      if(baseDataObject instanceof PdfDictionary)
      {destinationObject = ((PdfDictionary)baseDataObject).get(PdfName.D);}
      else
      {destinationObject = baseObject;}
    }
    return Destination.wrap(destinationObject);
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}