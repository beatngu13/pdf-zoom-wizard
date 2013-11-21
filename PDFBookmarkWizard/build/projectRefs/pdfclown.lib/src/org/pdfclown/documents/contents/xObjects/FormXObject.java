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

package org.pdfclown.documents.contents.xObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.RotationEnum;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.Rectangle;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.geom.Dimension;

/**
  Form external object [PDF:1.6:4.9].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class FormXObject
  extends XObject
  implements IContentContext
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static FormXObject wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new FormXObject(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new form within the specified document context.

    @param context Document where to place this form.
    @param size Form size.
  */
  public FormXObject(
    Document context,
    Dimension2D size
    )
  {this(context, new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()));}

  /**
    Creates a new form within the specified document context.

    @param context Document where to place this form.
    @param box Form box.
  */
  public FormXObject(
    Document context,
    Rectangle2D box
    )
  {
    super(context);

    getBaseDataObject().getHeader().put(PdfName.Subtype, PdfName.Form);
    setBox(box);
  }

  private FormXObject(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public FormXObject clone(
    Document context
    )
  {return (FormXObject)super.clone(context);}

  @Override
  public AffineTransform getMatrix(
    )
  {
    /*
      NOTE: Form-space-to-user-space matrix is identity [1 0 0 1 0 0] by default,
      but may be adjusted by setting the Matrix entry in the form dictionary [PDF:1.6:4.9].
    */
    PdfArray matrix = (PdfArray)getBaseDataObject().getHeader().resolve(PdfName.Matrix);
    if(matrix == null)
      return new AffineTransform();
    else
      return new AffineTransform(
        ((PdfNumber<?>)matrix.get(0)).getDoubleValue(),
        ((PdfNumber<?>)matrix.get(1)).getDoubleValue(),
        ((PdfNumber<?>)matrix.get(2)).getDoubleValue(),
        ((PdfNumber<?>)matrix.get(3)).getDoubleValue(),
        ((PdfNumber<?>)matrix.get(4)).getDoubleValue(),
        ((PdfNumber<?>)matrix.get(5)).getDoubleValue()
        );
  }

  /**
    Gets the form size.
  */
  @Override
  public Dimension2D getSize(
    )
  {
    PdfArray box = (PdfArray)getBaseDataObject().getHeader().resolve(PdfName.BBox);
    return new Dimension(
      ((PdfNumber<?>)box.get(2)).getDoubleValue(),
      ((PdfNumber<?>)box.get(3)).getDoubleValue()
      );
  }

  /**
    @see #getBox()
  */
  public void setBox(
    Rectangle2D value
    )
  {getBaseDataObject().getHeader().put(PdfName.BBox, new Rectangle(value).getBaseDataObject());}

  @Override
  public void setMatrix(
    AffineTransform value
    )
  {
    getBaseDataObject().getHeader().put(
      PdfName.Matrix,
      value != null
        ? new PdfArray(
            PdfReal.get(value.getScaleX()),
            PdfReal.get(value.getShearY()),
            PdfReal.get(value.getShearX()),
            PdfReal.get(value.getScaleY()),
            PdfReal.get(value.getTranslateX()),
            PdfReal.get(value.getTranslateY())
          )
        : null
      );
  }

  /**
    @see #getResources()
  */
  public void setResources(
    Resources value
    )
  {getBaseDataObject().getHeader().put(PdfName.Resources, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getSize()
  */
  @Override
  public void setSize(
    Dimension2D value
    )
  {
    PdfArray boxObject = (PdfArray)getBaseDataObject().getHeader().resolve(PdfName.BBox);
    boxObject.set(2, PdfReal.get(value.getWidth()));
    boxObject.set(3, PdfReal.get(value.getHeight()));
  }

  // <IContentContext>
  @Override
  public Rectangle2D getBox(
    )
  {return Rectangle.wrap(getBaseDataObject().getHeader().get(PdfName.BBox)).toRectangle2D();}

  @Override
  public Contents getContents(
    )
  {return Contents.wrap(getBaseObject(), this);}

  @Override
  public Resources getResources(
    )
  {return Resources.wrap(getBaseDataObject().getHeader().get(PdfName.Resources, PdfDictionary.class));}

  @Override
  public RotationEnum getRotation(
    )
  {return RotationEnum.Downward;}

  @Override
  public void render(
    Graphics2D context,
    Dimension2D size
    )
  {
    ContentScanner scanner = new ContentScanner(getContents());
    scanner.render(context,size);
  }

  // <IContentEntity>
  @Override
  public ContentObject toInlineObject(
    PrimitiveComposer composer
    )
  {throw new NotImplementedException();}

  @Override
  public XObject toXObject(
    Document context
    )
  {return clone(context);}
  // </IContentEntity>
  // </IContentContext>
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}