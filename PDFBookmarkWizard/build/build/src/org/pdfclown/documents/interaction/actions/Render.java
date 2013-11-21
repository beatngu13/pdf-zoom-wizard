/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.actions;

import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.annotations.Annotation;
import org.pdfclown.documents.interaction.annotations.Screen;
import org.pdfclown.documents.multimedia.Rendition;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;

/**
  'Control the playing of multimedia content' action [PDF:1.6:8.5.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class Render
  extends Action
{
  // <class>
  // <classes>
  public enum OperationEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Play the rendition on the screen, stopping any previous one.
    */
    Play(new PdfInteger(0)),
    /**
      Stop any rendition being played on the screen.
    */
    Stop(new PdfInteger(1)),
    /**
      Pause any rendition being played on the screen.
    */
    Pause(new PdfInteger(2)),
    /**
      Resume any rendition being played on the screen.
    */
    Resume(new PdfInteger(3)),
    /**
      Play the rendition on the screen, resuming any previous one.
    */
    PlayResume(new PdfInteger(4));

    private static Map<PdfInteger, OperationEnum> map = new HashMap<PdfInteger, OperationEnum>();
    // </fields>

    // <constructors>
    static
    {
      for (OperationEnum value : OperationEnum.values())
      {map.put(value.getCode(), value);}
    }
    // </constructors>

    // <interface>
    // <public>
    public static OperationEnum valueOf(
      PdfInteger code
      )
    {return map.get(code);}
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfInteger code;
    // </fields>

    // <constructors>
    private OperationEnum(
      PdfInteger code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfInteger getCode(
      )
    {return code;}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public Render(
    Screen screen,
    OperationEnum operation,
    Rendition rendition
    )
  {
    super(screen.getDocument(), PdfName.Rendition);
    setOperation(operation);
    setScreen(screen);
    setRendition(rendition);
  }

  Render(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Render clone(
    Document context
    )
  {return (Render)super.clone(context);}

  /**
    Gets the operation to perform when the action is triggered.
  */
  public OperationEnum getOperation(
    )
  {return OperationEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.OP));}

  /**
    Gets the rendition object to render.
  */
  public Rendition getRendition(
    )
  {return Rendition.wrap(getBaseDataObject().get(PdfName.R));}

  /**
    Gets the screen where to render the rendition object.
  */
  public Screen getScreen(
    )
  {return (Screen)Annotation.wrap(getBaseDataObject().get(PdfName.AN));}

  /**
    Gets the JavaScript script to be executed when the action is triggered.
  */
  public String getScript(
    )
  {return JavaScript.getScript(getBaseDataObject(), PdfName.JS);}

  /**
    @see #getOperation()
  */
  public void setOperation(
    OperationEnum value
    )
  {
    PdfDictionary baseDataObject = getBaseDataObject();
    if(value == null && baseDataObject.get(PdfName.JS) == null)
        throw new IllegalArgumentException("Operation MUST be defined.");

    baseDataObject.put(PdfName.OP, value != null ? value.getCode() : null);
  }

  /**
    @see #getRendition()
  */
  public void setRendition(
    Rendition value
    )
  {
    if(value == null)
    {
      OperationEnum operation = getOperation();
      if(operation != null)
      {
        switch(operation)
        {
          case Play:
          case PlayResume:
            throw new IllegalArgumentException("Rendition MUST be defined.");
          default:
          {
            /* NOOP */
          }
        }
      }
    }
    getBaseDataObject().put(PdfName.R, PdfObjectWrapper.getBaseObject(value));
  }

  /**
    @see #getScreen()
  */
  public void setScreen(
    Screen value
    )
  {
    if(value == null)
    {
      OperationEnum operation = getOperation();
      if(operation != null)
      {
        switch(operation)
        {
          case Play:
          case PlayResume:
          case Pause:
          case Resume:
          case Stop:
            throw new IllegalArgumentException("Screen MUST be defined.");
        }
      }
    }
    getBaseDataObject().put(PdfName.AN, PdfObjectWrapper.getBaseObject(value));
  }

  /**
    @see #getScript()
  */
  public void setScript(
    String value
    )
  {
    PdfDictionary baseDataObject = getBaseDataObject();
    if(value == null && baseDataObject.get(PdfName.OP) == null)
        throw new IllegalArgumentException("Script MUST be defined.");

    JavaScript.setScript(baseDataObject, PdfName.JS, value);
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}