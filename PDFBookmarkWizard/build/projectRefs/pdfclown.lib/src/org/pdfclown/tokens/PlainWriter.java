/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Stefano Chizzolini (original code developer, http://www.stefanochizzolini.it)
    * Haakan Aakerberg (bugfix contributor):
      - [FIX:0.0.4:5]

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

package org.pdfclown.tokens;

import java.text.DecimalFormat;
import java.util.Map;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.files.IndirectObjects;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.util.NotImplementedException;

/**
  PDF file writer implementing classic cross-reference table [PDF:1.6:3.4.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 08/23/12
*/
final class PlainWriter
  extends Writer
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] TrailerChunk = Encoding.Pdf.encode(Keyword.Trailer + Symbol.LineFeed);
  private static final String XRefChunk = Keyword.XRef + Symbol.LineFeed;
  private static final String XRefEOLChunk = "" + Symbol.CarriageReturn + Symbol.LineFeed;

  private static final DecimalFormat XRefGenerationFormatter = new DecimalFormat("00000");
  private static final DecimalFormat XRefOffsetFormatter = new DecimalFormat("0000000000");
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  PlainWriter(
    File file,
    IOutputStream stream
    )
  {super(file, stream);}
  // </constructors>

  // <interface>
  // <protected>
  @Override
  protected void writeIncremental(
    )
  {
    // 1. Original content (head, body and previous trailer).
    FileParser parser = file.getReader().getParser();
    stream.write(parser.getStream());

    // 2. Body update (modified indirect objects insertion).
    int xrefSize = file.getIndirectObjects().size();
    StringBuilder xrefBuilder = new StringBuilder(XRefChunk);
    {
      /*
        NOTE: Incremental xref table comprises multiple sections
        each one composed by multiple subsections; this update
        adds a new section.
      */
      StringBuilder xrefSubBuilder = new StringBuilder(); // Xref-table subsection builder.
      int xrefSubCount = 0; // Xref-table subsection counter.
      int prevKey = 0; // Previous-entry object number.
      for(
        Map.Entry<Integer,PdfIndirectObject> indirectObjectEntry
          : file.getIndirectObjects().getModifiedObjects().entrySet()
        )
      {
        // Is the object in the current subsection?
        /*
          NOTE: To belong to the current subsection, the object entry MUST be contiguous with the
          previous (condition 1) or the iteration has to have been just started (condition 2).
        */
        if(indirectObjectEntry.getKey() - prevKey == 1
          || prevKey == 0) // Current subsection continues.
        {xrefSubCount++;}
        else // Current subsection terminates.
        {
          // End current subsection!
          appendXRefSubsection(
            xrefBuilder,
            prevKey - xrefSubCount + 1,
            xrefSubCount,
            xrefSubBuilder
            );

          // Begin next subsection!
          xrefSubBuilder.setLength(0);
          xrefSubCount = 1;
        }

        prevKey = indirectObjectEntry.getKey();

        // Current entry insertion.
        if(indirectObjectEntry.getValue().isInUse()) // In-use entry.
        {
          // Add in-use entry!
          appendXRefEntry(
            xrefSubBuilder,
            indirectObjectEntry.getValue().getReference(),
            stream.getLength()
            );
          // Add in-use entry content!
          indirectObjectEntry.getValue().writeTo(stream, file);
        }
        else // Free entry.
        {
          // Add free entry!
          /*
            NOTE: We purposely neglect the linked list of free entries (see IndirectObjects.remove(int)),
            so that this entry links directly back to object number 0, having a generation number of 65535
            (not reusable) [PDF:1.6:3.4.3].
          */
          appendXRefEntry(
            xrefSubBuilder,
            indirectObjectEntry.getValue().getReference(),
            0
            );
        }
      }
      // End last subsection!
      appendXRefSubsection(
        xrefBuilder,
        prevKey - xrefSubCount + 1,
        xrefSubCount,
        xrefSubBuilder
        );
    }

    // 3. XRef-table last section.
    long startxref = stream.getLength();
    stream.write(xrefBuilder.toString());

    // 4. Trailer.
    writeTrailer(startxref, xrefSize, parser);
  }

  @Override
  protected void writeLinearized(
    )
  {throw new NotImplementedException();}

  @Override
  protected void writeStandard(
    )
  {
    // 1. Header [PDF:1.6:3.4.1].
    writeHeader();

    // 2. Body [PDF:1.6:3.4.2].
    int xrefSize = file.getIndirectObjects().size();
    StringBuilder xrefBuilder = new StringBuilder(XRefChunk);
    {
      /*
        NOTE: A standard xref table comprises just one section composed by just one subsection.
        NOTE: As xref-table free entries MUST be arrayed as a linked list,
        it's needed to cache intermingled in-use entries in order to properly render
        the object number of the next free entry inside the previous one.
      */
      appendXRefSubsectionIndexer(xrefBuilder, 0, xrefSize);

      StringBuilder xrefInUseBlockBuilder = new StringBuilder();
      IndirectObjects indirectObjects = file.getIndirectObjects();
      PdfReference freeReference = indirectObjects.get(0).getReference(); // Initialized to the first free entry.
      for(
        int index = 1;
        index < xrefSize;
        index++
        )
      {
        // Current entry insertion.
        PdfIndirectObject indirectObject = indirectObjects.get(index);
        if(indirectObject.isInUse()) // In-use entry.
        {
          // Add in-use entry!
          appendXRefEntry(
            xrefInUseBlockBuilder,
            indirectObject.getReference(),
            stream.getLength()
            );
          // Add in-use entry content!
          indirectObject.writeTo(stream, file);
        }
        else // Free entry.
        {
          // Add free entry!
          appendXRefEntry(
            xrefBuilder,
            freeReference,
            index
            );

          // End current block!
          xrefBuilder.append(xrefInUseBlockBuilder);

          // Initialize next block!
          xrefInUseBlockBuilder.setLength(0);
          freeReference = indirectObject.getReference();
        }
      }
      // Add last free entry!
      appendXRefEntry(
        xrefBuilder,
        freeReference,
        0
        );

      // End last block!
      xrefBuilder.append(xrefInUseBlockBuilder);
    }

    // 3. XRef table (unique section) [PDF:1.6:3.4.3].
    long startxref = stream.getLength();
    stream.write(xrefBuilder.toString());

    // 4. Trailer [PDF:1.6:3.4.4].
    writeTrailer(startxref, xrefSize, null);
  }
  // </protected>

  // <private>
  private StringBuilder appendXRefEntry(
    StringBuilder xrefBuilder,
    PdfReference reference,
    long offset
    )
  {
    String usage;
    switch(reference.getIndirectObject().getXrefEntry().getUsage())
    {
      case Free:
        usage = Keyword.FreeXrefEntry;
        break;
      case InUse:
        usage = Keyword.InUseXrefEntry;
        break;
      default: // Should NEVER happen.
        throw new UnsupportedOperationException();
    }
    return xrefBuilder.append(XRefOffsetFormatter.format(offset)).append(Symbol.Space)
      .append(XRefGenerationFormatter.format(reference.getGenerationNumber())).append(Symbol.Space)
      .append(usage).append(XRefEOLChunk);
  }

  /**
    Appends the cross-reference subsection to the specified builder.

    @param xrefBuilder Target builder.
    @param firstObjectNumber Object number of the first object in the subsection.
    @param entryCount Number of entries in the subsection.
    @param xrefSubBuilder Cross-reference subsection entries.
  */
  private StringBuilder appendXRefSubsection(
    StringBuilder xrefBuilder,
    int firstObjectNumber,
    int entryCount,
    StringBuilder xrefSubBuilder
    )
  {return appendXRefSubsectionIndexer(xrefBuilder, firstObjectNumber, entryCount).append(xrefSubBuilder);}

  /**
    Appends the cross-reference subsection indexer to the specified builder.

    @param xrefBuilder Target builder.
    @param firstObjectNumber Object number of the first object in the subsection.
    @param entryCount Number of entries in the subsection.
  */
  private StringBuilder appendXRefSubsectionIndexer(
    StringBuilder xrefBuilder,
    int firstObjectNumber,
    int entryCount
    )
  {return xrefBuilder.append(firstObjectNumber).append(Symbol.Space).append(entryCount).append(Symbol.LineFeed);}

  /**
    Serializes the file trailer [PDF:1.6:3.4.4].

    @param startxref Byte offset from the beginning of the file to the beginning
      of the last cross-reference section.
    @param xrefSize Total number of entries in the file's cross-reference table,
      as defined by the combination of the original section and all update sections.
    @param parser File parser.
  */
  private void writeTrailer(
    long startxref,
    int xrefSize,
    FileParser parser
    )
  {
    // 1. Header.
    stream.write(TrailerChunk);

    // 2. Body.
    // Update its entries:
    PdfDictionary trailer = file.getTrailer();
    updateTrailer(trailer, stream);
    // * Size
    trailer.put(PdfName.Size, PdfInteger.get(xrefSize));
    // * Prev
    if(parser == null)
    {trailer.remove(PdfName.Prev);} // [FIX:0.0.4:5] It (wrongly) kept the 'Prev' entry of multiple-section xref tables.
    else
    {trailer.put(PdfName.Prev, PdfInteger.get((int)parser.retrieveXRefOffset()));}
    // Serialize its contents!
    trailer.writeTo(stream, file); stream.write(Chunk.LineFeed);

    // 3. Tail.
    writeTail(startxref);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}