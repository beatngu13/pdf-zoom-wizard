/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;

/**
  Rubber stamp annotation [PDF:1.6:8.4.5].
  <p>It displays text or graphics intended to look as if they were stamped
  on the page with a rubber stamp.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF13)
public final class RubberStamp
  extends Annotation
{
  // <class>
  // <classes>
  /**
    Icon to be used in displaying the annotation [PDF:1.6:8.4.5].
  */
  public enum IconTypeEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Approved.
    */
    Approved(PdfName.Approved),
    /**
      As is.
    */
    AsIs(PdfName.AsIs),
    /**
      Confidential.
    */
    Confidential(PdfName.Confidential),
    /**
      Departmental.
    */
    Departmental(PdfName.Departmental),
    /**
      Draft.
    */
    Draft(PdfName.Draft),
    /**
      Experimental.
    */
    Experimental(PdfName.Experimental),
    /**
      Expired.
    */
    Expired(PdfName.Expired),
    /**
      Final.
    */
    Final(PdfName.Final),
    /**
      For comment.
    */
    ForComment(PdfName.ForComment),
    /**
      For public release.
    */
    ForPublicRelease(PdfName.ForPublicRelease),
    /**
      Not approved.
    */
    NotApproved(PdfName.NotApproved),
    /**
      Not for public release.
    */
    NotForPublicRelease(PdfName.NotForPublicRelease),
    /**
      Sold.
    */
    Sold(PdfName.Sold),
    /**
      Top secret.
    */
    TopSecret(PdfName.TopSecret);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the markup type corresponding to the given value.
    */
    public static IconTypeEnum get(
      PdfName value
      )
    {
      for(IconTypeEnum iconType : IconTypeEnum.values())
      {
        if(iconType.getCode().equals(value))
          return iconType;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfName code;
    // </fields>

    // <constructors>
    private IconTypeEnum(
      PdfName code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfName getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public RubberStamp(
    Page page,
    Rectangle2D box,
    String text,
    IconTypeEnum iconType
    )
  {
    super(page, PdfName.Stamp, box, text);
    setIconType(iconType);
  }

  RubberStamp(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public RubberStamp clone(
    Document context
    )
  {return (RubberStamp)super.clone(context);}

  /**
    Gets the icon to be used in displaying the annotation.
  */
  public IconTypeEnum getIconType(
    )
  {
    PdfName nameObject = (PdfName)getBaseDataObject().get(PdfName.Name);
    return nameObject != null ? IconTypeEnum.get(nameObject) : IconTypeEnum.Draft;
  }

  /**
    @see #getIconType()
  */
  public void setIconType(
    IconTypeEnum value
    )
  {getBaseDataObject().put(PdfName.Name, value.getCode());}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}