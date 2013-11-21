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
  Visitor interface.
  Implementations are expected to be functional (traversal results are propagated through return
  values rather than side effects) and external (responsibility for traversing the hierarchical
  structure is assigned to the 'visit' methods rather than the 'accept' counterparts).

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
public interface IVisitor
{
  /**
    Visits an object stream.
  
    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    ObjectStream object,
    Object data
    );
  
  /**
    Visits an object array.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfArray object,
    Object data
    );

  /**
    Visits a boolean object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfBoolean object,
    Object data
    );

  /**
    Visits a data object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfDataObject object,
    Object data
    );

  /**
    Visits a date object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfDate object,
    Object data
    );

  /**
    Visits an object dictionary.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfDictionary object,
    Object data
    );

  /**
    Visits an indirect object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfIndirectObject object,
    Object data
    );

  /**
    Visits an integer-number object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfInteger object,
    Object data
    );

  /**
    Visits a name object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfName object,
    Object data
    );

  /**
    Visits a real-number object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfReal object,
    Object data
    );

  /**
    Visits a reference object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfReference object,
    Object data
    );

  /**
    Visits a stream object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfStream object,
    Object data
    );

  /**
    Visits a string object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfString object,
    Object data
    );

  /**
    Visits a text string object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    PdfTextString object,
    Object data
    );

  /**
    Visits a cross-reference stream object.

    @param object Visited object.
    @param data Supplemental data.
    @return Result object.
  */
  PdfObject visit(
    XRefStream object,
    Object data
    );
}
