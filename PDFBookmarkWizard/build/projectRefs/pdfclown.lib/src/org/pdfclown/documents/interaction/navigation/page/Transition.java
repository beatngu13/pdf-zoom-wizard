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

package org.pdfclown.documents.interaction.navigation.page;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;

/**
  Visual transition to use when moving to a page during a presentation [PDF:1.6:8.3.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF11)
public final class Transition
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <classes>
  /**
    Transition direction (counterclockwise) [PDF:1.6:8.3.3].
  */
  public enum DirectionEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Left to right.
    */
    LeftToRight(PdfInteger.get(0)),
    /**
      Bottom to top.
    */
    BottomToTop(PdfInteger.get(90)),
    /**
      Right to left.
    */
    RightToLeft(PdfInteger.get(180)),
    /**
      Top to bottom.
    */
    TopToBottom(PdfInteger.get(270)),
    /**
      Top-left to bottom-right.
    */
    TopLeftToBottomRight(PdfInteger.get(315)),
    /**
      None.
    */
    None(PdfName.None);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the direction corresponding to the given value.
    */
    public static DirectionEnum get(
      PdfSimpleObject<?> value
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
    private final PdfSimpleObject<?> code;
    // </fields>

    // <constructors>
    private DirectionEnum(
      PdfSimpleObject<?> code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfSimpleObject<?> getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }

  /**
    Transition orientation [PDF:1.6:8.3.3].
  */
  public enum OrientationEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Horizontal.
    */
    Horizontal(PdfName.H),
    /**
      Vertical.
    */
    Vertical(PdfName.V);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the orientation corresponding to the given value.
    */
    public static OrientationEnum get(
      PdfName value
      )
    {
      for(OrientationEnum orientation : OrientationEnum.values())
      {
        if(orientation.getCode().equals(value))
          return orientation;
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
    private OrientationEnum(
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
    Transition direction on page [PDF:1.6:8.3.3].
  */
  public enum PageDirectionEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Inward (from the edges of the page).
    */
    Inward(PdfName.I),
    /**
      Outward (from the center of the page).
    */
    Outward(PdfName.O);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the page direction corresponding to the given value.
    */
    public static PageDirectionEnum get(
      PdfName value
      )
    {
      for(PageDirectionEnum direction : PageDirectionEnum.values())
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
    private PageDirectionEnum(
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
    Transition style [PDF:1.6:8.3.3].
  */
  public enum StyleEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Two lines sweep across the screen, revealing the page.
    */
    Split(PdfName.Split),
    /**
      Multiple lines sweep across the screen, revealing the page.
    */
    Blinds(PdfName.Blinds),
    /**
      A rectangular box sweeps between the edges of the page and the center.
    */
    Box(PdfName.Box),
    /**
      A single line sweeps across the screen from one edge to the other.
    */
    Wipe(PdfName.Wipe),
    /**
      The old page dissolves gradually.
    */
    Dissolve(PdfName.Dissolve),
    /**
      The old page dissolves gradually sweeping across the page in a wide band
      moving from one side of the screen to the other.
    */
    Glitter(PdfName.Glitter),
    /**
      No transition.
    */
    Replace(PdfName.R),
    /**
      Changes are flown across the screen.
    */
    @PDF(VersionEnum.PDF15)
    Fly(PdfName.Fly),
    /**
      The page slides in, pushing away the old one.
    */
    @PDF(VersionEnum.PDF15)
    Push(PdfName.Push),
    /**
      The page slides on to the screen, covering the old one.
    */
    @PDF(VersionEnum.PDF15)
    Cover(PdfName.Cover),
    /**
      The old page slides off the screen, uncovering the new one.
    */
    @PDF(VersionEnum.PDF15)
    Uncover(PdfName.Uncover),
    /**
      The new page reveals gradually.
    */
    @PDF(VersionEnum.PDF15)
    Fade(PdfName.Fade);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the style corresponding to the given value.
    */
    public static StyleEnum get(
      PdfName value
      )
    {
      for(StyleEnum style : StyleEnum.values())
      {
        if(style.getCode().equals(value))
          return style;
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
    private StyleEnum(
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
  public static Transition wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new Transition(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new action within the given document context.
  */
  public Transition(
    Document context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]{PdfName.Type},
        new PdfDirectObject[]{PdfName.Trans}
        )
      );
  }

  public Transition(
    Document context,
    StyleEnum style
    )
  {this(context, style, null, null, null, null, null);}

  public Transition(
    Document context,
    StyleEnum style,
    Double duration
    )
  {this(context, style, duration, null, null, null, null);}

  public Transition(
    Document context,
    StyleEnum style,
    Double duration,
    OrientationEnum orientation,
    PageDirectionEnum pageDirection,
    DirectionEnum direction,
    Double scale
    )
  {
    this(context);
    setStyle(style);
    setDuration(duration);
    setOrientation(orientation);
    setPageDirection(pageDirection);
    setDirection(direction);
    setScale(scale);
  }

  private Transition(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Transition clone(
    Document context
    )
  {return (Transition)super.clone(context);}

  /**
    Gets the transition direction.
  */
  public DirectionEnum getDirection(
    )
  {
    PdfSimpleObject<?> directionObject = (PdfSimpleObject<?>)getBaseDataObject().get(PdfName.Di);
    return directionObject == null
      ? DirectionEnum.LeftToRight
      : DirectionEnum.get(directionObject);
  }

  /**
    Gets the duration of the transition effect, in seconds.
  */
  public double getDuration(
    )
  {
    PdfNumber<?> durationObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.D);
    return durationObject == null
      ? 1
      : durationObject.getDoubleValue();
  }

  /**
    Gets the transition orientation.
  */
  public OrientationEnum getOrientation(
    )
  {
    PdfName orientationObject = (PdfName)getBaseDataObject().get(PdfName.Dm);
    return orientationObject == null
      ? OrientationEnum.Horizontal
      : OrientationEnum.get(orientationObject);
  }

  /**
    Gets the transition direction on page.
  */
  public PageDirectionEnum getPageDirection(
    )
  {
    PdfName pageDirectionObject = (PdfName)getBaseDataObject().get(PdfName.M);
    return pageDirectionObject == null
      ? PageDirectionEnum.Inward
      : PageDirectionEnum.get(pageDirectionObject);
  }

  /**
    Gets the scale at which the changes are drawn.
  */
  @PDF(VersionEnum.PDF15)
  public double getScale(
    )
  {
    PdfNumber<?> scaleObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.SS);
    return scaleObject == null
      ? 1
      : scaleObject.getDoubleValue();
  }

  /**
    Gets the transition style.
  */
  public StyleEnum getStyle(
    )
  {
    PdfName styleObject = (PdfName)getBaseDataObject().get(PdfName.S);
    return styleObject == null
      ? StyleEnum.Replace
      : StyleEnum.get(styleObject);
  }

  /**
    @see #getDirection()
  */
  public void setDirection(
    DirectionEnum value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.Di);}
    else
    {getBaseDataObject().put(PdfName.Di,value.getCode());}
  }

  /**
    @see #getDuration()
  */
  public void setDuration(
    Double value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.D);}
    else
    {getBaseDataObject().put(PdfName.D,PdfReal.get(value));}
  }

  /**
    @see #getOrientation()
  */
  public void setOrientation(
    OrientationEnum value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.Dm);}
    else
    {getBaseDataObject().put(PdfName.Dm,value.getCode());}
  }

  /**
    @see #getPageDirection()
  */
  public void setPageDirection(
    PageDirectionEnum value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.M);}
    else
    {getBaseDataObject().put(PdfName.M,value.getCode());}
  }

  /**
    @see #getScale()
  */
  public void setScale(
    Double value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.SS);}
    else
    {getBaseDataObject().put(PdfName.SS,PdfReal.get(value));}
  }

  /**
    @see #getStyle()
  */
  public void setStyle(
    StyleEnum value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.S);}
    else
    {getBaseDataObject().put(PdfName.S,value.getCode());}
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}