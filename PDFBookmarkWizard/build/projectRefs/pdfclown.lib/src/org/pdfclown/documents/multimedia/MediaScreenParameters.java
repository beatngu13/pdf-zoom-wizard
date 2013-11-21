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

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.interaction.annotations.Screen;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Media screen parameters [PDF:1.7:9.1.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public class MediaScreenParameters
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Media screen parameters viability.
  */
  public static class Viability
    extends PdfObjectWrapper<PdfDictionary>
  {
    public static class FloatingWindowParameters
      extends PdfObjectWrapper<PdfDictionary>
    {
      public enum LocationEnum
      {
        /**
          Upper-left corner.
        */
        UpperLeft(new PdfInteger(0)),
        /**
          Upper center.
        */
        UpperCenter(new PdfInteger(1)),
        /**
          Upper-right corner.
        */
        UpperRight(new PdfInteger(2)),
        /**
          Center left.
        */
        CenterLeft(new PdfInteger(3)),
        /**
          Center.
        */
        Center(new PdfInteger(4)),
        /**
          Center right.
        */
        CenterRight(new PdfInteger(5)),
        /**
          Lower-left corner.
        */
        LowerLeft(new PdfInteger(6)),
        /**
          Lower center.
        */
        LowerCenter(new PdfInteger(7)),
        /**
          Lower-right corner.
        */
        LowerRight(new PdfInteger(8));

        private static Map<PdfInteger, LocationEnum> map = new HashMap<PdfInteger, LocationEnum>();

        static
        {
          for (LocationEnum value : LocationEnum.values())
          {map.put(value.getCode(), value);}
        }

        public static LocationEnum valueOf(
          PdfInteger code
          )
        {return map.containsKey(code) ? map.get(code) : Center;}

        private final PdfInteger code;

        private LocationEnum(
          PdfInteger code
          )
        {this.code = code;}

        public PdfInteger getCode(
          )
        {return code;}
      }

      public enum OffscreenBehaviorEnum
      {
        /**
          Take no special action.
        */
        None(new PdfInteger(0)),
        /**
          Move and/or resize the window so that it is on-screen.
        */
        Adapt(new PdfInteger(1)),
        /**
          Consider the object to be non-viable.
        */
        NonViable(new PdfInteger(2));

        private static Map<PdfInteger, OffscreenBehaviorEnum> map = new HashMap<PdfInteger, OffscreenBehaviorEnum>();

        static
        {
          for (OffscreenBehaviorEnum value : OffscreenBehaviorEnum.values())
          {map.put(value.getCode(), value);}
        }

        public static OffscreenBehaviorEnum valueOf(
          PdfInteger code
          )
        {return map.containsKey(code) ? map.get(code) : Adapt;}

        private final PdfInteger code;

        private OffscreenBehaviorEnum(
          PdfInteger code
          )
        {this.code = code;}

        public PdfInteger getCode(
          )
        {return code;}
      }

      public enum RelatedWindowEnum
      {
        /**
          The document window.
        */
        Document(new PdfInteger(0)),
        /**
          The application window.
        */
        Application(new PdfInteger(1)),
        /**
          The full virtual desktop.
        */
        Desktop(new PdfInteger(2)),
        /**
          The monitor specified by {@link MediaScreenParameters.Viability#getMonitorSpecifier()}.
        */
        Custom(new PdfInteger(3));

        private static Map<PdfInteger, RelatedWindowEnum> map = new HashMap<PdfInteger, RelatedWindowEnum>();

        static
        {
          for (RelatedWindowEnum value : RelatedWindowEnum.values())
          {map.put(value.getCode(), value);}
        }

        public static RelatedWindowEnum valueOf(
          PdfInteger code
          )
        {return map.containsKey(code) ? map.get(code) : Document;}

        private final PdfInteger code;

        private RelatedWindowEnum(
          PdfInteger code
          )
        {this.code = code;}

        public PdfInteger getCode(
          )
        {return code;}
      }

      public enum ResizeBehaviorEnum
      {
        /**
          Not resizable.
        */
        None(new PdfInteger(0)),
        /**
          Resizable preserving its aspect ratio.
        */
        AspectRatioLocked(new PdfInteger(1)),
        /**
          Resizable without preserving its aspect ratio.
        */
        Free(new PdfInteger(2));

        private static Map<PdfInteger, ResizeBehaviorEnum> map = new HashMap<PdfInteger, ResizeBehaviorEnum>();

        static
        {
          for (ResizeBehaviorEnum value : ResizeBehaviorEnum.values())
          {map.put(value.getCode(), value);}
        }

        public static ResizeBehaviorEnum valueOf(
          PdfInteger code
          )
        {return map.containsKey(code) ? map.get(code) : None;}

        private final PdfInteger code;

        private ResizeBehaviorEnum(
          PdfInteger code
          )
        {this.code = code;}

        public PdfInteger getCode(
          )
        {return code;}
      }

      public FloatingWindowParameters(
        Dimension size
        )
      {
        super(
          new PdfDictionary(
            new PdfName[]
            {PdfName.Type},
            new PdfDirectObject[]
            {PdfName.FWParams}
            )
          );
        setSize(size);
      }

      private FloatingWindowParameters(
        PdfDirectObject baseObject
        )
      {super(baseObject);}

      @Override
      public FloatingWindowParameters clone(
        Document context
        )
      {return (FloatingWindowParameters)super.clone(context);}

      /**
        Gets the location where the floating window should be positioned relative to the related
        window.
      */
      public LocationEnum getLocation(
        )
      {return LocationEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.P));}

      /**
        Gets what should occur if the floating window is positioned totally or partially offscreen
        (that is, not visible on any physical monitor).
      */
      public OffscreenBehaviorEnum getOffscreenBehavior(
        )
      {return OffscreenBehaviorEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.O));}

      /**
        Gets the window relative to which the floating window should be positioned.
      */
      public RelatedWindowEnum getRelatedWindow(
        )
      {return RelatedWindowEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.RT));}

      /**
        Gets how the floating window may be resized by a user.
      */
      public ResizeBehaviorEnum getResizeBehavior(
        )
      {return ResizeBehaviorEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.R));}

      /**
        Gets the floating window's width and height, in pixels. These values correspond to the
        dimensions of the rectangle in which the media will play, not including such items as title
        bar and resizing handles.
      */
      public Dimension getSize(
        )
      {
        PdfArray sizeObject = (PdfArray)getBaseDataObject().get(PdfName.D);
        return new Dimension(((PdfInteger)sizeObject.get(0)).getValue(), ((PdfInteger)sizeObject.get(1)).getValue());
      }

      /**
        Gets whether the floating window should include user interface elements that allow a user to
        close it. Meaningful only if {@link #isTitleBarVisible()} is true.
      */
      public boolean isCloseable(
        )
      {return (Boolean)PdfBoolean.getValue(getBaseDataObject().get(PdfName.UC), true);}

      /**
        Gets whether the floating window should have a title bar.
      */
      public boolean isTitleBarVisible(
        )
      {return (Boolean)PdfBoolean.getValue(getBaseDataObject().get(PdfName.T), true);}

      /**
        @see #isCloseable()
      */
      public void setCloseable(
        boolean value
        )
      {getBaseDataObject().put(PdfName.UC, PdfBoolean.get(value));}

      /**
        @see #getLocation()
      */
      public void setLocation(
        LocationEnum value
        )
      {getBaseDataObject().put(PdfName.P, value != null ? value.getCode() : null);}

      /**
        @see #getOffscreenBehavior()
      */
      public void setOffscreenBehavior(
        OffscreenBehaviorEnum value
        )
      {getBaseDataObject().put(PdfName.O, value != null ? value.getCode() : null);}

      /**
        @see #getRelatedWindow()
      */
      public void setRelatedWindow(
        RelatedWindowEnum value
        )
      {getBaseDataObject().put(PdfName.RT, value != null ? value.getCode() : null);}

      /**
        @see #getResizeBehavior()
      */
      public void setResizeBehavior(
        ResizeBehaviorEnum value
        )
      {getBaseDataObject().put(PdfName.R, value != null ? value.getCode() : null);}

      /**
        @see #getSize()
       */
      public void setSize(
        Dimension value
        )
      {getBaseDataObject().put(PdfName.D, new PdfArray(PdfInteger.get(value.getWidth()), PdfInteger.get(value.getHeight())));}

      /**
        @see #isTitleBarVisible()
      */
      public void setTitleBarVisible(
        boolean value
        )
      {getBaseDataObject().put(PdfName.T, PdfBoolean.get(value));}

      //TODO: TT entry!
    }

    public enum WindowTypeEnum
    {
      /**
        A floating window.
      */
      Floating(new PdfInteger(0)),
      /**
        A full-screen window that obscures all other windows.
      */
      FullScreen(new PdfInteger(1)),
      /**
        A hidden window.
      */
      Hidden(new PdfInteger(2)),
      /**
        The rectangle occupied by the {@link Screen screen annotation} associated with the media
        rendition.
      */
      Annotation(new PdfInteger(3));

      private static Map<PdfInteger, WindowTypeEnum> map = new HashMap<PdfInteger, WindowTypeEnum>();

      static
      {
        for (WindowTypeEnum value : WindowTypeEnum.values())
        {map.put(value.getCode(), value);}
      }

      public static WindowTypeEnum valueOf(
        PdfInteger code
        )
      {return map.containsKey(code) ? map.get(code) : Annotation;}

      private final PdfInteger code;

      private WindowTypeEnum(
        PdfInteger code
        )
      {this.code = code;}

      public PdfInteger getCode(
        )
      {return code;}
    }

    private Viability(
      PdfDirectObject baseObject
      )
    {super(baseObject);}

    @Override
    public Viability clone(
      Document context
      )
    {return (Viability)super.clone(context);}

    /**
      Gets the background color for the rectangle in which the media is being played. This color is
      used if the media object does not entirely cover the rectangle or if it has transparent
      sections.
    */
    public DeviceRGBColor getBackgroundColor(
      )
    {return DeviceRGBColor.get((PdfArray)getBaseDataObject().get(PdfName.B));}

    /**
      Gets the opacity of the background color.

      @return A number in the range 0 to 1, where 0 means full transparency and 1 full opacity.
    */
    public double getBackgroundOpacity(
      )
    {return (Double)PdfReal.getValue(getBaseDataObject().get(PdfName.O), 1d);}

    /**
      Gets the options used in displaying floating windows.
    */
    public FloatingWindowParameters getFloatingWindowParameters(
      )
    {return new FloatingWindowParameters(getBaseDataObject().get(PdfName.F, PdfDictionary.class));}

    /**
      Gets which monitor in a multi-monitor system a floating or full-screen window should appear on.
    */
    public MonitorSpecifierEnum getMonitorSpecifier(
      )
    {return MonitorSpecifierEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.M));}

    /**
      Gets the type of window that the media object should play in.
    */
    public WindowTypeEnum getWindowType(
      )
    {return WindowTypeEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.W));}

    /**
      @see #getBackgroundColor()
    */
    public void setBackgroundColor(
      DeviceRGBColor value
      )
    {getBaseDataObject().put(PdfName.B, PdfObjectWrapper.getBaseObject(value));}

    /**
      @see #getBackgroundOpacity()
    */
    public void setBackgroundOpacity(
      double value
      )
    {
      if(value < 0)
      {value = 0;}
      else if(value > 1)
      {value = 1;}
      getBaseDataObject().put(PdfName.O, PdfReal.get(value));
    }

    /**
      @see #getFloatingWindowParameters()
    */
    public void setFloatingWindowParameters(
      FloatingWindowParameters value
      )
    {getBaseDataObject().put(PdfName.F, PdfObjectWrapper.getBaseObject(value));}

    /**
      @see #getMonitorSpecifier()
    */
    public void setMonitorSpecifier(
      MonitorSpecifierEnum value
      )
    {getBaseDataObject().put(PdfName.M, value != null ? value.getCode() : null);}

    /**
      @see #getWindowType()
    */
    public void setWindowType(
      WindowTypeEnum value
      )
    {getBaseDataObject().put(PdfName.W, value != null ? value.getCode() : null);}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public MediaScreenParameters(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.MediaScreenParams}
        )
      );
  }

  MediaScreenParameters(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public MediaScreenParameters clone(
    Document context
    )
  {return (MediaScreenParameters)super.clone(context);}

  /**
    Gets the preferred options the renderer should attempt to honor without affecting its viability.
  */
  public Viability getPreferences(
    )
  {return new Viability(getBaseDataObject().get(PdfName.BE, PdfDictionary.class));}

  /**
    Gets the minimum requirements the renderer must honor in order to be considered viable.
  */
  public Viability getRequirements(
    )
  {return new Viability(getBaseDataObject().get(PdfName.MH, PdfDictionary.class));}

  /**
    @see #getPreferences()
  */
  public void setPreferences(
    Viability value
    )
  {getBaseDataObject().put(PdfName.BE, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getRequirements()
  */
  public void setRequirements(
    Viability value
    )
  {getBaseDataObject().put(PdfName.MH, PdfObjectWrapper.getBaseObject(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
