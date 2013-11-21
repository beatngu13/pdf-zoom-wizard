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

package org.pdfclown.documents.interaction.annotations;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.DeviceColor;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfTextString;

/**
  Appearance characteristics [PDF:1.6:8.4.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class AppearanceCharacteristics
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Caption position relative to its icon [PDF:1.6:8.4.5].
  */
  public enum CaptionPositionEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Caption only (no icon).
    */
    CaptionOnly(0),
    /**
      No caption (icon only).
    */
    NoCaption(1),
    /**
      Caption below the icon.
    */
    Below(2),
    /**
      Caption above the icon.
    */
    Above(3),
    /**
      Caption to the right of the icon.
    */
    Right(4),
    /**
      Caption to the left of the icon.
    */
    Left(5),
    /**
      Caption overlaid directly on the icon.
    */
    Overlaid(6);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the caption position corresponding to the given value.
    */
    public static CaptionPositionEnum get(
      int value
      )
    {
      for(CaptionPositionEnum position : CaptionPositionEnum.values())
      {
        if(position.getCode() == value)
          return position;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final int code;
    // </fields>

    // <constructors>
    private CaptionPositionEnum(
      int code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public int getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }

  /**
    Icon fit [PDF:1.6:8.6.6].
  */
  public static class IconFitObject
    extends PdfObjectWrapper<PdfDictionary>
  {
    // <class>
    // <classes>
    /**
      Scaling mode [PDF:1.6:8.6.6].
    */
    public enum ScaleModeEnum
    {
      // <class>
      // <static>
      // <fields>
      /**
        Always scale.
      */
      Always(PdfName.A),
      /**
        Scale only when the icon is bigger than the annotation box.
      */
      Bigger(PdfName.B),
      /**
        Scale only when the icon is smaller than the annotation box.
      */
      Smaller(PdfName.S),
      /**
        Never scale.
      */
      Never(PdfName.N);
      // </fields>

      // <interface>
      // <public>
      /**
        Gets the scaling mode corresponding to the given value.
      */
      public static ScaleModeEnum get(
        PdfName value
        )
      {
        for(ScaleModeEnum mode : ScaleModeEnum.values())
        {
          if(mode.getCode().equals(value))
            return mode;
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
      private ScaleModeEnum(
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

    /**
      Scaling type [PDF:1.6:8.6.6].
    */
    public enum ScaleTypeEnum
    {
      // <class>
      // <static>
      // <fields>
      /**
        Scale the icon to fill the annotation box exactly,
        without regard to its original aspect ratio.
      */
      Anamorphic(PdfName.A),
      /**
        Scale the icon to fit the width or height of the annotation box,
        while maintaining the icon's original aspect ratio.
      */
      Proportional(PdfName.P);
      // </fields>

      // <interface>
      // <public>
      /**
        Gets the scaling type corresponding to the given value.
      */
      public static ScaleTypeEnum get(
        PdfName value
        )
      {
        for(ScaleTypeEnum mode : ScaleTypeEnum.values())
        {
          if(mode.getCode().equals(value))
            return mode;
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
      private ScaleTypeEnum(
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

    // <dynamic>
    // <constructors>
    public IconFitObject(
      Document context
      )
    {super(context, new PdfDictionary());}

    IconFitObject(
      PdfDirectObject baseObject
      )
    {super(baseObject);}
    // </constructors>

    // <interface>
    // <public>
    @Override
    public IconFitObject clone(
      Document context
      )
    {return (IconFitObject)super.clone(context);}

    /**
      Gets the circumstances under which the icon should be scaled inside the annotation box.
    */
    public ScaleModeEnum getScaleMode(
      )
    {
      /*
        NOTE: 'SW' entry may be undefined.
      */
      PdfName scaleModeObject = (PdfName)getBaseDataObject().get(PdfName.SW);
      if(scaleModeObject == null)
        return ScaleModeEnum.Always;

      return ScaleModeEnum.get(scaleModeObject);
    }

    /**
      Gets the type of scaling to use.
    */
    public ScaleTypeEnum getScaleType(
      )
    {
      /*
        NOTE: 'S' entry may be undefined.
      */
      PdfName scaleTypeObject = (PdfName)getBaseDataObject().get(PdfName.S);
      if(scaleTypeObject == null)
        return ScaleTypeEnum.Proportional;

      return ScaleTypeEnum.get(scaleTypeObject);
    }

    /**
      Gets the horizontal alignment of the icon inside the annotation box.
    */
    public XAlignmentEnum getXAlignment(
      )
    {
      /*
        NOTE: 'A' entry may be undefined.
      */
      PdfArray alignmentObject = (PdfArray)getBaseDataObject().get(PdfName.A);
      if(alignmentObject == null)
        return XAlignmentEnum.Center;

      switch((int)Math.round(((PdfNumber<?>)alignmentObject.get(0)).getDoubleValue()/.5))
      {
        case 0: return XAlignmentEnum.Left;
        case 2: return XAlignmentEnum.Right;
        default: return XAlignmentEnum.Center;
      }
    }

    /**
      Gets the vertical alignment of the icon inside the annotation box.
    */
    public YAlignmentEnum getYAlignment(
      )
    {
      /*
        NOTE: 'A' entry may be undefined.
      */
      PdfArray alignmentObject = (PdfArray)getBaseDataObject().get(PdfName.A);
      if(alignmentObject == null)
        return YAlignmentEnum.Middle;

      switch((int)Math.round(((PdfNumber<?>)alignmentObject.get(1)).getDoubleValue()/.5))
      {
        case 0: return YAlignmentEnum.Bottom;
        case 2: return YAlignmentEnum.Top;
        default: return YAlignmentEnum.Middle;
      }
    }

    /**
      Gets whether not to take into consideration the line width of the border.
    */
    public boolean isBorderExcluded(
      )
    {
      PdfBoolean borderExcludedObject = (PdfBoolean)getBaseDataObject().get(PdfName.FB);
      return borderExcludedObject != null
        ? borderExcludedObject.getValue()
        : false;
    }

    /**
      Sets whether not to take into consideration the line width of the border.

      @see #isBorderExcluded()
    */
    public void setBorderExcluded(
      boolean value
      )
    {getBaseDataObject().put(PdfName.FB, PdfBoolean.get(value));}

    /**
      Sets the circumstances under which the icon should be scaled inside the annotation box.

      @see #getScaleMode()
    */
    public void setScaleMode(
      ScaleModeEnum value
      )
    {getBaseDataObject().put(PdfName.SW, value.getCode());}

    /**
      Sets the type of scaling to use.

      @see #getScaleType()
    */
    public void setScaleType(
      ScaleTypeEnum value
      )
    {getBaseDataObject().put(PdfName.S, value.getCode());}

    /**
      Sets the horizontal alignment of the icon inside the annotation box.

      @see #getXAlignment()
    */
    public void setXAlignment(
      XAlignmentEnum value
      )
    {
      /*
        NOTE: 'A' entry may be undefined.
      */
      PdfArray alignmentObject = (PdfArray)getBaseDataObject().get(PdfName.A);
      if(alignmentObject == null)
      {
        alignmentObject = new PdfArray(
          new PdfDirectObject[]
          {
            PdfReal.get(0.5),
            PdfReal.get(0.5)
          }
          );
        getBaseDataObject().put(PdfName.A, alignmentObject);
      }

      double objectValue;
      switch(value)
      {
        case Left:
          objectValue = 0;
          break;
        case Right:
          objectValue = 1;
          break;
        default:
          objectValue = 0.5;
          break;
      }
      alignmentObject.set(0, PdfReal.get(objectValue));
    }

    /**
      Sets the vertical alignment of the icon inside the annotation box.

      @see #getYAlignment()
    */
    public void setYAlignment(
      YAlignmentEnum value
      )
    {
      /*
        NOTE: 'A' entry may be undefined.
      */
      PdfArray alignmentObject = (PdfArray)getBaseDataObject().get(PdfName.A);
      if(alignmentObject == null)
      {
        alignmentObject = new PdfArray(
          new PdfDirectObject[]
          {
            PdfReal.get(0.5),
            PdfReal.get(0.5)
          }
          );
        getBaseDataObject().put(PdfName.A, alignmentObject);
      }

      double objectValue;
      switch(value)
      {
        case Bottom:
          objectValue = 0;
          break;
        case Top:
          objectValue = 1;
          break;
        default:
          objectValue = 0.5;
          break;
      }
      alignmentObject.set(1, PdfReal.get(objectValue));
    }
    // </public>
    // </interface>
    // </class>
  }

  /**
    Annotation orientation [PDF:1.6:8.4.5].
  */
  public enum OrientationEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Upward.
    */
    Up(0),
    /**
      Leftward.
    */
    Left(90),
    /**
      Downward.
    */
    Down(180),
    /**
      Rightward.
    */
    Right(270);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the orientation corresponding to the given value.
    */
    public static OrientationEnum get(
      int value
      )
    {
      value = value % 360 - value % 90;
      for(OrientationEnum orientation : OrientationEnum.values())
      {
        if(orientation.getCode() == value)
          return orientation;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final int code;
    // </fields>

    // <constructors>
    private OrientationEnum(
      int code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public int getCode(
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
  public static AppearanceCharacteristics wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new AppearanceCharacteristics(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public AppearanceCharacteristics(
    Document context
    )
  {super(context, new PdfDictionary());}

  private AppearanceCharacteristics(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public AppearanceCharacteristics clone(
    Document context
    )
  {return (AppearanceCharacteristics)super.clone(context);}

  /**
    Gets the widget annotation's alternate (down) caption,
    displayed when the mouse button is pressed within its active area
    (Pushbutton fields only).
  */
  public String getAlternateCaption(
    )
  {
    PdfTextString alternateCaptionObject = (PdfTextString)getBaseDataObject().get(PdfName.AC);
    return alternateCaptionObject != null ? alternateCaptionObject.getValue() :null;
  }

  /**
    Gets the widget annotation's alternate (down) icon definition,
    displayed when the mouse button is pressed within its active area
    (Pushbutton fields only).
  */
  public FormXObject getAlternateIcon(
    )
  {return FormXObject.wrap(getBaseDataObject().get(PdfName.IX));}

  /**
    Gets the widget annotation's background color.
  */
  public DeviceColor getBackgroundColor(
    )
  {return getColor(PdfName.BG);}

  /**
    Gets the widget annotation's border color.
  */
  public DeviceColor getBorderColor(
    )
  {return getColor(PdfName.BC);}

  /**
    Gets the position of the caption relative to its icon (Pushbutton fields only).
  */
  public CaptionPositionEnum getCaptionPosition(
    )
  {
    PdfInteger captionPositionObject = (PdfInteger)getBaseDataObject().get(PdfName.TP);
    return captionPositionObject != null
      ? CaptionPositionEnum.get(captionPositionObject.getRawValue())
      : CaptionPositionEnum.CaptionOnly;
  }

  /**
    Gets the icon fit specifying how to display the widget annotation's icon
    within its annotation box (Pushbutton fields only).
    If present, the icon fit applies to all of the annotation's icons
    (normal, rollover, and alternate).
  */
  public IconFitObject getIconFit(
    )
  {
    PdfDirectObject iconFitObject = getBaseDataObject().get(PdfName.IF);
    return iconFitObject != null ? new IconFitObject(iconFitObject) : null;
  }

  /**
    Gets the widget annotation's normal caption,
    displayed when it is not interacting with the user (Button fields only).
  */
  public String getNormalCaption(
    )
  {
    PdfTextString normalCaptionObject = (PdfTextString)getBaseDataObject().get(PdfName.CA);
    return normalCaptionObject != null ? normalCaptionObject.getValue() : null;
  }

  /**
    Gets the widget annotation's normal icon definition,
    displayed when it is not interacting with the user (Pushbutton fields only).
  */
  public FormXObject getNormalIcon(
    )
  {return FormXObject.wrap(getBaseDataObject().get(PdfName.I));}

  /**
    Gets the widget annotation's orientation.
  */
  public OrientationEnum getOrientation(
    )
  {
    PdfInteger orientationObject = (PdfInteger)getBaseDataObject().get(PdfName.R);
    return orientationObject != null
      ? OrientationEnum.get(orientationObject.getRawValue())
      : OrientationEnum.Up;
  }

  /**
    Gets the widget annotation's rollover caption,
    displayed when the user rolls the cursor into its active area
    without pressing the mouse button (Pushbutton fields only).
  */
  public String getRolloverCaption(
    )
  {
    PdfTextString rolloverCaptionObject = (PdfTextString)getBaseDataObject().get(PdfName.RC);
    return rolloverCaptionObject != null ? rolloverCaptionObject.getValue() : null;
  }

  /**
    Gets the widget annotation's rollover icon definition,
    displayed when the user rolls the cursor into its active area
    without pressing the mouse button (Pushbutton fields only).
  */
  public FormXObject getRolloverIcon(
    )
  {return FormXObject.wrap(getBaseDataObject().get(PdfName.RI));}

  /**
    Sets the widget annotation's alternate (down) caption.

    @see #getAlternateCaption()
  */
  public void setAlternateCaption(
    String value
    )
  {getBaseDataObject().put(PdfName.AC, new PdfTextString(value));}

  /**
    Sets the widget annotation's alternate (down) icon definition.

    @see #getAlternateIcon()
  */
  public void setAlternateIcon(
    FormXObject value
    )
  {getBaseDataObject().put(PdfName.IX, value.getBaseObject());}

  /**
    Sets the widget annotation's background color.

    @see #getBackgroundColor()
  */
  public void setBackgroundColor(
    DeviceColor value
    )
  {setColor(PdfName.BG, value);}

  /**
    Sets the widget annotation's border color.

    @see #getBorderColor()
  */
  public void setBorderColor(
    DeviceColor value
    )
  {setColor(PdfName.BC, value);}

  /**
    Sets the position of the caption relative to its icon.

    @see #getCaptionPosition()
  */
  public void setCaptionPosition(
    CaptionPositionEnum value
    )
  {getBaseDataObject().put(PdfName.TP, PdfInteger.get(value.getCode()));}

  /**
    Sets the icon fit specifying how to display the widget annotation's icon
    within its annotation box.

    @see #getIconFit()
  */
  public void setIconFit(
    IconFitObject value
    )
  {getBaseDataObject().put(PdfName.IF, PdfObjectWrapper.getBaseObject(value));}

  /**
    Sets the widget annotation's normal caption.

    @see #getNormalCaption()
  */
  public void setNormalCaption(
    String value
    )
  {getBaseDataObject().put(PdfName.CA, PdfTextString.get(value));}

  /**
    Sets the widget annotation's normal icon definition.

    @see #getNormalIcon()
  */
  public void setNormalIcon(
    FormXObject value
    )
  {getBaseDataObject().put(PdfName.I, PdfObjectWrapper.getBaseObject(value));}

  /**
    Sets the widget annotation's orientation.

    @see #getOrientation()
  */
  public void setOrientation(
    OrientationEnum value
    )
  {getBaseDataObject().put(PdfName.R, PdfInteger.get(value.getCode()));}

  /**
    Sets the widget annotation's rollover caption.

    @see #getRolloverCaption()
  */
  public void setRolloverCaption(
    String value
    )
  {getBaseDataObject().put(PdfName.RC, PdfTextString.get(value));}

  /**
    Sets the widget annotation's rollover icon definition.

    @see #getRolloverIcon()
  */
  public void setRolloverIcon(
    FormXObject value
    )
  {getBaseDataObject().put(PdfName.RI, PdfObjectWrapper.getBaseObject(value));}
  // </public>

  // <private>
  private DeviceColor getColor(
    PdfName key
    )
  {return DeviceColor.get((PdfArray)getBaseDataObject().resolve(key));}

  private void setColor(
    PdfName key,
    DeviceColor value
    )
  {getBaseDataObject().put(key, PdfObjectWrapper.getBaseObject(value));}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}