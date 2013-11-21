/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.navigation.page;

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Page;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Article bead [PDF:1.7:8.3.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF11)
public class ArticleElement
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  public static ArticleElement wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ArticleElement(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ArticleElement(
    Page page,
    Rectangle2D box
    )
  {
    super(
      page.getDocument(),
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.Bead}
        )
      );
    page.getArticleElements().add(this);
    setBox(box);
  }

  private ArticleElement(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Deletes this bead removing also its references on the page and its article thread.
  */
  @Override
  public boolean delete(
    )
  {
    // Shallow removal (references):
    // * thread links
    getArticle().getElements().remove(this);
    // * reference on page
    getPage().getArticleElements().remove(this);

    // Deep removal (indirect object).
    return super.delete();
  }

  /**
    Gets the thread article this bead belongs to.
  */
  public Article getArticle(
    )
  {
    PdfDictionary bead = getBaseDataObject();
    Article article = null;
    while((article = Article.wrap(bead.get(PdfName.T))) == null)
    {bead = (PdfDictionary)bead.resolve(PdfName.V);}
    return article;
  }

  /**
    Gets the location on the page in default user space units.
  */
  public Rectangle2D getBox(
    )
  {
    org.pdfclown.objects.Rectangle box = org.pdfclown.objects.Rectangle.wrap(getBaseDataObject().get(PdfName.R));
    return new Rectangle2D.Double(
      box.getLeft(),
      getPage().getBox().getHeight() - box.getTop(),
      box.getWidth(),
      box.getHeight()
      );
  }

  /**
    Gets the next bead.
  */
  public ArticleElement getNext(
    )
  {return ArticleElement.wrap(getBaseDataObject().get(PdfName.N));}

  /**
    Gets the location page.
  */
  public Page getPage(
    )
  {return Page.wrap(getBaseDataObject().get(PdfName.P));}

  /**
    Gets the previous bead.
  */
  public ArticleElement getPrevious(
    )
  {return ArticleElement.wrap(getBaseDataObject().get(PdfName.V));}

  /**
    Gets whether this is the first bead in its thread.
  */
  public boolean isHead(
    )
  {
    PdfDictionary thread = (PdfDictionary)getBaseDataObject().resolve(PdfName.T);
    return thread != null && getBaseObject().equals(thread.get(PdfName.F));
  }

  /**
    @see #getBox()
  */
  public void setBox(
    Rectangle2D value
    )
  {
    getBaseDataObject().put(
      PdfName.R,
      new org.pdfclown.objects.Rectangle(
        value.getX(),
        getPage().getBox().getHeight() - value.getY(),
        value.getWidth(),
        value.getHeight()
        ).getBaseDataObject()
      );
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
