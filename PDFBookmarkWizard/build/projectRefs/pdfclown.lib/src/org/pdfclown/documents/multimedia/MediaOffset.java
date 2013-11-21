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

package org.pdfclown.documents.multimedia;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfTextString;

/**
  Media offset [PDF:1.7:9.1.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF15)
public abstract class MediaOffset<T>
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Media offset frame [PDF:1.7:9.1.5].

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.2
    @version 0.1.2, 09/24/12
  */
  public final static class Frame
    extends MediaOffset<Integer>
  {
    public Frame(
      Document context,
      int value
      )
    {
      super(context, PdfName.F);
      setValue(value);
    }

    Frame(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Frame clone(
      Document context
      )
    {return (Frame)super.clone(context);}

    /**
      Gets the (zero-based) frame within a media object.
    */
    @Override
    public Integer getValue(
      )
    {return ((PdfInteger)getBaseDataObject().get(PdfName.F)).getValue();}

    @Override
    public void setValue(
      Integer value
      )
    {
      if(value < 0)
        throw new IllegalArgumentException("MUST be non-negative.");

      getBaseDataObject().put(PdfName.F, PdfInteger.get(value));
    }
  }

  /**
    Media offset marker [PDF:1.7:9.1.5].

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.2
    @version 0.1.2, 09/24/12
  */
  public final static class Marker
    extends MediaOffset<String>
  {
    public Marker(
      Document context,
      String value
      )
    {
      super(context, PdfName.M);
      setValue(value);
    }

    Marker(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Marker clone(
      Document context
      )
    {return (Marker)super.clone(context);}

    /**
      Gets a named offset within a media object.
    */
    @Override
    public String getValue(
      )
    {return ((PdfTextString)getBaseDataObject().get(PdfName.M)).getValue();}

    @Override
    public void setValue(
      String value
      )
    {getBaseDataObject().put(PdfName.M, PdfTextString.get(value));}
  }

  /**
    Media offset time [PDF:1.7:9.1.5].

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.2
    @version 0.1.2, 09/24/12
  */
  public final static class Time
    extends MediaOffset<Double>
  {
    public Time(
      Document context,
      double value
      )
    {
      super(context, PdfName.T);
      getBaseDataObject().put(PdfName.T, new Timespan(value).getBaseObject());
    }

    Time(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Time clone(
      Document context
      )
    {return (Time)super.clone(context);}

    /**
      Gets the temporal offset (in seconds).
    */
    @Override
    public Double getValue(
      )
    {return getTimespan().getTime();}

    @Override
    public void setValue(
      Double value
      )
    {getTimespan().setTime(value);}

    private Timespan getTimespan(
      )
    {return new Timespan(getBaseDataObject().get(PdfName.T));}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static MediaOffset<?> wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfDictionary dataObject = (PdfDictionary)baseObject.resolve();
    PdfName offsetType = (PdfName)dataObject.get(PdfName.S);
    if(offsetType == null
      || (dataObject.containsKey(PdfName.Type)
          && !dataObject.get(PdfName.Type).equals(PdfName.MediaOffset)))
      return null;

    if(offsetType.equals(PdfName.F))
      return new Frame(baseObject);
    else if(offsetType.equals(PdfName.M))
      return new Marker(baseObject);
    else if(offsetType.equals(PdfName.T))
      return new Time(baseObject);
    else
      throw new UnsupportedOperationException();
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected MediaOffset(
    Document context,
    PdfName subtype
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.S
        },
        new PdfDirectObject[]
        {
          PdfName.MediaOffset,
          subtype
        }
        )
      );
  }

  protected MediaOffset(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the offset value.
  */
  public abstract T getValue(
    );

  /**
    @see #getValue()
  */
  public abstract void setValue(
    T value
    );
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
