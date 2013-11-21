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

import java.util.HashMap;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;

/**
  Media play parameters [PDF:1.7:9.1.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class MediaPlayParameters
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Media player parameters viability.
  */
  public static class Viability
    extends PdfObjectWrapper<PdfDictionary>
  {
    private static class Duration
      extends PdfObjectWrapper<PdfDictionary>
    {
      private Duration(
        double value
        )
      {
        super(
          new PdfDictionary(
            new PdfName[]
            {PdfName.Type},
            new PdfDirectObject[]
            {PdfName.MediaDuration}
            )
          );
        setValue(value);
      }

      private Duration(
        PdfDirectObject baseObject
        )
      {super(baseObject);}

      @Override
      public Duration clone(
        Document context
        )
      {return (Duration)super.clone(context);}

      /**
        Gets the temporal duration.

        @return
          <ul>
            <li><code>Double.NEGATIVE_INFINITY</code>: intrinsic duration of the associated media;</li>
            <li><code>Double.POSITIVE_INFINITY</code>: infinite duration;</li>
            <li>non-infinite positive: explicit duration.</li>
          </ul>
      */
      public double getValue(
        )
      {
        PdfName durationSubtype = (PdfName)getBaseDataObject().get(PdfName.S);
        if(PdfName.I.equals(durationSubtype))
          return Double.NEGATIVE_INFINITY;
        else if(PdfName.F.equals(durationSubtype))
          return Double.POSITIVE_INFINITY;
        else if(PdfName.T.equals(durationSubtype))
          return new Timespan(getBaseDataObject().get(PdfName.T)).getTime();
        else
          throw new UnsupportedOperationException("Duration subtype '" + durationSubtype + "'");
      }

      /**
        @see #getValue()
      */
      public void setValue(
        double value
        )
      {
        if(value == Double.NEGATIVE_INFINITY)
        {
          getBaseDataObject().put(PdfName.S, PdfName.I);
          getBaseDataObject().remove(PdfName.T);
        }
        else if(value == Double.POSITIVE_INFINITY)
        {
          getBaseDataObject().put(PdfName.S, PdfName.F);
          getBaseDataObject().remove(PdfName.T);
        }
        else
        {
          getBaseDataObject().put(PdfName.S, PdfName.T);
          new Timespan(getBaseDataObject().get(PdfName.T, PdfDictionary.class)).setTime(value);
        }
      }
    }

    public enum FitModeEnum
    {
      /**
        The media's width and height are scaled while preserving the aspect ratio so that the media
        and play rectangles have the greatest possible intersection while still displaying all media
        content. Same as <code>meet</code> value of SMIL's fit attribute.
      */
      Meet(new PdfInteger(0)),
      /**
        The media's width and height are scaled while preserving the aspect ratio so that the play
        rectangle is entirely filled, and the amount of media content that does not fit within the
        play rectangle is minimized. Same as <code>slice</code> value of SMIL's fit attribute.
      */
      Slice(new PdfInteger(1)),
      /**
        The media's width and height are scaled independently so that the media and play rectangles
        are the same; the aspect ratio is not necessarily preserved. Same as <code>fill</code> value
        of SMIL's fit attribute.
      */
      Fill(new PdfInteger(2)),
      /**
        The media is not scaled. A scrolling user interface is provided if the media rectangle is
        wider or taller than the play rectangle. Same as <code>scroll</code> value of SMIL's fit
        attribute.
      */
      Scroll(new PdfInteger(3)),
      /**
        The media is not scaled. Only the portions of the media rectangle that intersect the play
        rectangle are displayed. Same as <code>hidden</code> value of SMIL's fit attribute.
      */
      Hidden(new PdfInteger(4)),
      /**
        Use the player's default setting (author has no preference).
      */
      Default(new PdfInteger(5));

      private static Map<PdfInteger, FitModeEnum> map = new HashMap<PdfInteger, FitModeEnum>();

      static
      {
        for (FitModeEnum value : FitModeEnum.values())
        {map.put(value.getCode(), value);}
      }

      public static FitModeEnum valueOf(
        PdfInteger code
        )
      {return map.containsKey(code) ? map.get(code) : Default;}

      private final PdfInteger code;

      private FitModeEnum(
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
      Gets the temporal duration, corresponding to the notion of simple duration in SMIL.

      @return
        <ul>
          <li><code>Double.NEGATIVE_INFINITY</code>: intrinsic duration of the associated media;</li>
          <li><code>Double.POSITIVE_INFINITY</code>: infinite duration;</li>
          <li>non-infinite positive: explicit duration.</li>
        </ul>
    */
    public double getDuration(
      )
    {
      PdfDirectObject durationObject = getBaseDataObject().get(PdfName.D);
      return durationObject != null ? new Duration(durationObject).getValue() : Double.NEGATIVE_INFINITY;
    }

    /**
      Gets the manner in which the player should treat a visual media type that does not exactly fit
      the rectangle in which it plays.
    */
    public FitModeEnum getFitMode(
      )
    {return FitModeEnum.valueOf((PdfInteger)getBaseDataObject().get(PdfName.F));}

    /**
      Gets the number of iterations of the duration to repeat; similar to SMIL's
      <code>repeatCount</code> attribute.

      @return
        <ul>
          <li><code>0</code>: repeat forever</li>
        </ul>
    */
    public double getRepeatCount(
      )
    {return (Double)PdfReal.getValue(getBaseDataObject().get(PdfName.RC), 1d);}

    /**
      Gets the volume level as a percentage of recorded volume level. A zero value is equivalent to
      mute.
    */
    public int getVolume(
      )
    {return (Integer)PdfInteger.getValue(getBaseDataObject().get(PdfName.V), 100);}

    /**
      Gets whether the media should automatically play when activated.
    */
    public boolean isAutoplay(
      )
    {return (Boolean)PdfBoolean.getValue(getBaseDataObject().get(PdfName.A), true);}

    /**
      Gets whether to display a player-specific controller user interface (for example,
      play/pause/stop controls) when playing.
    */
    public boolean isPlayerSpecificControl(
      )
    {return (Boolean)PdfBoolean.getValue(getBaseDataObject().get(PdfName.C), false);}

    /**
      @see #isAutoplay()
    */
    public void setAutoplay(
      boolean value
      )
    {getBaseDataObject().put(PdfName.A, PdfBoolean.get(value));}

    /**
      @see #getDuration()
    */
    public void setDuration(
      double value
      )
    {getBaseDataObject().put(PdfName.D, new Duration(value).getBaseObject());}

    /**
      @see #getFitMode()
    */
    public void setFitMode(
      FitModeEnum value
      )
    {getBaseDataObject().put(PdfName.F, value != null ? value.getCode() : null);}

    /**
      @see #isPlayerSpecificControl()
    */
    public void setPlayerSpecificControl(
      boolean value
      )
    {getBaseDataObject().put(PdfName.C, PdfBoolean.get(value));}

    /**
      @see #getRepeatCount()
    */
    public void setRepeatCount(
      double value
      )
    {getBaseDataObject().put(PdfName.RC, PdfReal.get(value));}

    /**
      @see #getVolume()
    */
    public void setVolume(
      int value
      )
    {
      if(value < 0)
      {value = 0;}
      else if(value > 100)
      {value = 100;}
      getBaseDataObject().put(PdfName.V, PdfInteger.get(value));
    }
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public MediaPlayParameters(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {PdfName.Type},
        new PdfDirectObject[]
        {PdfName.MediaPlayParams}
        )
      );
  }

  MediaPlayParameters(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public MediaPlayParameters clone(
    Document context
    )
  {return (MediaPlayParameters)super.clone(context);}

  /**
    Gets the player rules for playing this media.
  */
  public MediaPlayers getPlayers(
    )
  {return MediaPlayers.wrap(getBaseDataObject().get(PdfName.PL, PdfDictionary.class));}

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
    @see #getPlayers()
  */
  public void setPlayers(
    MediaPlayers value
    )
  {getBaseDataObject().put(PdfName.PL, PdfObjectWrapper.getBaseObject(value));}

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
