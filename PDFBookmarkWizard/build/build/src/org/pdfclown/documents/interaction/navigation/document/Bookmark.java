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

package org.pdfclown.documents.interaction.navigation.document;

import java.util.EnumSet;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.interaction.ILink;
import org.pdfclown.documents.interaction.actions.Action;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfTextString;

/**
  Outline item [PDF:1.6:8.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF10)
public final class Bookmark
  extends PdfObjectWrapper<PdfDictionary>
  implements ILink
{
  // <class>
  // <classes>
  /**
    Bookmark flags [PDF:1.6:8.2.2].

    @since 0.1.0
  */
  @PDF(VersionEnum.PDF14)
  public enum FlagsEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Display the item in italic.
    */
    Italic(0x1),
    /**
      Display the item in bold.
    */
    Bold(0x2);
    // </fields>

    // <interface>
    // <public>
    /**
      Converts an enumeration set into its corresponding bit mask representation.
    */
    public static int toInt(
      EnumSet<FlagsEnum> flags
      )
    {
      int flagsMask = 0;
      for(FlagsEnum flag : flags)
      {flagsMask |= flag.getCode();}

      return flagsMask;
    }

    /**
      Converts a bit mask into its corresponding enumeration representation.
    */
    public static EnumSet<FlagsEnum> toEnumSet(
      int flagsMask
      )
    {
      EnumSet<FlagsEnum> flags = EnumSet.noneOf(FlagsEnum.class);
      for(FlagsEnum flag : FlagsEnum.values())
      {
        if((flagsMask & flag.getCode()) > 0)
        {flags.add(flag);}
      }

      return flags;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final int code;
    // </fields>

    // <constructors>
    private FlagsEnum(
      int code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public int getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <dynamic>
  // <constructors>
  public Bookmark(
    Document context,
    String title
    )
  {
    super(context, new PdfDictionary());
    setTitle(title);
  }

  public Bookmark(
    Document context,
    String title,
    LocalDestination destination
    )
  {
    this(context,title);
    setDestination(destination);
  }

  public Bookmark(
    Document context,
    String title,
    Action action
    )
  {
    this(context,title);
    setAction(action);
  }

  Bookmark(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Bookmark clone(
    Document context
    )
  {return (Bookmark)super.clone(context);}

  /**
    Gets the child bookmarks.
  */
  public Bookmarks getBookmarks(
    )
  {return Bookmarks.wrap(getBaseObject());}

  /**
    Gets the bookmark text color.

    @since 0.1.0
  */
  @PDF(VersionEnum.PDF14)
  public DeviceRGBColor getColor(
    )
  {return DeviceRGBColor.get((PdfArray)getBaseDataObject().get(PdfName.C));}

  /**
    Gets the bookmark flags.

    @since 0.1.0
  */
  @PDF(VersionEnum.PDF14)
  public EnumSet<FlagsEnum> getFlags(
    )
  {
    PdfInteger flagsObject = (PdfInteger)getBaseDataObject().get(PdfName.F);
    if(flagsObject == null)
      return EnumSet.noneOf(FlagsEnum.class);

    return FlagsEnum.toEnumSet(flagsObject.getRawValue());
  }

  /**
    Gets the parent bookmark.
  */
  public Bookmark getParent(
    )
  {
    PdfReference reference = (PdfReference)getBaseDataObject().get(PdfName.Parent);
    // Is its parent a bookmark?
    /*
      NOTE: the Title entry can be used as a flag to distinguish bookmark
      (outline item) dictionaries from outline (root) dictionaries.
    */
    if(((PdfDictionary)reference.getDataObject()).containsKey(PdfName.Title)) // Bookmark.
      return new Bookmark(reference);
    else // Outline root.
      return null; // NO parent bookmark.
  }

  /**
    Gets the text to be displayed for this bookmark.
  */
  public String getTitle(
    )
  {return ((PdfTextString)getBaseDataObject().get(PdfName.Title)).getValue();}

  /**
    Gets whether this bookmark's children are displayed.
  */
  public boolean isExpanded(
    )
  {
    PdfInteger countObject = (PdfInteger)getBaseDataObject().get(PdfName.Count);

    return (countObject == null
        || countObject.getRawValue() >= 0);
  }

  /**
    @see #getColor()
  */
  public void setColor(
    DeviceRGBColor value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.C);}
    else
    {
      checkCompatibility("color");
      getBaseDataObject().put(PdfName.C, value.getBaseObject());
    }
  }

  /**
    @see #isExpanded()
  */
  public void setExpanded(
    boolean value
    )
  {
    PdfInteger countObject = (PdfInteger)getBaseDataObject().get(PdfName.Count);
    if(countObject == null)
      return;

    /*
      NOTE: Positive Count entry means open, negative Count entry means closed [PDF:1.6:8.2.2].
    */
    getBaseDataObject().put(
      PdfName.Count,
      PdfInteger.get((value ? 1 : -1) * Math.abs(countObject.getValue()))
      );
  }

  /**
    @see #getFlags()
  */
  public void setFlags(
    EnumSet<FlagsEnum> value
    )
  {
    if(value.isEmpty())
    {getBaseDataObject().remove(PdfName.F);}
    else
    {
      checkCompatibility(value);
      getBaseDataObject().put(PdfName.F, PdfInteger.get(FlagsEnum.toInt(value)));
    }
  }

  /**
    @see #getTitle()
  */
  public void setTitle(
    String value
    )
  {getBaseDataObject().put(PdfName.Title,new PdfTextString(value));}

  // <ILink>
  @Override
  public PdfObjectWrapper<?> getTarget(
    )
  {
    if(getBaseDataObject().containsKey(PdfName.Dest))
      return getDestination();
    else if(getBaseDataObject().containsKey(PdfName.A))
      return getAction();
    else
      return null;
  }

  @Override
  public void setTarget(
    PdfObjectWrapper<?> value
    )
  {
    if(value instanceof Destination)
    {setDestination((Destination)value);}
    else if(value instanceof Action)
    {setAction((Action)value);}
    else
      throw new IllegalArgumentException("It MUST be either a Destination or an Action.");
  }
  // </ILink>
  // </public>

  // <private>
  private Action getAction(
    )
  {return Action.wrap(getBaseDataObject().get(PdfName.A));}

  private Destination getDestination(
    )
  {
    PdfDirectObject destinationObject = getBaseDataObject().get(PdfName.Dest);
    return destinationObject != null
      ? getDocument().resolveName(
        LocalDestination.class,
        destinationObject
        )
      : null;
  }

  private void setAction(
    Action value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.A);}
    else
    {
      /*
        NOTE: This entry is not permitted in bookmarks if a 'Dest' entry already exists.
      */
      if(getBaseDataObject().containsKey(PdfName.Dest))
      {getBaseDataObject().remove(PdfName.Dest);}

      getBaseDataObject().put(PdfName.A,value.getBaseObject());
    }
  }

  private void setDestination(
    Destination value
    )
  {
    if(value == null)
    {getBaseDataObject().remove(PdfName.Dest);}
    else
    {
      /*
        NOTE: This entry is not permitted in bookmarks if an 'A' entry already exists.
      */
      if(getBaseDataObject().containsKey(PdfName.A))
      {getBaseDataObject().remove(PdfName.A);}

      getBaseDataObject().put(PdfName.Dest,value.getNamedBaseObject());
    }
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}