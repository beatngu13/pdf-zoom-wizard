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

package org.pdfclown.objects;

import org.pdfclown.tokens.ObjectStream;
import org.pdfclown.tokens.XRefStream;

/**
  Visitor object.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
public class Visitor
  implements IVisitor
{
  @Override
  public PdfObject visit(
    ObjectStream object,
    Object data
    )
  {
    for(PdfDataObject value : object.values())
    {value.accept(this, data);}
    return object;
  }

  @Override
  public PdfObject visit(
    PdfArray object,
    Object data
    )
  {
    for(PdfDirectObject item : object)
    {
      if(item != null)
      {item.accept(this, data);}
    }
    return object;
  }

  @Override
  public PdfObject visit(
    PdfBoolean object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfDataObject object,
    Object data
    )
  {return object.accept(this, data);}

  @Override
  public PdfObject visit(
    PdfDate object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfDictionary object,
    Object data
    )
  {
    for(PdfDirectObject value : object.values())
    {
      if(value != null)
      {value.accept(this, data);}
    }
    return object;
  }

  @Override
  public PdfObject visit(
    PdfIndirectObject object,
    Object data
    )
  {
    PdfDataObject dataObject = object.getDataObject();
    if(dataObject != null)
    {dataObject.accept(this, data);}
    return object;
  }

  @Override
  public PdfObject visit(
    PdfInteger object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfName object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfReal object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfReference object,
    Object data
    )
  {
    object.getIndirectObject().accept(this, data);
    return object;
  }

  @Override
  public PdfObject visit(
    PdfStream object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfString object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    PdfTextString object,
    Object data
    )
  {return object;}

  @Override
  public PdfObject visit(
    XRefStream object,
    Object data
    )
  {return object;}
}
