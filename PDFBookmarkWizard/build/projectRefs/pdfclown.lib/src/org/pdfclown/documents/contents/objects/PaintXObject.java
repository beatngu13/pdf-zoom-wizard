/*
  Copyright 2007-2011 Stefano Chizzolini. http://www.pdfclown.org

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
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  'Paint the specified XObject' operation [PDF:1.6:4.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF10)
public final class PaintXObject
  extends Operation
  implements IResourceReference<XObject>
{
  // <class>
  // <static>
  // <fields>
  public static final String Operator = "Do";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public PaintXObject(
    PdfName name
    )
  {super(Operator,name);}

  public PaintXObject(
    List<PdfDirectObject> operands
    )
  {super(Operator,operands);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the scanner for the contents of the painted external object.

    @param context Scanning context.
  */
  public ContentScanner getScanner(
    ContentScanner context
    )
  {
    XObject xObject = getXObject(context.getContentContext());
    return xObject instanceof FormXObject
      ? new ContentScanner((FormXObject)xObject, context)
      : null;
  }

  /**
    Gets the {@link XObject external object} resource to be painted.

    @param context Content context.
  */
  public XObject getXObject(
    IContentContext context
    )
  {return getResource(context);}

  // <IResourceReference>
  @Override
  public PdfName getName(
    )
  {return (PdfName)operands.get(0);}

  @Override
  public XObject getResource(
    IContentContext context
    )
  {return context.getResources().getXObjects().get(getName());}

  @Override
  public void setName(
    PdfName value
    )
  {operands.set(0,value);}
  // </IResourceReference>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}