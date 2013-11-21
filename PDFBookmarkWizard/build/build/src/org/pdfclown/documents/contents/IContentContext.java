/*
  Copyright 2006-2011 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
  Content stream context.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.1, 11/14/11
*/
public interface IContentContext
  extends IContentEntity
{
  /**
    Gets the bounding box associated with this content context either explicitly
    (directly associated to the object) or (if not explicitly available) implicitly
    (inherited from a higher level object), expressed in default user-space units.
  */
  Rectangle2D getBox(
    );

  /**
    Gets the contents collection representing the content stream associated
    with this content context.

    @since 0.0.5
  */
  Contents getContents(
    );

  /**
    Gets the resources associated with this content context either explicitly (directly
    associated to the object) or (if not explicitly available) implicitly (inherited from a
    higher-level object).
    <p>The implementing class MUST ensure that the returned object isn't <code>null</code>.</p>
  */
  Resources getResources(
    );

  /**
    Gets the rendering rotation of this content context.

    @since 0.1.0
  */
  RotationEnum getRotation(
    );

  /**
    Renders this content context into the specified rendering context.

    @param context Rendering context.
    @param size Rendering canvas size.
    @since 0.1.0
  */
  void render(
    Graphics2D context,
    Dimension2D size
    );
}