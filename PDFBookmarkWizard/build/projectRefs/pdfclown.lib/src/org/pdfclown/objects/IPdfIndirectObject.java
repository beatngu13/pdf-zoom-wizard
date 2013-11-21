/*
  Copyright 2006-2010 Stefano Chizzolini. http://www.pdfclown.org

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

import org.pdfclown.files.File;

/**
  PDF indirect object interface.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.0
*/
public interface IPdfIndirectObject
{
  // <interface>
  Object clone(
    File context
    );

  /**
    Removes the object from its file context.
    <h3>Remarks</h3>
    <p>The object is no more usable after this method returns.</p>
  */
  void delete(
    );

  /**
    Gets the actual data associated to the indirect reference.
  */
  PdfDataObject getDataObject(
    );

  /**
    Gets the indirect object associated to the indirect reference.
  */
  PdfIndirectObject getIndirectObject(
    );

  /**
    Gets the indirect reference associated to the indirect object.
  */
  PdfReference getReference(
    );

  /**
    @see #getDataObject()
    @since 0.0.7
  */
  void setDataObject(
    PdfDataObject value
    );
  // </interface>
}