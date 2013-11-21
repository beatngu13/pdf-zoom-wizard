/*
  Copyright 2009-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfBoolean;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfSimpleObject;

/**
  Graphics state parameters [PDF:1.6:4.3.4].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.8
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class ExtGState
  extends PdfObjectWrapper<PdfDictionary>
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Wraps the specified base object into a graphics state parameter dictionary object.

    @param baseObject Base object of a graphics state parameter dictionary object.
    @return Graphics state parameter dictionary object corresponding to the base object.
  */
  public static ExtGState wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new ExtGState(baseObject) : null;}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  public ExtGState(
    Document context
    )
  {super(context, new PdfDictionary());}

  ExtGState(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  public void applyTo(
    GraphicsState state
    )
  {
    for(PdfName parameterName : getBaseDataObject().keySet())
    {
      if(parameterName.equals(PdfName.Font))
      {
        state.setFont(getFont());
        state.setFontSize(getFontSize());
      }
      else if(parameterName.equals(PdfName.LC))
      {state.setLineCap(getLineCap());}
      else if(parameterName.equals(PdfName.D))
      {state.setLineDash(getLineDash());}
      else if(parameterName.equals(PdfName.LJ))
      {state.setLineJoin(getLineJoin());}
      else if(parameterName.equals(PdfName.LW))
      {state.setLineWidth(getLineWidth());}
      else if(parameterName.equals(PdfName.ML))
      {state.setMiterLimit(getMiterLimit());}
      else if(parameterName.equals(PdfName.BM))
      {state.setBlendMode(getBlendMode());}
      //TODO:extend supported parameters!!!
    }
  }

  @Override
  public ExtGState clone(
    Document context
    )
  {return (ExtGState)super.clone(context);}

  /**
    Gets the blend mode to be used in the transparent imaging model [PDF:1.7:7.2.4].
  */
  @PDF(VersionEnum.PDF14)
  public List<BlendModeEnum> getBlendMode(
    )
  {
    PdfDirectObject blendModeObject = getBaseDataObject().get(PdfName.BM);
    if(blendModeObject == null)
      return Collections.emptyList();

    List<BlendModeEnum> blendMode = new ArrayList<BlendModeEnum>();
    if(blendModeObject instanceof PdfName)
    {blendMode.add(BlendModeEnum.get((PdfName)blendModeObject));}
    else // MUST be an array.
    {
      for(PdfDirectObject alternateBlendModeObject : (PdfArray)blendModeObject)
      {blendMode.add(BlendModeEnum.get((PdfName)alternateBlendModeObject));}
    }
    return blendMode;
  }

  /**
    Gets the nonstroking alpha constant, specifying the constant shape or constant opacity value to
    be used for nonstroking operations in the transparent imaging model [PDF:1.7:7.2.6].
  */
  @PDF(VersionEnum.PDF14)
  public Double getFillAlpha(
    )
  {return (Double)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.ca));}

  @PDF(VersionEnum.PDF13)
  public Font getFont(
    )
  {
    PdfArray fontObject = (PdfArray)getBaseDataObject().get(PdfName.Font);
    return fontObject != null ? Font.wrap(fontObject.get(0)) : null;
  }

  @PDF(VersionEnum.PDF13)
  public Double getFontSize(
    )
  {
    PdfArray fontObject = (PdfArray)getBaseDataObject().get(PdfName.Font);
    return fontObject != null ? ((PdfNumber<?>)fontObject.get(1)).getDoubleValue() : null;
  }

  @PDF(VersionEnum.PDF13)
  public LineCapEnum getLineCap(
    )
  {
    PdfInteger lineCapObject = (PdfInteger)getBaseDataObject().get(PdfName.LC);
    return lineCapObject != null ? LineCapEnum.valueOf(lineCapObject.getRawValue()) : null;
  }

  @PDF(VersionEnum.PDF13)
  public LineDash getLineDash(
    )
  {
    PdfArray lineDashObject = (PdfArray)getBaseDataObject().get(PdfName.D);
    if(lineDashObject == null)
      return null;

    double[] dashArray;
    {
      PdfArray baseDashArray = (PdfArray)lineDashObject.get(0);
      dashArray = new double[baseDashArray.size()];
      for(
        int index = 0,
          length = dashArray.length;
        index < length;
        index++
        )
      {dashArray[index] = ((PdfNumber<?>)baseDashArray.get(index)).getDoubleValue();}
    }
    double dashPhase = ((PdfNumber<?>)lineDashObject.get(1)).getDoubleValue();
    return new LineDash(dashArray, dashPhase);
  }

  @PDF(VersionEnum.PDF13)
  public LineJoinEnum getLineJoin(
    )
  {
    PdfInteger lineJoinObject = (PdfInteger)getBaseDataObject().get(PdfName.LJ);
    return lineJoinObject != null ? LineJoinEnum.valueOf(lineJoinObject.getRawValue()) : null;
  }

  @PDF(VersionEnum.PDF13)
  public Double getLineWidth(
    )
  {
    PdfNumber<?> lineWidthObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.LW);
    return lineWidthObject != null ? lineWidthObject.getDoubleValue() : null;
  }

  @PDF(VersionEnum.PDF13)
  public Double getMiterLimit(
    )
  {
    PdfNumber<?> miterLimitObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.ML);
    return miterLimitObject != null ? miterLimitObject.getDoubleValue() : null;
  }

  /**
    Gets the stroking alpha constant, specifying the constant shape or constant opacity value to be
    used for stroking operations in the transparent imaging model [PDF:1.7:7.2.6].
  */
  @PDF(VersionEnum.PDF14)
  public Double getStrokeAlpha(
    )
  {return (Double)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.CA));}

  /**
    Gets whether the current soft mask and alpha constant are to be interpreted as shape values
    instead of opacity values.
  */
  @PDF(VersionEnum.PDF14)
  public boolean isAlphaShape(
    )
  {return (Boolean)PdfSimpleObject.getValue(getBaseDataObject().get(PdfName.AIS), false);}

  /**
    @see #isAlphaShape()
  */
  public void setAlphaShape(
    boolean value
    )
  {getBaseDataObject().put(PdfName.AIS, PdfBoolean.get(value));}

  /**
    @see #getBlendMode()
  */
  public void setBlendMode(
    List<BlendModeEnum> value
    )
  {
    PdfDirectObject blendModeObject;
    if(value == null || value.isEmpty())
    {blendModeObject = null;}
    else if(value.size() == 1)
    {blendModeObject = value.get(0).getCode();}
    else
    {
      PdfArray blendModeArray = new PdfArray();
      for(BlendModeEnum blendMode : value)
      {blendModeArray.add(blendMode.getCode());}
      blendModeObject = blendModeArray;
    }
    getBaseDataObject().put(PdfName.BM, blendModeObject);
  }

  /**
    @see #getBlendMode()
  */
  public void setBlendMode(
    BlendModeEnum... value
    )
  {setBlendMode(Arrays.asList(value));}

  /**
    @see #getFillAlpha()
  */
  public void setFillAlpha(
    Double value
    )
  {getBaseDataObject().put(PdfName.ca, PdfReal.get(value));}

  /**
    @see #getFont()
  */
  public void setFont(
    Font value
    )
  {
    PdfArray fontObject = (PdfArray)getBaseDataObject().get(PdfName.Font);
    if(fontObject == null)
    {fontObject = new PdfArray(PdfObjectWrapper.getBaseObject(value), PdfInteger.Default);}
    else
    {fontObject.set(0, PdfObjectWrapper.getBaseObject(value));}
    getBaseDataObject().put(PdfName.Font, fontObject);
  }

  /**
    @see #getFontSize()
  */
  public void setFontSize(
    Double value
    )
  {
    PdfArray fontObject = (PdfArray)getBaseDataObject().get(PdfName.Font);
    if(fontObject == null)
    {fontObject = new PdfArray(null, PdfReal.get(value));}
    else
    {fontObject.set(1, PdfReal.get(value));}
    getBaseDataObject().put(PdfName.Font, fontObject);
  }

  /**
    @see #getLineCap()
  */
  public void setLineCap(
    LineCapEnum value
    )
  {getBaseDataObject().put(PdfName.LC, value != null ? PdfInteger.get(value.getCode()) : null);}

  /**
    @see #getLineDash()
  */
  public void setLineDash(
    LineDash value
    )
  {
    PdfArray lineDashObject = new PdfArray();
    {
      PdfArray dashArrayObject = new PdfArray();
      for(double dashArrayItem : value.getDashArray())
      {dashArrayObject.add(PdfReal.get(dashArrayItem));}
      lineDashObject.add(dashArrayObject);
      lineDashObject.add(PdfReal.get(value.getDashPhase()));
    }
    getBaseDataObject().put(PdfName.D, lineDashObject);
  }

  /**
    @see #getLineJoin()
  */
  public void setLineJoin(
    LineJoinEnum value
    )
  {getBaseDataObject().put(PdfName.LJ, value != null ? PdfInteger.get(value.getCode()) : null);}

  /**
    @see #getLineWidth()
  */
  public void setLineWidth(
    Double value
    )
  {getBaseDataObject().put(PdfName.LW, PdfReal.get(value));}

  /**
    @see #getMiterLimit()
  */
  public void setMiterLimit(
    Double value
    )
  {getBaseDataObject().put(PdfName.ML, PdfReal.get(value));}

  /**
    @see #getStrokeAlpha()
  */
  public void setStrokeAlpha(
    Double value
    )
  {getBaseDataObject().put(PdfName.CA, PdfReal.get(value));}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}