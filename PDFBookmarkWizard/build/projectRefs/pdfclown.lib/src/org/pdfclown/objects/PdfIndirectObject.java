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

package org.pdfclown.objects;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.tokens.Encoding;
import org.pdfclown.tokens.FileParser;
import org.pdfclown.tokens.Keyword;
import org.pdfclown.tokens.ObjectStream;
import org.pdfclown.tokens.Symbol;
import org.pdfclown.tokens.XRefEntry;
import org.pdfclown.tokens.XRefEntry.UsageEnum;

/**
  PDF indirect object [PDF:1.6:3.2.9].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
public class PdfIndirectObject
  extends PdfObject
  implements IPdfIndirectObject
{
  // <class>
  // <static>
  // <fields>
  private static final byte[] BeginIndirectObjectChunk = Encoding.Pdf.encode(Symbol.Space + Keyword.BeginIndirectObject + Symbol.LineFeed);
  private static final byte[] EndIndirectObjectChunk = Encoding.Pdf.encode(Symbol.LineFeed + Keyword.EndIndirectObject + Symbol.LineFeed);
  // </fields>
  // </static>

  // <dynamic>
  // <fields>
  private PdfDataObject dataObject;
  private File file;
  private boolean original;
  private final PdfReference reference;
  private final XRefEntry xrefEntry;

  private boolean updated;
  private boolean updateable = true;
  private boolean virtual;
  // </fields>

  // <constructors>
  /**
    <span style="color:red">For internal use only.</span>

    @param file Associated file.
    @param dataObject Data object associated to the indirect object. It MUST be
      <ul>
        <li><code>null</code>, if the indirect object is original or free;</li>
        <li>NOT <code>null</code>, if the indirect object is new and in-use.</li>
      </ul>
    @param xrefEntry Cross-reference entry associated to the indirect object. If the indirect object
      is new, its offset field MUST be set to 0.
  */
  public PdfIndirectObject(
    File file,
    PdfDataObject dataObject,
    XRefEntry xrefEntry
    )
  {
    this.file = file;
    this.dataObject = include(dataObject);
    this.xrefEntry = xrefEntry;

    this.original = (xrefEntry.getOffset() >= 0);
    this.reference = new PdfReference(this);
  }
  // </constructors>

  // <interface>
  // <public>
  @Override
  public PdfObject accept(
    IVisitor visitor,
    Object data
    )
  {return visitor.visit(this, data);}

  /**
    Adds the {@link #getDataObject() data object} to the specified object stream [PDF:1.6:3.4.6].

    @param objectStream Target object stream.
   */
  public void compress(
    ObjectStream objectStream
    )
  {
    // Remove from previous object stream!
    uncompress();

    if(objectStream != null)
    {
      // Add to the object stream!
      objectStream.put(xrefEntry.getNumber(),getDataObject());
      // Update its xref entry!
      xrefEntry.setUsage(UsageEnum.InUseCompressed);
      xrefEntry.setStreamNumber(objectStream.getReference().getObjectNumber());
      xrefEntry.setOffset(XRefEntry.UndefinedOffset); // Internal object index unknown (to set on object stream serialization -- see ObjectStream).
    }
  }

  @Override
  public PdfIndirectObject getContainer(
    )
  {return this;}

  @Override
  public File getFile(
    )
  {return file;}

  @Override
  public PdfObject getParent(
    )
  {return null;} // NOTE: As indirect objects are root objects, no parent can be associated.

  public XRefEntry getXrefEntry(
    )
  {return xrefEntry;}

  @Override
  public int hashCode(
    )
  {
    /*
      NOTE: Uniqueness should be achieved XORring the (local) reference hashcode with the (global)
      file hashcode.
      NOTE: Do NOT directly invoke reference.hashCode() method here as, conversely relying on this
      method, it would trigger an infinite loop.
    */
    return reference.getId().hashCode() ^ file.hashCode();
  }

  /**
    Gets whether this object is compressed within an object stream [PDF:1.6:3.4.6].
  */
  public boolean isCompressed(
    )
  {return xrefEntry.getUsage() == UsageEnum.InUseCompressed;}

  /**
    Gets whether this object contains a data object.
  */
  public boolean isInUse(
    )
  {return xrefEntry.getUsage() != UsageEnum.Free;}

  /**
    Gets whether this object comes intact from an existing file.
  */
  public boolean isOriginal(
    )
  {return original;}

  @Override
  public boolean isUpdateable(
    )
  {return updateable;}

  @Override
  public void setUpdateable(
    boolean value
    )
  {updateable = value;}

  @Override
  public String toString(
    )
  {
    StringBuilder buffer = new StringBuilder();
    {
      // Header.
      buffer.append(reference.getId()).append(" obj").append(Symbol.LineFeed);
      // Body.
      buffer.append(getDataObject());
    }
    return buffer.toString();
  }

  @Override
  public PdfIndirectObject swap(
    PdfObject other
    )
  {
    PdfIndirectObject otherObject = (PdfIndirectObject)other;
    PdfDataObject otherDataObject = otherObject.dataObject;
    // Update the other!
    otherObject.setDataObject(dataObject);
    // Update this one!
    this.setDataObject(otherDataObject);
    return this;
  }

  /**
    Removes the {@link #getDataObject() data object} from its object stream [PDF:1.6:3.4.6].
  */
  public void uncompress(
    )
  {
    if(!isCompressed())
      return;

    // Remove from its object stream!
    ObjectStream oldObjectStream = (ObjectStream)file.getIndirectObjects().get(xrefEntry.getStreamNumber()).getDataObject();
    oldObjectStream.remove(xrefEntry.getNumber());
    // Update its xref entry!
    xrefEntry.setUsage(UsageEnum.InUse);
    xrefEntry.setStreamNumber(-1); // No object stream.
    xrefEntry.setOffset(XRefEntry.UndefinedOffset); // Offset unknown (to set on file serialization -- see CompressedWriter).
  }

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {
    // Header.
    stream.write(reference.getId()); stream.write(BeginIndirectObjectChunk);
    // Body.
    getDataObject().writeTo(stream, context);
    // Tail.
    stream.write(EndIndirectObjectChunk);
  }

  // <IPdfIndirectObject>
  @Override
  public PdfIndirectObject clone(
    File context
    )
  {return (PdfIndirectObject)super.clone(context);}

  @Override
  public void delete(
    )
  {
    if(file == null)
      return;

    /*
      NOTE: It's expected that dropFile() is invoked by IndirectObjects.remove() method;
      such an action is delegated because clients may invoke directly remove() method,
      skipping this method.
    */
    file.getIndirectObjects().remove(xrefEntry.getNumber());
  }

  @Override
  public PdfDataObject getDataObject(
    )
  {
    if(dataObject == null)
    {
      switch (xrefEntry.getUsage())
      {
        case Free: // Free entry (no data object at all).
          break;
        case InUse: // In-use entry (late-bound data object).
        {
          FileParser parser = file.getReader().getParser();
          // Retrieve the associated data object among the original objects!
          parser.seek(xrefEntry.getOffset());
          // Get the indirect data object!
          dataObject = include(parser.parsePdfObject(4)); // NOTE: Skips the indirect-object header.
          break;
        }
        case InUseCompressed:
        {
          // Get the object stream where its data object is stored!
          ObjectStream objectStream = (ObjectStream)file.getIndirectObjects().get(xrefEntry.getStreamNumber()).getDataObject();
          // Get the indirect data object!
          dataObject = include(objectStream.get(xrefEntry.getNumber()));
          break;
        }
      }
    }
    return dataObject;
  }

  @Override
  public PdfIndirectObject getIndirectObject(
    )
  {return this;}

  @Override
  public PdfReference getReference(
    )
  {return reference;}

  @Override
  public boolean isUpdated(
    )
  {return updated;}

  @Override
  public void setDataObject(
    PdfDataObject value
    )
  {
    if(xrefEntry.getGeneration() == XRefEntry.GenerationUnreusable)
      throw new RuntimeException("Unreusable entry.");

    exclude(dataObject);
    dataObject = include(value);
    xrefEntry.setUsage(UsageEnum.InUse);
    update();
  }
  // </IPdfIndirectObject>
  // </public>

  // <protected>
  @Override
  protected boolean isVirtual(
    )
  {return virtual;}

  @Override
  protected void setUpdated(
    boolean value
    )
  {
    if(value && original)
    {
      /*
        NOTE: It's expected that dropOriginal() is invoked by IndirectObjects set() method;
        such an action is delegated because clients may invoke directly set() method, skipping
        this method.
      */
      file.getIndirectObjects().update(this);
    }
    updated = value;
  }

  @Override
  protected void setVirtual(
    boolean value
    )
  {
    if(virtual && !value)
    {
      /*
        NOTE: When a virtual indirect object becomes concrete it must be registered.
      */
      file.getIndirectObjects().addVirtual(this);
      virtual = false;
      getReference().update();
    }
    else
    {virtual = value;}
    dataObject.setVirtual(virtual);
  }
  // </protected>

  // <internal>
  /**
    <span style="color:red">For internal use only.</span>
  */
  public void dropFile(
    )
  {
    uncompress();
    file = null;
  }

  /**
    <span style="color:red">For internal use only.</span>
  */
  public void dropOriginal(
    )
  {original = false;}

  @Override
  void setParent(
    PdfObject value
    )
  {/* NOOP: As indirect objects are root objects, no parent can be associated. */}
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}