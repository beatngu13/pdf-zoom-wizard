/*
  Copyright 2011-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.layers;

import org.pdfclown.objects.Array;
import org.pdfclown.objects.IPdfObjectWrapper;

/**
  Optional content configuration interface [PDF:1.7:4.10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 03/12/12
*/
public interface ILayerConfiguration
  extends IPdfObjectWrapper
{
  /**
    Gets the name of the application or feature that created this configuration.
  */
  String getCreator(
    );

  /**
    Gets the layer structure.
  */
  Layers getLayers(
    );

  /**
    Gets the list mode specifying which layers should be displayed to the user.
  */
  ListModeEnum getListMode(
    );

  /**
    Gets the groups of layers whose states are intended to follow a radio button paradigm (that is
    exclusive visibility within the same group).
  */
  Array<LayerGroup> getOptionGroups(
    );

  /**
    Gets the configuration name.
  */
  String getTitle(
    );

  /**
    Gets whether all the layers in the document are initialized to be visible when this configuration
    is applied.
  */
  Boolean isVisible(
    );

  /**
    @see #getCreator()
  */
  void setCreator(
    String value
    );

  /**
    @see #getLayers()
  */
  void setLayers(
    Layers value
    );

  /**
    @see #getListMode()
  */
  void setListMode(
    ListModeEnum value
    );

  /**
    @see #getTitle()
  */
  void setTitle(
    String value
    );

  /**
    @see #isVisible()
  */
  void setVisible(
    Boolean value
    );
}
