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

package org.pdfclown.tokens;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.files.FileIdentifier;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfName;

/**
  PDF file writer.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 02/04/12
*/
public abstract class Writer
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BOFChunk = Encoding.Pdf.encode(Keyword.BOF);
  private static final byte[] EOFChunk = Encoding.Pdf.encode(Symbol.LineFeed + Keyword.EOF + Symbol.CarriageReturn + Symbol.LineFeed);
  private static final byte[] HeaderBinaryHintChunk = new byte[]{(byte)Symbol.LineFeed,(byte)Symbol.Percent,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x80,(byte)Symbol.LineFeed}; // NOTE: Arbitrary binary characters (code >= 128) for ensuring proper behavior of file transfer applications [PDF:1.6:3.4.1].
  private static final byte[] StartXRefChunk = Encoding.Pdf.encode(Keyword.StartXRef + Symbol.LineFeed);
  // </fields>

  // <interface>
  // <public>
  /**
    Gets a new writer instance for the specified file.

    @param file File to serialize.
    @param stream Target stream.
  */
  public static Writer get(
    File file,
    IOutputStream stream
    )
  {
    // Which cross-reference table mode?
    switch(file.getDocument().getConfiguration().getXrefMode())
    {
      case Plain:
        return new PlainWriter(file, stream);
      case Compressed:
        return new CompressedWriter(file, stream);
      default:
        throw new UnsupportedOperationException();
    }
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  protected final File file;
  protected final IOutputStream stream;
  // </fields>

  // <constructors>
  protected Writer(
    File file,
    IOutputStream stream
    )
  {
    this.file = file;
    this.stream = stream;
  }
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the file to serialize.
  */
  public File getFile(
    )
  {return file;}

  /**
    Gets the target stream.
  */
  public IOutputStream getStream(
    )
  {return stream;}

  /**
    Serializes the {@link #getFile() file} to the {@link #getStream() target stream}.

    @param mode Serialization mode.
   */
  public void write(
    SerializationModeEnum mode
    )
  {
    switch(mode)
    {
      case Incremental:
        if(file.getReader() != null)
        {
          writeIncremental();
          break;
        }
        // If the file is new (no reader), fall through to Standard!
      case Standard:
        writeStandard();
        break;
      case Linearized:
        writeLinearized();
        break;
    }
  }
  // </public>

  // <protected>
  /**
    Updates the specified trailer.
    NOTE: this method has to be called just before serializing the trailer object.
  */
  protected void updateTrailer(
    PdfDictionary trailer,
    IOutputStream stream
    )
  {
    // File identifier update.
    FileIdentifier identifier = FileIdentifier.wrap(trailer.get(PdfName.ID));
    if(identifier == null)
    {trailer.put(PdfName.ID, (identifier = new FileIdentifier()).getBaseObject());}
    identifier.update(this);
  }

  /**
    Serializes the beginning of the file [PDF:1.6:3.4.1].
  */
  protected final void writeHeader(
    )
  {
    stream.write(BOFChunk);
    stream.write(file.getDocument().getVersion().toString()); // NOTE: Document version represents the actual (possibly-overridden) file version.
    stream.write(HeaderBinaryHintChunk);
  }

  /**
    Serializes the PDF file as incremental update [PDF:1.6:3.4.5].
  */
  protected abstract void writeIncremental(
    );

  /**
    Serializes the PDF file linearized [PDF:1.6:F].
  */
  protected abstract void writeLinearized(
    );

  /**
    Serializes the PDF file compactly [PDF:1.6:3.4].
  */
  protected abstract void writeStandard(
    );

  /**
    Serializes the end of the file [PDF:1.6:3.4.4].

    @param startxref Byte offset from the beginning of the file to the beginning
      of the last cross-reference section.
  */
  protected final void writeTail(
    long startxref
    )
  {
    stream.write(StartXRefChunk);
    stream.write(Long.toString(startxref));
    stream.write(EOFChunk);
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}