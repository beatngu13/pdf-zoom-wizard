/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

  Contributors:
    * Manuel Guilbault (original .NET/C# code developer, manuel.guilbault@gmail.com)
    * Stefano Chizzolini (source code porting to Java and enhancement, http://www.stefanochizzolini.it)

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

package org.pdfclown.documents.contents.composition;

/**
  Line alignment.

  @author Manuel Guilbault (manuel.guilbault@gmail.com)
  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 01/20/12
*/
public enum LineAlignmentEnum
{
  /**
    The top of the element is aligned with the top of the tallest element on the line.
  */
  Top,
  /**
    Aligns the baseline of the element with the baseline of the parent element. This is default.
  */
  BaseLine,
  /**
    The element is placed in the middle of the parent element.
  */
  Middle,
  /**
    The bottom of the element is aligned with the lowest element on the line.
  */
  Bottom,
  /**
    Aligns the element as it was superscript.
  */
  Super,
  /**
    Aligns the element as it was subscript.
  */
  Sub
}
