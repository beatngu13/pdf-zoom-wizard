/*
  Copyright 2006-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.objects;

import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.files.File;

/**
  Abstract PDF object.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
public abstract class PdfObject
  implements Cloneable,
    IVisitable
{
  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets the clone of the specified object, registered inside the specified file context.

    @param object Object to clone into the specified file context.
    @param context File context of the cloning.
  */
  public static final PdfObject clone(
    PdfObject object,
    File context
    )
  {return object == null ? null : object.clone(context);}

  /**
    Ensures an indirect reference to be resolved into its corresponding data object.

    @param object Object to resolve.
  */
  public static PdfDataObject resolve(
    PdfObject object
    )
  {return object == null ? null : object.resolve();}

  /**
    Ensures a data object to be unresolved into its corresponding indirect reference, if available.

    @param object Object to unresolve.
    @return {@link PdfReference}, if available; <code>object</code>, otherwise.
  */
  public static PdfDirectObject unresolve(
    PdfDataObject object
    )
  {return object == null ? null : object.unresolve();}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  protected PdfObject(
    )
  {}
  // </constructors>

  // <interface>
  // <public>
  /**
    Creates a deep copy of this object within the specified file context.
  */
  public PdfObject clone(
    File context
    )
  {return accept(context.getCloner(), null);}

  /**
    Gets the indirect object containing this object.

    @see #getDataContainer()
    @see #getIndirectObject()
  */
  public PdfIndirectObject getContainer(
    )
  {
    PdfObject parent = getParent();
    return parent != null ? parent.getContainer() : null;
  }

  /**
    Gets the indirect object containing the data associated to this object.

    @see #getContainer()
    @see #getIndirectObject()
  */
  public PdfIndirectObject getDataContainer(
    )
  {
    PdfIndirectObject indirectObject = getIndirectObject();
    return indirectObject != null ? indirectObject : getContainer();
  }

  /**
    Gets the file containing this object.
  */
  public File getFile(
    )
  {
    PdfIndirectObject dataContainer = getDataContainer();
    return dataContainer != null ? dataContainer.getFile() : null;
  }

  /**
    Gets the indirect object corresponding to this object.

    @see #getContainer()
    @see #getDataContainer()
  */
  public PdfIndirectObject getIndirectObject(
    )
  {
    PdfObject parent = getParent();
    return parent instanceof PdfIndirectObject ? (PdfIndirectObject)parent : null;
  }

  /**
    Gets the parent of this object.

    @see #getContainer()
  */
  public abstract PdfObject getParent(
    );

  /**
    Gets the indirect reference of this object.
  */
  public PdfReference getReference(
    )
  {
    PdfIndirectObject indirectObject = getIndirectObject();
    return indirectObject != null ? indirectObject.getReference() : null;
  }

  /**
    Gets whether the detection of object state changes is enabled.
  */
  public abstract boolean isUpdateable(
    );

  /**
    Gets whether the initial state of this object has been modified.
  */
  public abstract boolean isUpdated(
    );

  /**
    Ensures this object to be resolved into its corresponding data object.

    @see #unresolve()
  */
  public PdfDataObject resolve(
    )
  {return this instanceof IPdfIndirectObject ? ((IPdfIndirectObject)this).getDataObject() : (PdfDataObject)this;}

  /**
    @see #isUpdateable()
  */
  public abstract void setUpdateable(
    boolean value
    );

  /**
    Swaps contents between this object and the other one.

    @param other Object whose contents have to be swapped with this one's.
    @return This object.
  */
  public abstract PdfObject swap(
    PdfObject other
    );

  /**
    Ensures this object to be unresolved into its corresponding indirect reference, if available.

    @return {@link PdfReference}, if available; <code>this</code>, otherwise.
    @see #resolve()
  */
  public PdfDirectObject unresolve(
    )
  {
    PdfReference reference = getReference();
    return reference != null ? reference : (PdfDirectObject)this;
  }

  /**
    Serializes this object to the specified stream.

    @param stream Target stream.
    @param context File context.
  */
  public abstract void writeTo(
    IOutputStream stream,
    File context
    );
  // </public>

  // <protected>
  /**
    Creates a shallow copy of this object.
  */
  @Override
  protected final Object clone(
    )
  {
    PdfObject clone;
    try
    {clone = (PdfObject)super.clone();}
    catch(CloneNotSupportedException e)
    {throw new RuntimeException(e);}
    clone.setParent(null);
    return clone;
  }

  /**
    Gets whether this object acts like a null-object placeholder.
  */
  protected abstract boolean isVirtual(
    );

  /**
    @see #isUpdated()
  */
  protected abstract void setUpdated(
    boolean value
    );

  /**
    @see #isVirtual()
  */
  protected abstract void setVirtual(
    boolean value
    );

  /**
    Updates the state of this object.
  */
  protected final void update(
    )
  {
    if(!isUpdateable() || isUpdated())
      return;

    setUpdated(true);
    setVirtual(false);

    // Propagate the update to the ascendants!
    if(getParent() != null)
    {getParent().update();}
  }
  // </protected>

  // <internal>
  /**
    Ensures that the specified object is decontextualized from this object.

    @param object Object to decontextualize from this object.
    @see #include(PdfDataObject)
  */
  final void exclude(
    PdfDataObject object
    )
  {
    if(object != null)
    {object.setParent(null);}
  }

  /**
    Ensures that the specified object is contextualized into this object.

    @param object Object to contextualize into this object; if it is already contextualized
      into another object, it will be cloned to preserve its previous association.
    @return Contextualized object.
    @see #exclude(PdfDataObject)
  */
  final PdfDataObject include(
    PdfDataObject object
    )
  {
    if(object != null)
    {
      if(object.getParent() != null)
      {object = (PdfDataObject)object.clone();}
      object.setParent(this);
    }
    return object;
  }

  /**
    @see #getParent()
  */
  abstract void setParent(
    PdfObject value
    );
  // </internal>
  // </interface>
  // </dynamic>
  // </class>
}