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

package org.pdfclown.tools;

import java.util.HashSet;
import java.util.Set;

import org.pdfclown.files.File;
import org.pdfclown.files.IndirectObjects;
import org.pdfclown.objects.IVisitor;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.Visitor;

/**
  Tool to enhance PDF files.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
public final class Optimizer
{
  /**
    Removes indirect objects which have no reference in the document structure.

    @param file File to optimize.
  */
  public static void removeOrphanedObjects(
    File file
    )
  {
    // 1. Collecting alive indirect objects...
    final Set<Integer> aliveObjectNumbers = new HashSet<Integer>();
    {
      // Alive indirect objects collector.
      IVisitor visitor = new Visitor()
      {
        @Override
        public PdfObject visit(
          PdfReference object,
          Object data
          )
        {
          Integer objectNumber = object.getReference().getObjectNumber();
          if(aliveObjectNumbers.contains(objectNumber))
            return object;

          aliveObjectNumbers.add(objectNumber);
          return super.visit(object, data);
        }
      };
      // Walk through the document structure to collect alive indirect objects!
      file.getTrailer().accept(visitor, null);
    }

    // 2. Removing dead indirect objects...
    IndirectObjects indirectObjects = file.getIndirectObjects();
    for(int objectNumber = 0, objectCount = indirectObjects.size(); objectNumber < objectCount; objectNumber++)
    {
      if(!aliveObjectNumbers.contains(objectNumber))
      {indirectObjects.remove(objectNumber);}
    }
  }
}
