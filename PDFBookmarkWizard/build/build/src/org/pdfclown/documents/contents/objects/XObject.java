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

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.objects.PdfName;

/**
  External object shown in a content stream context [PDF:1.6:4.7].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF10)
public final class XObject
  extends GraphicsObject
  implements IResourceReference<org.pdfclown.documents.contents.xObjects.XObject>
{
  // <class>
  // <static>
  // <fields>
  public static final String BeginOperator = PaintXObject.Operator;
  public static final String EndOperator = BeginOperator;
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  // </fields>

  // <constructors>
  public XObject(
    PaintXObject operation
    )
  {super(operation);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the scanner for this object's contents.

    @param context Scanning context.
  */
  public ContentScanner getScanner(
    ContentScanner context
    )
  {return getOperation().getScanner(context);}

  // <IResourceReference>
  @Override
  public PdfName getName(
    )
  {return getOperation().getName();}

  @Override
  public org.pdfclown.documents.contents.xObjects.XObject getResource(
    IContentContext context
    )
  {return getOperation().getResource(context);}

  @Override
  public void setName(
    PdfName value
    )
  {getOperation().setName(value);}
  // </IResourceReference>
  // </public>

  // <private>
  private PaintXObject getOperation(
    )
  {return (PaintXObject)getObjects().get(0);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}