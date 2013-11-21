/*
  Copyright 2012-2013 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.objects.Array;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  Page elements.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 01/04/13
*/
public abstract class PageElements<TItem extends PdfObjectWrapper<PdfDictionary>>
  extends Array<TItem>
{
  // <class>
  // <dynamic>
  // <fields>
  private final Page page;
  // </fields>

  // <constructors>
  PageElements(
    Class<TItem> itemClass,
    PdfDirectObject baseObject,
    Page page
    )
  {
    super(itemClass, baseObject);
    this.page = page;
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public boolean add(
    TItem object
    )
  {
    doAdd(object);
    return super.add(object);
  }

  @Override
  public void add(
    int index,
    TItem object
    )
  {
    doAdd(object);
    super.add(index, object);
  }

  @Override
  public PageElements<TItem> clone(
    Document context
    )
  {throw new UnsupportedOperationException();}

  /**
    Gets the page associated to these elements.
  */
  public Page getPage(
    )
  {return page;}

  @Override
  public TItem remove(
    int index
    )
  {
    TItem object = super.remove(index);
    doRemove(object);
    return object;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(
    Object object
    )
  {
    if(!super.remove(object))
      return false;

    doRemove((TItem)object);
    return true;
  }
  // </public>

  // <private>
  private void doAdd(
    TItem object
    )
  {
    // Link the element to its page!
    object.getBaseDataObject().put(PdfName.P, page.getBaseObject());
  }

  private void doRemove(
    TItem object
    )
  {
    // Unlink the element from its page!
    object.getBaseDataObject().remove(PdfName.P);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}