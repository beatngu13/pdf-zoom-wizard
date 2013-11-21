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

package org.pdfclown.documents.interaction.viewer;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfSimpleObject;

/**
  Viewer preferences [PDF:1.6:8.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class ViewerPreferences
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Predominant reading order for text [PDF:1.6:8.1].
  */
  public enum DirectionEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Left to right.
    */
    LeftToRight(PdfName.L2R),
    /**
      Right to left.
    */
    RightToLeft(PdfName.R2L);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the direction corresponding to the given value.
    */
    public static DirectionEnum get(
      PdfName value
      )
    {
      for(DirectionEnum direction : DirectionEnum.values())
      {
        if(direction.getCode().equals(value))
          return direction;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfName code;
    // </fields>

    // <constructors>
    private DirectionEnum(
      PdfName code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfName getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  public static ViewerPreferences wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ViewerPreferences(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ViewerPreferences(
    Document context
    )
  {super(context, new PdfDictionary());}

  private ViewerPreferences(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public ViewerPreferences clone(
    Document context
    )
  {return (ViewerPreferences)super.clone(context);}

  public DirectionEnum getDirection(
    )
  {
    PdfName directionObject = (PdfName)getBaseDataObject().get(PdfName.Direction);
    return directionObject != null ? DirectionEnum.get(directionObject) : DirectionEnum.LeftToRight;
  }

  public boolean isCenterWindow(
    )
  {return (Boolean)get(PdfName.CenterWindow, false);}

  public boolean isDisplayDocTitle(
    )
  {return (Boolean)get(PdfName.DisplayDocTitle, false);}

  public boolean isFitWindow(
    )
  {return (Boolean)get(PdfName.FitWindow, false);}

  public boolean isHideMenubar(
    )
  {return (Boolean)get(PdfName.HideMenubar, false);}

  public boolean isHideToolbar(
    )
  {return (Boolean)get(PdfName.HideToolbar, false);}

  public boolean isHideWindowUI(
    )
  {return (Boolean)get(PdfName.HideWindowUI, false);}

  public void setCenterWindow(
    boolean value
    )
  {getBaseDataObject().put(PdfName.CenterWindow, PdfBoolean.get(value));}

  public void setDirection(
    DirectionEnum value
    )
  {getBaseDataObject().put(PdfName.Direction, value == null ? null : value.getCode());}

  public void setDisplayDocTitle(
    boolean value
    )
  {getBaseDataObject().put(PdfName.DisplayDocTitle, PdfBoolean.get(value));}

  public void setFitWindow(
    boolean value
    )
  {getBaseDataObject().put(PdfName.FitWindow, PdfBoolean.get(value));}

  public void setHideMenubar(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideMenubar, PdfBoolean.get(value));}

  public void setHideToolbar(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideToolbar, PdfBoolean.get(value));}

  public void setHideWindowUI(
    boolean value
    )
  {getBaseDataObject().put(PdfName.HideWindowUI, PdfBoolean.get(value));}
  // </public>

  // <private>
  private Object get(
    PdfName key,
    Object defaultValue
    )
  {return PdfSimpleObject.getValue(getBaseDataObject().get(key), defaultValue);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}