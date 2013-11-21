/*
  Copyright 2010-2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;
import org.pdfclown.files.IndirectObjects;
import org.pdfclown.objects.IVisitor;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfIndirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.MapEntry;

/**
  Object stream containing a sequence of PDF objects [PDF:1.6:3.4.6].
  <p>The purpose of object streams is to allow a greater number of PDF objects
  to be compressed, thereby substantially reducing the size of PDF files.
  The objects in the stream are referred to as <i>compressed objects</i>.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
public final class ObjectStream
  extends PdfStream
  implements Map<Integer,PdfDataObject>
{
  // <class>
  // <classes>
  private final class ObjectEntry
  {
    private PdfDataObject dataObject;
    private int offset;

    public ObjectEntry(
      int offset
      )
    {
      this.dataObject = null;
      this.offset = offset;
    }

    public ObjectEntry(
      PdfDataObject dataObject
      )
    {
      this.dataObject = dataObject;
      this.offset = -1; // Undefined -- to set on stream serialization.
    }

    public PdfDataObject getDataObject(
      )
    {
      if(dataObject == null)
      {
        parser.seek(offset); parser.moveNext();
        dataObject = parser.parsePdfObject();
      }
      return dataObject;
    }
  }
  // </classes>

  // <dynamic>
  // <fields>
  /**
    Compressed objects map.
    <p>This map is initially populated with offset values;
    when a compressed object is required, its offset is used to retrieve it.
  */
  private Map<Integer,ObjectEntry> entries;
  private FileParser parser;
  // </fields>

  // <constructors>
  public ObjectStream(
    )
  {super(new PdfDictionary(new PdfName[]{PdfName.Type}, new PdfDirectObject[]{PdfName.ObjStm}));}

  public ObjectStream(
    PdfDictionary header,
    IBuffer body
    )
  {super(header, body);}
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
    Gets the object stream extended by this one.
    <p>Both streams are considered part of a collection of object streams  whose links form a
    directed acyclic graph.</p>
  */
  public ObjectStream getBaseStream(
    )
  {return (ObjectStream)getHeader().resolve(PdfName.Extends);}

  /**
    @see #getBaseStream()
  */
  public void setBaseStream(
    ObjectStream value
    )
  {getHeader().put(PdfName.Extends, value.getReference());}

  @Override
  public void writeTo(
    IOutputStream stream,
    File context
    )
  {
    if(entries != null)
    {flush(stream);}

    super.writeTo(stream, context);
  }

  // <Map>
  @Override
  public void clear(
    )
  {throw new UnsupportedOperationException();}

  @Override
  public boolean containsKey(
    Object key
    )
  {return getEntries().containsKey(key);}

  @Override
  public boolean containsValue(
    Object value
    )
  {return values().contains(value);}

  @Override
  public Set<Map.Entry<Integer,PdfDataObject>> entrySet(
    )
  {
    Set<Map.Entry<Integer,PdfDataObject>> entrySet = new HashSet<Map.Entry<Integer,PdfDataObject>>();
    for(Integer key : getEntries().keySet())
    {entrySet.add(new MapEntry<Integer,PdfDataObject>(key,get(key)));}
    return entrySet;
  }

  @Override
  public PdfDataObject get(Object key) {
    ObjectEntry entry = getEntries().get(key);
    return (entry != null ? entry.getDataObject() : null);
  }

  @Override
  public boolean isEmpty(
    )
  {return getEntries().isEmpty();}

  @Override
  public Set<Integer> keySet(
    )
  {return getEntries().keySet();}

  /**
    <span style="color:red">For internal use only.</span> If you need to <i>add a data object
    into an object stream</i>, invoke {@link PdfIndirectObject#compress(ObjectStream)} instead.
  */
  @Override
  public PdfDataObject put(
    Integer key,
    PdfDataObject value
    )
  {
    PdfDataObject removedDataObject = null;
    {
      ObjectEntry removedEntry = getEntries().put(key,new ObjectEntry(value));
      if(removedEntry != null)
      {removedDataObject = removedEntry.getDataObject();}
    }
    return removedDataObject;
  }

  @Override
  public void putAll(
    Map<? extends Integer,? extends PdfDataObject> map
    )
  {throw new UnsupportedOperationException();}

  /**
    <span style="color:red">For internal use only.</span> If you need to <i>remove a compressed data
    object from its object stream</i>, invoke {@link PdfIndirectObject#uncompress()} instead.
  */
  @Override
  public PdfDataObject remove(
    Object key
    )
  {
    PdfDataObject removedDataObject = null;
    {
      ObjectEntry removedEntry = getEntries().remove(key);
      if(removedEntry != null)
      {removedDataObject = removedEntry.getDataObject();}
    }
    return removedDataObject;
  }

  @Override
  public int size(
    )
  {return getEntries().size();}

  @Override
  public Collection<PdfDataObject> values(
    )
  {
    List<PdfDataObject> values = new ArrayList<PdfDataObject>();
    for(Integer key : getEntries().keySet())
    {values.add(get(key));}
    return values;
  }
  // </Map>
  // </public>

  // <private>
  /**
    Serializes the object stream entries into the stream body.
  */
  private void flush(
    IOutputStream stream
    )
  {
    // 1. Body.
    int dataByteOffset;
    {
      // Serializing the entries into the stream buffer...
      IBuffer indexBuffer = new Buffer();
      IBuffer dataBuffer = new Buffer();
      IndirectObjects indirectObjects = getFile().getIndirectObjects();
      int objectIndex = -1;
      File context = getFile();
      for(Map.Entry<Integer,ObjectEntry> entry : getEntries().entrySet())
      {
        final int objectNumber = entry.getKey();

        // Update the xref entry!
        XRefEntry xrefEntry = indirectObjects.get(objectNumber).getXrefEntry();
        xrefEntry.setOffset(++objectIndex);

        /*
          NOTE: The entry offset MUST be updated only after its serialization, in order not to
          interfere with its possible data-object retrieval from the old serialization.
        */
        int entryValueOffset = (int)dataBuffer.getLength();

        // Index.
        indexBuffer
          .append(Integer.toString(objectNumber)).append(Chunk.Space) // Object number.
          .append(Integer.toString(entryValueOffset)).append(Chunk.Space); // Byte offset (relative to the first one).

        // Data.
        entry.getValue().getDataObject().writeTo(dataBuffer, context);
        entry.getValue().offset = entryValueOffset;
      }

      // Get the stream buffer!
      final IBuffer body = getBody();

      // Delete the old entries!
      body.setLength(0);

      // Add the new entries!
      body.append(indexBuffer);
      dataByteOffset = (int)body.getLength();
      body.append(dataBuffer);
    }

    // 2. Header.
    {
      final PdfDictionary header = getHeader();
      header.put(
        PdfName.N,
        PdfInteger.get(getEntries().size())
        );
      header.put(
        PdfName.First,
        PdfInteger.get(dataByteOffset)
        );
    }
  }

  private Map<Integer,ObjectEntry> getEntries(
    )
  {
    if(entries == null)
    {
      entries = new HashMap<Integer,ObjectEntry>();

      final IBuffer body = getBody();
      if(body.getLength() > 0)
      {
        parser = new FileParser(body, getFile());
        int baseOffset = ((PdfInteger)getHeader().get(PdfName.First)).getValue();
        for(
          int index = 0,
            length = ((PdfInteger)getHeader().get(PdfName.N)).getValue();
          index < length;
          index++
          )
        {
          int objectNumber = ((PdfInteger)parser.parsePdfObject(1)).getValue();
          int objectOffset = baseOffset + ((PdfInteger)parser.parsePdfObject(1)).getValue();
          entries.put(objectNumber, new ObjectEntry(objectOffset));
        }
      }
    }
    return entries;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
