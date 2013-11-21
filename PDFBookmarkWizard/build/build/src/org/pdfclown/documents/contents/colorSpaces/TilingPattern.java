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

package org.pdfclown.documents.contents.colorSpaces;

import java.awt.geom.Rectangle2D;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfStream;

/**
  Pattern consisting of a small graphical figure called <i>pattern cell</i> [PDF:1.6:4.6.2].
  <p>Painting with the pattern replicates the cell at fixed horizontal and vertical intervals
  to fill an area.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/21/12
*/
//TODO: define as IContentContext?
@PDF(VersionEnum.PDF12)
public class TilingPattern
  extends Pattern<PdfStream>
{
  // <class>
  // <classes>
  /**
    Uncolored tiling pattern ("stencil") associated to a color.
  */
  public static final class Colorized
    extends TilingPattern
  {
    private final Color<?> color;

    private Colorized(
      TilingPattern uncoloredPattern,
      Color<?> color
      )
    {
      super(
        (PatternColorSpace)uncoloredPattern.getColorSpace(),
        uncoloredPattern.getBaseObject()
        );

      this.color = color;
    }

    /**
      Gets the color applied to the stencil.
    */
    public Color<?> getColor(
      )
    {return color;}
  }

  /**
    Pattern cell color mode.
  */
  public enum PaintTypeEnum
  {
    /**
      The pattern's content stream <i>specifies the colors used to paint the pattern cell</i>.
      <p>When the content stream begins execution, the current color is the one
      that was initially in effect in the pattern's parent content stream.</p>
    */
    Colored(1),
    /**
      The pattern's content stream <i>does NOT specify any color information</i>.
      <p>Instead, the entire pattern cell is painted with a separately specified color
      each time the pattern is used; essentially, the content stream describes a <i>stencil</i>
      through which the current color is to be poured.</p>
      <p>The content stream must not invoke operators that specify colors
      or other color-related parameters in the graphics state.</p>
     */
    Uncolored(2);

    public static PaintTypeEnum get(
      int code
      )
    {
      for(PaintTypeEnum paintType : values())
        if(paintType.getCode() == code)
          return paintType;
      return null;
    }

    private int code;

    private PaintTypeEnum(
      int code
      )
    {this.code = code;}

    public int getCode(
      )
    {return code;}
  }

  /**
    Spacing adjustment of tiles relative to the device pixel grid.
  */
  public enum TilingTypeEnum
  {
    /**
      Pattern cells are <i>spaced consistently</i>, that is by a multiple of a device pixel.
    */
    ConstantSpacing(1),
    /**
      The pattern cell is not distorted, but <i>the spacing between pattern cells
      may vary</i> by as much as 1 device pixel, both horizontally and vertically,
      when the pattern is painted.
    */
    VariableSpacing(2),
    /**
      Pattern cells are <i>spaced consistently</i> as in tiling type 1
      but with additional distortion permitted to enable a more efficient implementation.
    */
    FasterConstantSpacing(3);

    public static TilingTypeEnum get(
      int code
      )
    {
      for(TilingTypeEnum tilingType : values())
        if(tilingType.getCode() == code)
          return tilingType;
      return null;
    }

    private int code;

    private TilingTypeEnum(
      int code
      )
    {this.code = code;}

    public int getCode(
      )
    {return code;}
  }
  // </classes>

  // <dynamic>
  // <constructors>
  TilingPattern(
    PatternColorSpace colorSpace,
    PdfDirectObject baseObject
    )
  {super(colorSpace, baseObject);}

  TilingPattern(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the colorized representation of this pattern.

    @param color Color to be applied to the pattern.
    @throws UnsupportedOperationException In case this isn't an <i>uncolored tiling pattern</i>.
   */
  public Colorized colorize(
    Color<?> color
    )
  {
    if(getPaintType() != PaintTypeEnum.Uncolored)
      throw new UnsupportedOperationException("Only uncolored tiling patterns can be colorized.");

    return new Colorized(this, color);
  }

  /**
    Gets the pattern cell's bounding box (expressed in the pattern coordinate system)
    used to clip the pattern cell.
  */
  public Rectangle2D getBox(
    )
  {
    /*
      NOTE: 'BBox' entry MUST be defined.
    */
    org.pdfclown.objects.Rectangle box = org.pdfclown.objects.Rectangle.wrap(getBaseDataObject().getHeader().get(PdfName.BBox));
    return new Rectangle2D.Double(box.getX(), box.getY(), box.getWidth(), box.getHeight());
  }

  /**
    Gets how the color of the pattern cell is to be specified.
  */
  public PaintTypeEnum getPaintType(
    )
  {return PaintTypeEnum.get(((PdfInteger)getBaseDataObject().getHeader().get(PdfName.PaintType)).getRawValue());}

  /**
    Gets the named resources required by the pattern's content stream.
  */
  public Resources getResources(
    )
  {return Resources.wrap(getBaseDataObject().getHeader().get(PdfName.Resources));}

  /**
    Gets how to adjust the spacing of tiles relative to the device pixel grid.
  */
  public TilingTypeEnum getTilingType(
    )
  {return TilingTypeEnum.get(((PdfInteger)getBaseDataObject().getHeader().get(PdfName.TilingType)).getRawValue());}

  /**
    Gets the horizontal spacing between pattern cells (expressed in the pattern coordinate system).
  */
  public double getXStep(
    )
  {return ((PdfNumber<?>)getBaseDataObject().getHeader().get(PdfName.XStep)).getDoubleValue();}

  /**
    Gets the vertical spacing between pattern cells (expressed in the pattern coordinate system).
  */
  public double getYStep(
    )
  {return ((PdfNumber<?>)getBaseDataObject().getHeader().get(PdfName.YStep)).getDoubleValue();}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
