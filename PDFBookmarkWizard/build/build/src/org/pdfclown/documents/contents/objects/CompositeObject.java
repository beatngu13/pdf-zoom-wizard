/*
  Copyright 2007-2011 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.contents.objects;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.ContentScanner.GraphicsState;

/**
  Composite object. It is made up of multiple content objects.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.4
  @version 0.1.1, 11/01/11
*/
@PDF(VersionEnum.PDF10)
public abstract class CompositeObject
  extends ContentObject
{
  // <class>
  // <dynamic>
  // <fields>
  protected List<ContentObject> objects;
  // </fields>

  // <constructors>
  protected CompositeObject(
    )
  {this.objects = new ArrayList<ContentObject>();}

  protected CompositeObject(
    ContentObject object
    )
  {
    this();
    objects.add(object);
  }

  protected CompositeObject(
    ContentObject... objects
    )
  {
    this();
    for(ContentObject object : objects)
    {this.objects.add(object);}
  }

  protected CompositeObject(
    List<ContentObject> objects
    )
  {this.objects = objects;}
  // </constructors>

  // <interface>
  // <public>
  /**
    Gets the object header.
  */
  public Operation getHeader(
    )
  {return null;}

  /**
    Gets the list of inner objects.
  */
  public List<ContentObject> getObjects(
    )
  {return objects;}

  @Override
  public void scan(
    GraphicsState state
    )
  {
    ContentScanner childLevel = state.getScanner().getChildLevel();

    if(!render(state))
    {childLevel.moveEnd();} // Forces the current object to its final graphics state.

    childLevel.getState().copyTo(state); // Copies the current object's final graphics state to the current level's.
  }

  /**
    @see #getHeader()
  */
  public void setHeader(
    Operation value
    )
  {throw new UnsupportedOperationException();}

  @Override
  public String toString(
    )
  {return "{" + objects.toString() + "}";}

  @Override
  public void writeTo(
    IOutputStream stream,
    Document context
    )
  {
    for(ContentObject object : objects)
    {object.writeTo(stream, context);}
  }
  // </public>

  // <protected>
  /**
    Creates the rendering object corresponding to this container.
  */
  protected Shape createRenderObject(
    )
  {return null;}

  /**
    Renders this container.

    @param state Graphics state.
    @return Whether the rendering has been executed.
   */
  protected boolean render(
    GraphicsState state
    )
  {
    ContentScanner scanner = state.getScanner();
    Graphics2D context = scanner.getRenderContext();
    if(context == null)
      return false;

    // Render the inner elements!
    scanner.getChildLevel().render(
      context,
      scanner.getCanvasSize(),
      createRenderObject()
      );
    return true;
  }
  // </protected>
  // </interface>
  // </dynamic>
  // </class>
}