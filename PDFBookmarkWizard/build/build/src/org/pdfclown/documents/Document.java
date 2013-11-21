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

package org.pdfclown.documents;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collection;

import org.pdfclown.PDF;
import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.layers.LayerDefinition;
import org.pdfclown.documents.interaction.forms.Form;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.viewer.ViewerPreferences;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.Rectangle;
import org.pdfclown.util.NotImplementedException;

/**
  PDF document [PDF:1.6:3.6.1].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public final class Document
  extends PdfObjectWrapper<PdfDictionary>
  implements Pageable
{
  // <class>
  // <classes>
  /**
    Document configuration.

    @author Stefano Chizzolini (http://www.stefanochizzolini.it)
    @since 0.1.0
  */
  public static final class Configuration
  {
    /**
      Version compatibility mode.
    */
    public enum CompatibilityModeEnum
    {
      /**
        Document's conformance version is ignored;
        any feature is accepted without checking its compatibility.
      */
      Passthrough,
      /**
        Document's conformance version is automatically updated
        to support used features.
      */
      Loose,
      /**
        Document's conformance version is mandatory;
        any unsupported feature is forbidden and causes an exception
        to be thrown in case of attempted use.
      */
      Strict
    }

    /**
      Cross-reference mode [PDF:1.6:3.4].
    */
    public enum XRefModeEnum
    {
      /**
        Cross-reference table [PDF:1.6:3.4.3].
      */
      @PDF(VersionEnum.PDF10)
      Plain,
      /**
        Cross-reference stream [PDF:1.6:3.4.7].
      */
      @PDF(VersionEnum.PDF15)
      Compressed
    }

    private CompatibilityModeEnum compatibilityMode = CompatibilityModeEnum.Loose;
    private XRefModeEnum xrefMode = XRefModeEnum.Plain;

    private final Document document;

    Configuration(
      Document document
      )
    {this.document = document;}

    /**
      Gets the document's version compatibility mode.
    */
    public CompatibilityModeEnum getCompatibilityMode(
      )
    {return compatibilityMode;}

    /**
      Gets the document associated with this configuration.
    */
    public Document getDocument(
      )
    {return document;}

    /**
      Gets the document's cross-reference mode.
    */
    public XRefModeEnum getXrefMode(
      )
    {return xrefMode;}

    /**
      @see #getCompatibilityMode()
    */
    public void setCompatibilityMode(
      CompatibilityModeEnum value
      )
    {compatibilityMode = value;}

    /**
      @see #getXrefMode()
    */
    public void setXrefMode(
      XRefModeEnum value
      )
    {document.checkCompatibility(xrefMode = value);}
  }

  /**
    Page layout to be used when the document is opened [PDF:1.6:3.6.1].
  */
  public enum PageLayoutEnum
  {
    /**
      Displays one page at a time.
    */
    SinglePage(PdfName.SinglePage),
    /**
      Displays the pages in one column.
    */
    OneColumn(PdfName.OneColumn),
    /**
      Displays the pages in two columns, with odd-numbered pages on the left.
    */
    TwoColumnLeft(PdfName.TwoColumnLeft),
    /**
      Displays the pages in two columns, with odd-numbered pages on the right.
    */
    TwoColumnRight(PdfName.TwoColumnRight),
    /**
      Displays the pages two at a time, with odd-numbered pages on the left.
    */
    @PDF(VersionEnum.PDF15)
    TwoPageLeft(PdfName.TwoPageLeft),
    /**
      Displays the pages two at a time, with odd-numbered pages on the right.
    */
    @PDF(VersionEnum.PDF15)
    TwoPageRight(PdfName.TwoPageRight);

    public static PageLayoutEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return PageLayoutEnum.SinglePage;

      for(PageLayoutEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Page layout unknown: " + name);
    }

    private PdfName name;

    private PageLayoutEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }

  /**
    Page mode specifying how the document should be displayed when opened [PDF:1.6:3.6.1].
  */
  public enum PageModeEnum
  {
    /**
      Neither document outline nor thumbnail images visible.
    */
    Simple(PdfName.UseNone),
    /**
      Document outline visible.
    */
    Bookmarks(PdfName.UseOutlines),
    /**
      Thumbnail images visible.
    */
    Thumbnails(PdfName.UseThumbs),
    /**
      Full-screen mode, with no menu bar, window controls, or any other window visible.
    */
    FullScreen(PdfName.FullScreen),
    /**
      Optional content group panel visible.
    */
    @PDF(VersionEnum.PDF15)
    Layers(PdfName.UseOC),
    /**
      Attachments panel visible.
    */
    @PDF(VersionEnum.PDF16)
    Attachments(PdfName.UseAttachments);

    public static PageModeEnum valueOf(
      PdfName name
      )
    {
      if(name == null)
        return PageModeEnum.Simple;

      for(PageModeEnum value : values())
      {
        if(value.getName().equals(name))
          return value;
      }
      throw new UnsupportedOperationException("Page mode unknown: " + name);
    }

    private PdfName name;

    private PageModeEnum(
      PdfName name
      )
    {this.name = name;}

    public PdfName getName(
      )
    {return name;}
  }
  // </classes>

  // <static>
  // <interface>
  // <public>
  @SuppressWarnings("unchecked")
  public static <T extends PdfObjectWrapper<?>> T resolve(
    Class<T> type,
    PdfDirectObject baseObject
    )
  {
    if(Destination.class.isAssignableFrom(type))
      return (T)Destination.wrap(baseObject);
    else
      throw new UnsupportedOperationException("Type '" + type.getName() + "' wrapping is not supported.");
  }
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  /**
    <span style="color:red">For internal use only.</span>
  */
  public java.util.Hashtable<PdfReference,Object> cache = new java.util.Hashtable<PdfReference,Object>();

  private Configuration configuration = new Configuration(this);
  // </fields>

  // <constructors>
  /**
    <span style="color:red">For internal use only.</span>
  */
  public Document(
    File context
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]{PdfName.Type},
        new PdfDirectObject[]{PdfName.Catalog}
        )
      );

    // Attach the document catalog to the file trailer!
    context.getTrailer().put(PdfName.Root, getBaseObject());

    // Pages collection.
    setPages(new Pages(this));

    // Default page size.
    setPageSize(PageFormat.getSize());

    // Default resources collection.
    setResources(new Resources(this));
  }

  /**
    <span style="color:red">For internal use only.</span>
  */
  public Document(
    PdfDirectObject baseObject // Catalog.
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Document clone(
    Document context
    )
  {throw new NotImplementedException();}

  /**
    Deletes the object from this document context.
  */
  public void exclude(
    PdfObjectWrapper<?> object
    )
  {
    if(object.getFile() != getFile())
      return;

    object.delete();
  }

  /**
    Deletes the objects from this document context.
  */
  public void exclude(
    Collection<? extends PdfObjectWrapper<?>> objects
    )
  {
    for(PdfObjectWrapper<?> object : objects)
    {exclude(object);}
  }

  /**
    Gets the document's behavior in response to trigger events.
  */
  @PDF(VersionEnum.PDF14)
  public DocumentActions getActions(
    )
  {return new DocumentActions(getBaseDataObject().get(PdfName.AA, PdfDictionary.class));}

  /**
    Gets the article threads.
  */
  @PDF(VersionEnum.PDF11)
  public Articles getArticles(
    )
  {return Articles.wrap(getBaseDataObject().get(PdfName.Threads, PdfArray.class, false));}

  /**
    Gets the bookmark collection.
  */
  public Bookmarks getBookmarks(
    )
  {return Bookmarks.wrap(getBaseDataObject().get(PdfName.Outlines, PdfDictionary.class, false));}

  /**
    Gets the configuration of this document.
  */
  public final Configuration getConfiguration(
    )
  {return configuration;}

  /**
    Gets the interactive form (AcroForm).

    @since 0.0.7
  */
  @PDF(VersionEnum.PDF12)
  public Form getForm(
    )
  {return Form.wrap(getBaseDataObject().get(PdfName.AcroForm, PdfDictionary.class));}

  /**
    Gets common document metadata.
  */
  public Information getInformation(
    )
  {return Information.wrap(getFile().getTrailer().get(PdfName.Info, PdfDictionary.class, false));}

  /**
    Gets the optional content properties.
  */
  @PDF(VersionEnum.PDF15)
  public LayerDefinition getLayer(
    )
  {return LayerDefinition.wrap(getBaseDataObject().get(PdfName.OCProperties, PdfDictionary.class));}

  /**
    Gets the name dictionary.
  */
  @PDF(VersionEnum.PDF12)
  public Names getNames(
    )
  {return new Names(getBaseDataObject().get(PdfName.Names, PdfDictionary.class));}

  /**
    Gets the page label ranges.
  */
  @PDF(VersionEnum.PDF13)
  public PageLabels getPageLabels(
    )
  {return new PageLabels(getBaseDataObject().get(PdfName.PageLabels, PdfDictionary.class));}

  /**
    Gets the page layout to be used when the document is opened.
  */
  public PageLayoutEnum getPageLayout(
    )
  {return PageLayoutEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.PageLayout));}

  /**
    Gets the page mode, that is how the document should be displayed when is opened.
  */
  public PageModeEnum getPageMode(
    )
  {return PageModeEnum.valueOf((PdfName)getBaseDataObject().get(PdfName.PageMode));}

  /**
    Gets the page collection.
  */
  public Pages getPages(
    )
  {return new Pages(getBaseDataObject().get(PdfName.Pages));}

  /**
    Gets the default page size [PDF:1.6:3.6.2].

    @see #getSize()
  */
  public Dimension2D getPageSize(
    )
  {
    PdfArray mediaBox = getMediaBox();
    return mediaBox != null
      ? new Dimension(
        ((PdfNumber<?>)mediaBox.get(2)).getValue().intValue(),
        ((PdfNumber<?>)mediaBox.get(3)).getValue().intValue()
        )
      : null;
  }

  /**
    Gets the default resource collection [PDF:1.6:3.6.2].
    <p>The default resource collection is used as last resort by every page that doesn't reference
    one explicitly (and doesn't reference an intermediate one implicitly).</p>
  */
  public Resources getResources(
    )
  {return Resources.wrap(((PdfDictionary)getBaseDataObject().resolve(PdfName.Pages)).get(PdfName.Resources, PdfDictionary.class));}

  /**
    Gets the document size, that is the maximum page dimensions across the whole document.

    @see #getPageSize()
  */
  public Dimension2D getSize(
    )
  {
    double height = 0, width = 0;
    for(Page page : getPages())
    {
      Dimension2D pageSize = page.getSize();
      height = Math.max(height,pageSize.getHeight());
      width = Math.max(width,pageSize.getWidth());
    }
    return new org.pdfclown.util.math.geom.Dimension(width,height);
  }

  /**
    Gets the version of the PDF specification this document conforms to.
  */
  @PDF(VersionEnum.PDF14)
  public Version getVersion(
    )
  {
    /*
      NOTE: If the header specifies a later version, or if this entry is absent, the document
      conforms to the version specified in the header.
    */
    Version fileVersion = getFile().getVersion();

    PdfName versionObject = (PdfName)getBaseDataObject().get(PdfName.Version);
    if(versionObject == null)
      return fileVersion;

    Version version = Version.get(versionObject);
    if(getFile().getReader() == null)
      return version;

    return (version.compareTo(fileVersion) > 0 ? version : fileVersion);
  }

  /**
    Gets the way the document is to be presented.
  */
  @PDF(VersionEnum.PDF12)
  public ViewerPreferences getViewerPreferences(
    )
  {return ViewerPreferences.wrap(getBaseDataObject().get(PdfName.ViewerPreferences, PdfDictionary.class));}

  /**
    Clones the specified object within this document context.
  */
  public PdfObjectWrapper<?> include(
    PdfObjectWrapper<?> object
    )
  {
    if(object.getFile() == getFile())
      return object;

    return (PdfObjectWrapper<?>)object.clone(this);
  }

  /**
    Clones the specified collection objects within this document context.
  */
  public Collection<? extends PdfObjectWrapper<?>> include(
    Collection<? extends PdfObjectWrapper<?>> objects
    )
  {
    ArrayList<PdfObjectWrapper<?>> includedObjects = new ArrayList<PdfObjectWrapper<?>>(objects.size());
    for(PdfObjectWrapper<?> object : objects)
    {includedObjects.add(include(object));}

    return includedObjects;
  }

  /**
    Forces a named base object to be expressed as its corresponding high-level representation.
  */
  public <T extends PdfObjectWrapper<?>> T resolveName(
    Class<T> type,
    PdfDirectObject namedBaseObject
    )
  {
    if(namedBaseObject instanceof PdfString) // Named object.
      return getNames().get(type, (PdfString)namedBaseObject);
    else // Explicit object.
      return resolve(type, namedBaseObject);
  }

  /**
    @see #getActions()
  */
  public void setActions(
    DocumentActions value
    )
  {getBaseDataObject().put(PdfName.AA, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getArticles()
  */
  public void setArticles(
    Articles value
    )
  {getBaseDataObject().put(PdfName.Threads, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getBookmarks()
  */
  public void setBookmarks(
    Bookmarks value
    )
  {getBaseDataObject().put(PdfName.Outlines, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getConfiguration()
    @since 0.1.0
  */
  public void setConfiguration(
    Configuration value
    )
  {configuration = value;}

  /**
    @see #getForm()
    @since 0.0.7
  */
  public void setForm(
    Form value
    )
  {getBaseDataObject().put(PdfName.AcroForm, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getInformation()
  */
  public void setInformation(
    Information value
    )
  {getFile().getTrailer().put(PdfName.Info, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getLayer()
  */
  public void setLayer(
    LayerDefinition value
    )
  {
    checkCompatibility("layer");
    getBaseDataObject().put(PdfName.OCProperties, PdfObjectWrapper.getBaseObject(value));
  }

  /**
    @see #getNames()
    @since 0.0.4
  */
  public void setNames(
    Names value
    )
  {getBaseDataObject().put(PdfName.Names, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getPageLabels()
  */
  public void setPageLabels(
    PageLabels value
    )
  {
    checkCompatibility("pageLabels");
    getBaseDataObject().put(PdfName.PageLabels, PdfObjectWrapper.getBaseObject(value));
  }

  /**
    @see #getPageLayout()
  */
  public void setPageLayout(
    PageLayoutEnum value
    )
  {getBaseDataObject().put(PdfName.PageLayout, value.getName());}

  /**
    @see #getPageMode()
  */
  public void setPageMode(
    PageModeEnum value
    )
  {getBaseDataObject().put(PdfName.PageMode, value.getName());}

  /**
    @see #getPages()
  */
  public void setPages(
    Pages value
    )
  {getBaseDataObject().put(PdfName.Pages, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getPageSize()
  */
  public void setPageSize(
    Dimension2D value
    )
  {
    PdfArray mediaBox = getMediaBox();
    if(mediaBox == null)
    {
      // Create default media box!
      mediaBox = new Rectangle(0,0,0,0).getBaseDataObject();
      // Assign the media box to the document!
      ((PdfDictionary)getBaseDataObject().resolve(PdfName.Pages)).put(PdfName.MediaBox,mediaBox);
    }
    mediaBox.set(2, PdfReal.get(value.getWidth()));
    mediaBox.set(3, PdfReal.get(value.getHeight()));
  }

  /**
    @see #getResources()
  */
  public void setResources(
    Resources value
    )
  {((PdfDictionary)getBaseDataObject().resolve(PdfName.Pages)).put(PdfName.Resources, PdfObjectWrapper.getBaseObject(value));}

  /**
    @see #getVersion()
  */
  public void setVersion(
    Version value
    )
  {getBaseDataObject().put(PdfName.Version, PdfName.get(value));}

  /**
    @see #getViewerPreferences()
  */
  public void setViewerPreferences(
    ViewerPreferences value
    )
  {getBaseDataObject().put(PdfName.ViewerPreferences, PdfObjectWrapper.getBaseObject(value));}

  // <Pageable>
  @Override
  public int getNumberOfPages(
    )
  {return getPages().size();}

  @Override
  public java.awt.print.PageFormat getPageFormat(
    int pageIndex
    ) throws IndexOutOfBoundsException
  {
    java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
    {
      Page page = getPages().get(pageIndex);
      Paper paper = new Paper();
      {
        Rectangle2D pageBox = page.getBox();
        paper.setSize(pageBox.getWidth(),pageBox.getHeight());
      }
      pageFormat.setPaper(paper);
    }
    return pageFormat;
  }

  @Override
  public Printable getPrintable(
    int pageIndex
    ) throws IndexOutOfBoundsException
  {return getPages().get(pageIndex);}
  // </Pageable>
  // </public>

  // <private>
  /**
    Gets the default media box.
  */
  private PdfArray getMediaBox(
    )
  {
    /*
      NOTE: Document media box MUST be associated with the page-tree root node in order to be
      inheritable by all the pages.
    */
    return (PdfArray)((PdfDictionary)getBaseDataObject().resolve(PdfName.Pages)).resolve(PdfName.MediaBox);
  }
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}