/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.fonts;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IInputStream;
import org.pdfclown.tokens.CharsetName;
import org.pdfclown.util.NotImplementedException;

/**
  CFF file format parser [CFF:1.0].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 02/04/12
*/
final class CffParser
{
  // <class>
  // <classes>
  /**
    Dictionary [CFF:1.0:4].
  */
  private static final class Dict
    implements Map<Integer,List<Number>>
  {
    public enum OperatorEnum
    {
      Charset("charset", 15),
      CharStrings(17),
      CharstringType(6 + OperatorValueEscape),
      Encoding(16);

      private final String name;
      private final int value;

      private OperatorEnum(
        int value
        )
      {this(null, value);}

      private OperatorEnum(
        String name,
        int value
        )
      {
        this.name = name;
        this.value = value;
      }

      @SuppressWarnings("unused")
      public String getName(
        )
      {return name != null ? name : name();}

      public int getValue(
        )
      {return value;}
    }

    private static final int OperatorValueEscape = 12 << 8;

    public static Dict parse(
      byte[] data
      ) throws EOFException
    {return parse(new Buffer(data));}

    public static Dict parse(
      IInputStream stream
      ) throws EOFException
    {
      Map<Integer,List<Number>> entries = new HashMap<Integer,List<Number>>();
      List<Number> operands = null;
      while(true)
      {
        int b0;
        try
        {b0 = stream.readUnsignedByte();}
        catch(EOFException e)
        {break;}
        if(b0 >= 0 && b0 <= 21) // Operator.
        {
          int operator = b0;
          if(b0 == 12) // 2-byte operator.
          {operator = operator << 8 + stream.readUnsignedByte();}

          /*
            NOTE: In order to resiliently support unknown operators on parsing, parsed operators
            are not directly mapped to OperatorEnum.
          */
          entries.put(operator, operands);
          operands = null;
        }
        else // Operand.
        {
          if(operands == null)
          {operands = new ArrayList<Number>();}

          if(b0 == 28) // 3-byte integer.
          {operands.add(stream.readUnsignedByte() << 8 + stream.readUnsignedByte());}
          else if(b0 == 29) // 5-byte integer.
          {operands.add(stream.readUnsignedByte() << 24 + stream.readUnsignedByte() << 16 + stream.readUnsignedByte() << 8 + stream.readUnsignedByte());}
          else if(b0 == 30) // Variable-length real.
          {
            StringBuilder operandBuilder = new StringBuilder();
            boolean ended = false;
            do
            {
              int b = stream.readUnsignedByte();
              int[] nibbles = {(b >> 4) & 0xf, b & 0xf};
              for(int nibble : nibbles)
              {
                switch (nibble)
                {
                  case 0x0:
                  case 0x1:
                  case 0x2:
                  case 0x3:
                  case 0x4:
                  case 0x5:
                  case 0x6:
                  case 0x7:
                  case 0x8:
                  case 0x9:
                    operandBuilder.append(nibble);
                    break;
                  case 0xa: // Decimal point.
                    operandBuilder.append(".");
                    break;
                  case 0xb: // Positive exponent.
                    operandBuilder.append("E");
                    break;
                  case 0xc: // Negative exponent.
                    operandBuilder.append("E-");
                    break;
                  case 0xd: // Reserved.
                    break;
                  case 0xe: // Minus.
                    operandBuilder.append("-");
                    break;
                  case 0xf: // End of number.
                    ended = true;
                    break;
                }
              }
            } while(!ended);
            operands.add(Double.valueOf(operandBuilder.toString()));
          }
          else if (b0 >= 32 && b0 <= 246) // 1-byte integer.
          {operands.add(b0 - 139);}
          else if (b0 >= 247 && b0 <= 250) // 2-byte positive integer.
          {operands.add((b0 - 247) << 8 + stream.readUnsignedByte() + 108);}
          else if (b0 >= 251 && b0 <= 254) // 2-byte negative integer.
          {operands.add(-(b0 - 251) << 8 - stream.readUnsignedByte() - 108);}
          else // Reserved.
          { /* NOOP */ }
        }
      }
      return new Dict(entries);
    }

    private final Map<Integer,List<Number>> entries;

    private Dict(
      Map<Integer,List<Number>> entries
      )
    {this.entries = entries;}

    @Override
    public void clear(
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean containsKey(
      Object key
      )
    {return entries.containsKey(key);}

    @Override
    public boolean containsValue(
      Object value
      )
    {return entries.containsValue(value);}

    @Override
    public Set<java.util.Map.Entry<Integer,List<Number>>> entrySet(
      )
    {return entries.entrySet();}

    @Override
    public List<Number> get(
      Object key
      )
    {return entries.get(key);}

    @Override
    public boolean isEmpty(
      )
    {return entries.isEmpty();}

    @Override
    public Set<Integer> keySet(
      )
    {return entries.keySet();}

    @Override
    public List<Number> put(
      Integer key,
      List<Number> value
      )
    {throw new UnsupportedOperationException();}

    @Override
    public void putAll(
      Map<? extends Integer,? extends List<Number>> m
      )
    {throw new UnsupportedOperationException();}

    @Override
    public List<Number> remove(
      Object key
      )
    {throw new UnsupportedOperationException();}

    @Override
    public int size(
      )
    {return entries.size();}

    @Override
    public Collection<List<Number>> values(
      )
    {return entries.values();}

    public Number get(
      OperatorEnum operator,
      int operandIndex
      )
    {return get(operator, operandIndex, null);}

    public Number get(
      OperatorEnum operator,
      int operandIndex,
      Integer defaultValue
      )
    {
      List<Number> operands = get(operator.getValue());
      return operands != null ? operands.get(operandIndex) : defaultValue;
    }
  }

  /**
    Array of variable-sized objects [CFF:1.0:5].
  */
  private static final class Index
    implements List<byte[]>
  {
    @SuppressWarnings("unused")
    public static Index parse(
      byte[] data
      ) throws EOFException
    {return parse(new Buffer(data));}

    public static Index parse(
      IInputStream stream
      ) throws EOFException
    {
      byte[][] data = new byte[stream.readUnsignedShort()][];
      {
        int[] offsets = new int[data.length + 1];
        int offSize = stream.readUnsignedByte();
        for (int index = 0, count = offsets.length; index < count; index++)
        {offsets[index] = stream.readInt(offSize);}
        for (int index = 0, count = data.length; index < count; index++)
        {stream.read(data[index] = new byte[offsets[index + 1] - offsets[index]]);}
      }
      return new Index(data);
    }

    public static Index parse(
      IInputStream stream,
      int offset
      ) throws EOFException
    {
      stream.setPosition(offset);
      return parse(stream);
    }

    private final byte[][] data;

    private Index(
      byte[][] data
      )
    {this.data = data;}

    @Override
    public boolean add(
      byte[] item
      )
    {throw new UnsupportedOperationException();}

    @Override
    public void add(
      int index,
      byte[] item
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean addAll(
      Collection<? extends byte[]> items
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean addAll(
      int index,
      Collection<? extends byte[]> items
      )
    {throw new UnsupportedOperationException();}

    @Override
    public void clear(
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean contains(
      Object item
      )
    {throw new NotImplementedException();}

    @Override
    public boolean containsAll(
      Collection<?> items
      )
    {throw new NotImplementedException();}

    @Override
    public byte[] get(
      int index
      )
    {return data[index];}

    @Override
    public int indexOf(
      Object item
      )
    {throw new NotImplementedException();}

    @Override
    public boolean isEmpty(
      )
    {return size() == 0;}

    @Override
    public Iterator<byte[]> iterator(
      )
    {
      return new Iterator<byte[]>(
        )
      {
        int index = -1;

        @Override
        public boolean hasNext(
          )
        {return index + 1 < size();}

        @Override
        public byte[] next(
          )
        {return data[++index];}

        @Override
        public void remove(
          )
        {throw new UnsupportedOperationException();}
      };
    }

    @Override
    public int lastIndexOf(
      Object item
      )
    {throw new NotImplementedException();}

    @Override
    public ListIterator<byte[]> listIterator(
      )
    {throw new NotImplementedException();}

    @Override
    public ListIterator<byte[]> listIterator(
      int index
      )
    {throw new NotImplementedException();}

    @Override
    public boolean remove(
      Object item
      )
    {throw new UnsupportedOperationException();}

    @Override
    public byte[] remove(
      int index
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean removeAll(
      Collection<?> items
      )
    {throw new UnsupportedOperationException();}

    @Override
    public boolean retainAll(
      Collection<?> items
      )
    {throw new UnsupportedOperationException();}

    @Override
    public byte[] set(
      int index,
      byte[] item
      )
    {throw new UnsupportedOperationException();}

    @Override
    public int size(
      )
    {return data.length;}

    @Override
    public List<byte[]> subList(
      int fromIndex,
      int toIndex
      )
    {throw new NotImplementedException();}

    @Override
    public Object[] toArray(
      )
    {throw new NotImplementedException();}

    @Override
    public <T> T[] toArray(
      T[] array
      )
    {throw new NotImplementedException();}
  }

  /**
    Predefined charsets [CFF:1.0:12,C].
  */
  private enum StandardCharsetEnum
  {
    ISOAdobe(0),
    Expert(1),
    ExpertSubset(2);

    /**
      Gets the charset corresponding to the given value.
    */
    public static StandardCharsetEnum get(
      Integer value
      )
    {
      if(value == null)
        return StandardCharsetEnum.ISOAdobe;

      for(StandardCharsetEnum charset : StandardCharsetEnum.values())
      {
        if(value.equals(charset.getId()))
          return charset;
      }
      return null;
    }

    final int id;
    final Map<Integer,Integer> map;

    private StandardCharsetEnum(
      int id
      )
    {
      this.id = id;

      map = new HashMap<Integer,Integer>();
      {
        BufferedReader stream = null;
        try
        {
          // Open the resource!
          stream = new BufferedReader(
            new InputStreamReader(
              CffParser.class.getResourceAsStream("/fonts/cff/" + name() + "Charset")
              )
            );
          // Parsing the resource...
          String line;
          while((line = stream.readLine()) != null)
          {
            String[] lineItems = line.split(",");
            map.put(Integer.parseInt(lineItems[0]), GlyphMapping.nameToCode(lineItems[1]));
          }
        }
        catch(IOException e)
        {throw new RuntimeException(e);}
        finally
        {
          try
          {
            if(stream != null)
            {stream.close();}
          }
          catch(IOException e)
          {throw new RuntimeException(e);}
        }
      }
    }

    public int getId(
      )
    {return id;}

    public Map<Integer,Integer> getMap(
      )
    {return map;}
  }
  // </classes>

  // <static>
  // <fields>
  /**
    Standard Strings [CFF:1.0:10] represent commonly occurring strings allocated to predefined SIDs.
  */
  private static final List<String> StandardStrings;
  // </fields>

  // <constructors>
  static
  {
    StandardStrings = new ArrayList<String>();
    {
      BufferedReader stream = null;
      try
      {
        // Open the resource!
        stream = new BufferedReader(
          new InputStreamReader(
            CffParser.class.getResourceAsStream("/fonts/cff/StandardStrings")
            )
          );
        // Parsing the resource...
        String line;
        while((line = stream.readLine()) != null)
        {StandardStrings.add(line);}
      }
      catch(IOException e)
      {throw new RuntimeException(e);}
      finally
      {
        try
        {
          if(stream != null)
          {stream.close();}
        }
        catch(IOException e)
        {throw new RuntimeException(e);}
      }
    }
  }
  // </constructors>

  // <interface>
  // <private>
  private static String toString(
    byte[] data
    ) throws UnsupportedEncodingException
  {return new String(data, CharsetName.ISO88591);}
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  public Map<Integer,Integer> glyphIndexes;

  private final IInputStream fontData;
  private Index stringIndex;
  // <fields>

  // <constructors>
  CffParser(
    IInputStream fontData
    )
  {
    this.fontData = fontData;

    load();
  }
  // </constructors>

  // <interface>
  // <private>
  /**
    Loads the font data.
  */
  @SuppressWarnings("unused")
  private void load(
    )
  {
    try
    {
      parseHeader();
      Index nameIndex = Index.parse(fontData);
      Index topDictIndex = Index.parse(fontData);
      stringIndex = Index.parse(fontData);
      Index globalSubrIndex = Index.parse(fontData);

      String fontName = toString(nameIndex.get(0));
      Dict topDict = Dict.parse(topDictIndex.get(0));

//      int encodingOffset = topDict.get(Dict.OperatorEnum.Encoding, 0, 0).intValue();
      //TODO: encoding

      int charstringType = topDict.get(Dict.OperatorEnum.CharstringType, 0, 2).intValue();
      int charStringsOffset = topDict.get(Dict.OperatorEnum.CharStrings, 0).intValue();
      Index charStringsIndex = Index.parse(fontData, charStringsOffset);

      int charsetOffset = topDict.get(Dict.OperatorEnum.Charset, 0, 0).intValue();
      StandardCharsetEnum charset = StandardCharsetEnum.get(charsetOffset);
      if(charset != null)
      {
        glyphIndexes = new HashMap<Integer,Integer>(charset.getMap());
      }
      else
      {
        glyphIndexes = new HashMap<Integer,Integer>();
        fontData.setPosition(charsetOffset);
        int charsetFormat = fontData.readUnsignedByte();
        for (int index = 1, count = charStringsIndex.size(); index <= count;)
        {
          switch(charsetFormat)
          {
            case 0:
              glyphIndexes.put(index++, toUnicode(fontData.readUnsignedShort()));
              break;
            case 1:
            case 2:
            {
              int first = fontData.readUnsignedShort();
              int nLeft = (charsetFormat == 1 ? fontData.readUnsignedByte() : fontData.readUnsignedShort());
              for (int rangeItemIndex = first, rangeItemEndIndex = first + nLeft; rangeItemIndex <= rangeItemEndIndex; rangeItemIndex++)
              {glyphIndexes.put(index++, toUnicode(rangeItemIndex));}
            }
              break;
          }
        }
      }
    }
    catch(Exception e)
    {throw new RuntimeException(e);}
  }

  /**
    Gets the string corresponding to the specified identifier.

    @param id SID (String ID).
    @throws UnsupportedEncodingException
  */
  private String getString(
    int id
    ) throws UnsupportedEncodingException
  {
    return id < StandardStrings.size()
      ? StandardStrings.get(id)
      : toString(stringIndex.get(id - StandardStrings.size()));
  }

  private void parseHeader(
    ) throws EOFException
  {
    fontData.seek(2);
    int hdrSize = fontData.readUnsignedByte();
    // Skip to the end of the header!
    fontData.seek(hdrSize);
  }

  private int toUnicode(
    int sid
    ) throws UnsupportedEncodingException
  {
    /*
     * FIXME: avoid Unicode resolution at this stage -- names should be kept to allow subsequent
     * character substitution (see font differences) in case of custom (non-unicode) encodings.
     */
    Integer code = GlyphMapping.nameToCode(getString(sid));
    if(code == null)
    {
      //custom code
      code = sid; // really bad
    }
    return code;
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}
