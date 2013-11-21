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

import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.NotImplementedException;

/**
  Paint that consists of a repeating graphical figure or a smoothly varying color gradient
  instead of a simple color [PDF:1.6:4.6].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF12)
public abstract class Pattern<TDataObject extends PdfDataObject>
  extends Color<TDataObject>
{
  // <class>
  // <static>
  // <fields>
  //TODO:verify!
  public static final Pattern<?> Default = new TilingPattern(null);

  private static final int PatternType1 = 1;
  private static final int PatternType2 = 2;
  // </fields>

  // <interface>
  // <public>
  /**
    Wraps the specified base object into a pattern object.

    @param baseObject Base object of a pattern object.
    @return Pattern object corresponding to the base object.
  */
  public static Pattern<?> wrap(
    PdfDirectObject baseObject
    )
  {
    if(baseObject == null)
      return null;

    PdfDataObject dataObject = baseObject.resolve();
    PdfDictionary dictionary = getDictionary(dataObject);
    int patternType = ((PdfInteger)dictionary.get(PdfName.PatternType)).getRawValue();
    switch(patternType)
    {
      case PatternType1:
        return new TilingPattern(baseObject);
      case PatternType2:
        return new ShadingPattern(baseObject);
      default:
        throw new UnsupportedOperationException("Pattern type " + patternType + " unknown.");
    }
  }
  // </public>

  // <private>
  /**
    Gets a pattern's dictionary.

    @param patternDataObject Pattern data object.
  */
  private static final PdfDictionary getDictionary(
    PdfDataObject patternDataObject
    )
  {
    if(patternDataObject instanceof PdfDictionary)
      return (PdfDictionary)patternDataObject;
    else // MUST be PdfStream.
      return ((PdfStream)patternDataObject).getHeader();
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  //TODO:verify (colorspace is available or may be implicit?)
  protected Pattern(
    PdfDirectObject baseObject
    )
  {super(baseObject);}

  //TODO:verify (colorspace is available or may be implicit?)
  protected Pattern(
    PatternColorSpace colorSpace,
    PdfDirectObject baseObject
    )
  {super(colorSpace, baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Object clone(
    Document context
    )
  {throw new NotImplementedException();}

  @Override
  public List<PdfDirectObject> getComponents(
    )
  {return new ArrayList<PdfDirectObject>();}//TODO:verify (see SetFillColor/SetStrokeColor -- name!)!

  /**
    Gets the <b>pattern matrix</b>, a transformation matrix that <i>maps the pattern's
    internal coordinate system to the default coordinate system of the pattern's
    parent content stream</i> (the content stream in which the pattern is defined as a resource).
    <p>The concatenation of the pattern matrix with that of the parent content stream establishes
    the pattern coordinate space, within which all graphics objects in the pattern are interpreted.</p>
  */
  public double[] getMatrix(
    )
  {
    /*
      NOTE: Pattern-space-to-user-space matrix is identity [1 0 0 1 0 0] by default.
    */
    PdfArray matrix = (PdfArray)getDictionary().get(PdfName.Matrix);
    if(matrix == null)
      return new double[]
        {
          1, // a.
          0, // b.
          0, // c.
          1, // d.
          0, // e.
          0 // f.
        };
    else
      return new double[]
        {
          ((PdfNumber<?>)matrix.get(0)).getDoubleValue(), // a.
          ((PdfNumber<?>)matrix.get(1)).getDoubleValue(), // b.
          ((PdfNumber<?>)matrix.get(2)).getDoubleValue(), // c.
          ((PdfNumber<?>)matrix.get(3)).getDoubleValue(), // d.
          ((PdfNumber<?>)matrix.get(4)).getDoubleValue(), // e.
          ((PdfNumber<?>)matrix.get(5)).getDoubleValue() // f.
        };
  }
  // </public>

  // <protected>
  /**
    Gets this pattern's dictionary.
  */
  protected final PdfDictionary getDictionary(
    )
  {return getDictionary(getBaseDataObject());}
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}
