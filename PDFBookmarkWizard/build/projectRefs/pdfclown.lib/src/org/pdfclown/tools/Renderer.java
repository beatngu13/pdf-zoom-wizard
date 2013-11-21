/*
  Copyright 2010 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.tools;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.IContentContext;

/**
  Tool for rendering {@link IContentContext content contexts}.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.0
  @version 0.1.0
*/
public final class Renderer
{
  /**
    Prints the specified document.
    <p>The <code>document</code> can be either a {@link Document} object
    or a {@link Book combination} of {@link Page} objects.</p>

    @param document Document to print.
    @return Whether the print was fulfilled.
    @throws PrinterException
  */
  public boolean print(
    Pageable document
    ) throws PrinterException
  {return print(document, true);}

  /**
    Prints the specified document.
    <p>The <code>document</code> can be either a {@link Document} object
    or a {@link Book combination} of {@link Page} objects.</p>

    @param document Document to print.
    @param silent Whether to avoid showing a print dialog.
    @return Whether the print was fulfilled.
    @throws PrinterException
  */
  public boolean print(
    Pageable document,
    boolean silent
    ) throws PrinterException
  {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPageable(document);
    if(!(silent || printJob.printDialog()))
      return false;

    printJob.print();
    return true;
  }

  /**
    Renders the specified contents into an image context.

    @param contents Source contents.
    @param size Image size expressed in device-space units (that is typically pixels).
    @return Image representing the rendered contents.
   */
  public BufferedImage render(
    Contents contents,
    Dimension2D size
    )
  {return render(contents, size, null);}

  /**
    Renders the specified content context into an image context.

    @param contentContext Source content context.
    @param size Image size expressed in device-space units (that is typically pixels).
    @return Image representing the rendered contents.
   */
  public BufferedImage render(
    IContentContext contentContext,
    Dimension2D size
    )
  {return render(contentContext, size, null);}

  /**
    Renders the specified contents into an image context.

    @param contents Source contents.
    @param size Image size expressed in device-space units (that is typically pixels).
    @param area Content area to render; <code>null</code> corresponds to the entire {@link IContentContext#getBox() content bounding box}.
    @return Image representing the rendered contents.
   */
  public BufferedImage render(
    Contents contents,
    Dimension2D size,
    Rectangle2D area
    )
  {return render(contents.getContentContext(), size, area);}

  /**
    Renders the specified content context into an image context.

    @param contentContext Source content context.
    @param size Image size expressed in device-space units (that is typically pixels).
    @param area Content area to render; <code>null</code> corresponds to the entire {@link IContentContext#getBox() content bounding box}.
    @return Image representing the rendered contents.
   */
  public BufferedImage render(
    IContentContext contentContext,
    Dimension2D size,
    Rectangle2D area
    )
  {
    //TODO:area!
    BufferedImage image = new BufferedImage(
      (int)size.getWidth(),
      (int)size.getHeight(),
      BufferedImage.TYPE_INT_BGR
      );
    contentContext.render(image.createGraphics(),size);
    return image;
  }
}
