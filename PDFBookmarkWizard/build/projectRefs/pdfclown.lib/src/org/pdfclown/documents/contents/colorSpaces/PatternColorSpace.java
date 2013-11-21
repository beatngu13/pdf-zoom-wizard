/*
  Copyright 2010-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Paint;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.colorSpaces.TilingPattern.PaintTypeEnum;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.util.NotImplementedException;

/**
  Pattern color space [PDF:1.6:4.5.5].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 04/10/11
*/
@PDF(VersionEnum.PDF12)
public final class PatternColorSpace
  extends SpecialColorSpace<PdfDirectObject>
{
  // <class>
  // <static>
  // <fields>
  /*
    NOTE: In case of no parameters, it may be specified directly (i.e. without being defined
    in the ColorSpace subdictionary of the contextual resource dictionary) [PDF:1.6:4.5.7].
  */
  //TODO:verify parameters!!!
  public static final PatternColorSpace Default = new PatternColorSpace(null);
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  //TODO:IMPL new element constructor!

  PatternColorSpace(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public Color<?> getColor(
    List<PdfDirectObject> components,
    IContentContext context
    )
  {
    Pattern<?> pattern = context.getResources().getPatterns().get(components.get(components.size()-1));
    if(pattern instanceof TilingPattern)
    {
      TilingPattern tilingPattern = (TilingPattern)pattern;
      if(tilingPattern.getPaintType() == PaintTypeEnum.Uncolored)
      {
        ColorSpace<?> underlyingColorSpace = getUnderlyingColorSpace();
        if(underlyingColorSpace == null)
          throw new IllegalArgumentException("Uncolored tiling patterns not supported by this color space because no underlying color space has been defined.");

        // Get the color to be used for colorizing the uncolored tiling pattern!
        Color<?> color = underlyingColorSpace.getColor(components, context);
        // Colorize the uncolored tiling pattern!
        pattern = tilingPattern.colorize(color);
      }
    }
    return pattern;
  }

  @Override
  public int getComponentCount(
    )
  {return 0;}

  @Override
  public Pattern<?> getDefaultColor(
    )
  {return Pattern.Default;}

  @Override
  public Paint getPaint(
    Color<?> color
    )
  {
    // FIXME: Auto-generated method stub
    return null;
  }

  /**
    Gets the color space in which the actual color of the {@link Pattern pattern} is to be specified.
    <p>This feature is <i>applicable to {@link TilingPattern uncolored tiling patterns} only</i>.</p>
  */
  public ColorSpace<?> getUnderlyingColorSpace(
    )
  {
    PdfDirectObject baseDataObject = getBaseDataObject();
    if(baseDataObject instanceof PdfArray)
    {
      PdfArray baseArrayObject = (PdfArray)baseDataObject;
      if(baseArrayObject.size() > 1)
        return ColorSpace.wrap(baseArrayObject.get(1));
    }
    return null;
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
