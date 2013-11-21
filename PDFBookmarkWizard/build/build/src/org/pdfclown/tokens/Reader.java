/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.tokens;

import java.io.Closeable;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.pdfclown.Version;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.parsers.ParseException;
import org.pdfclown.util.parsers.PostScriptParser.TokenTypeEnum;

/**
  PDF file reader.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/01/11
*/
public final class Reader
  implements Closeable
{
  // <class>
  // <classes>
  public static final class FileInfo
  {
    private final PdfDictionary trailer;
    private final Version version;
    private final SortedMap<Integer,XRefEntry> xrefEntries;

    FileInfo(
      Version version,
      PdfDictionary trailer,
      SortedMap<Integer,XRefEntry> xrefEntries
      )
    {
      this.version = version;
      this.trailer = trailer;
      this.xrefEntries = xrefEntries;
    }

    public PdfDictionary getTrailer(
      )
    {return trailer;}

    public Version getVersion(
      )
    {return version;}

    public SortedMap<Integer,XRefEntry> getXrefEntries(
      )
    {return xrefEntries;}
  }
  // </classes>

  // <dynamic>
  // <fields>
  private FileParser parser;
  // </fields>

  // <constructors>
  /**
    <span style="color:red">For internal use only.</span>
  */
  public Reader(
    IInputStream stream,
    File file
    )
  {this.parser = new FileParser(stream, file);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public int hashCode(
    )
  {return parser.hashCode();}

  public FileParser getParser(
    )
  {return parser;}

  /**
    Retrieves the file information.
  */
  public FileInfo readInfo(
    )
  {
//TODO:hybrid xref table/stream
    Version version = Version.get(parser.retrieveVersion());
    PdfDictionary trailer = null;
    SortedMap<Integer,XRefEntry> xrefEntries = new TreeMap<Integer,XRefEntry>();
    {
      long sectionOffset = parser.retrieveXRefOffset();
      while(sectionOffset > -1)
      {
        // Move to the start of the xref section!
        parser.seek(sectionOffset);

        PdfDictionary sectionTrailer;
        if(parser.getToken(1).equals(Keyword.XRef)) // XRef-table section.
        {
          // Looping sequentially across the subsections inside the current xref-table section...
          while(true)
          {
            /*
              NOTE: Each iteration of this block represents the scanning of one subsection.
              We get its bounds (first and last object numbers within its range) and then collect
              its entries.
            */
            // 1. First object number.
            parser.moveNext();
            if((parser.getTokenType() == TokenTypeEnum.Keyword)
                && parser.getToken().equals(Keyword.Trailer)) // XRef-table section ended.
              break;
            else if(parser.getTokenType() != TokenTypeEnum.Integer)
              throw new ParseException("Neither object number of the first object in this xref subsection nor end of xref section found.",parser.getPosition());

            // Get the object number of the first object in this xref-table subsection!
            int startObjectNumber = (Integer)parser.getToken();

            // 2. Last object number.
            parser.moveNext();
            if(parser.getTokenType() != TokenTypeEnum.Integer)
              throw new ParseException("Number of entries in this xref subsection not found.",parser.getPosition());

            // Get the object number of the last object in this xref-table subsection!
            int endObjectNumber = (Integer)parser.getToken() + startObjectNumber;

            // 3. XRef-table subsection entries.
            for(
              int index = startObjectNumber;
              index < endObjectNumber;
              index++
              )
            {
              if(xrefEntries.containsKey(index)) // Already-defined entry.
              {
                // Skip to the next entry!
                parser.moveNext(3);
                continue;
              }

              // Get the indirect object offset!
              int offset = (Integer)parser.getToken(1);
              // Get the object generation number!
              int generation = (Integer)parser.getToken(1);
              // Get the usage tag!
              XRefEntry.UsageEnum usage;
              {
                String usageToken = (String)parser.getToken(1);
                if(usageToken.equals(Keyword.InUseXrefEntry))
                  usage = XRefEntry.UsageEnum.InUse;
                else if(usageToken.equals(Keyword.FreeXrefEntry))
                  usage = XRefEntry.UsageEnum.Free;
                else
                  throw new ParseException("Invalid xref entry.", parser.getPosition());
              }

              // Define entry!
              xrefEntries.put(
                index,
                new XRefEntry(
                  index,
                  generation,
                  offset,
                  usage
                  )
                );
            }
          }

          // Get the previous trailer!
          sectionTrailer = (PdfDictionary)parser.parsePdfObject(1);
        }
        else // XRef-stream section.
        {
          XRefStream stream = (XRefStream)parser.parsePdfObject(3); // Gets the xref stream skipping the indirect-object header.
          // XRef-stream subsection entries.
          for(XRefEntry xrefEntry : stream.values())
          {
            if(xrefEntries.containsKey(xrefEntry.getNumber())) // Already-defined entry.
              continue;

            // Define entry!
            xrefEntries.put(xrefEntry.getNumber(), xrefEntry);
          }

          // Get the previous trailer!
          sectionTrailer = stream.getHeader();
        }

        if(trailer == null)
        {trailer = sectionTrailer;}

        // Get the previous xref-table section's offset!
        PdfInteger prevXRefOffset = (PdfInteger)sectionTrailer.get(PdfName.Prev);
        sectionOffset = (prevXRefOffset != null ? prevXRefOffset.getValue() : -1);
      }
    }
    return new FileInfo(version, trailer, xrefEntries);
  }

  // <Closeable>
  @Override
  public void close(
    ) throws IOException
  {
    if(parser != null)
    {
      parser.close();
      parser = null;
    }
  }
  // </Closeable>
  // </public>

  // <protected>
  @Override
  protected void finalize(
    ) throws Throwable
  {
    try
    {close();}
    finally
    {super.finalize();}
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}