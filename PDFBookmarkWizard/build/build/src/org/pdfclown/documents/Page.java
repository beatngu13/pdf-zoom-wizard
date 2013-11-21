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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.HashSet;
import java.util.Set;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.documents.contents.ContentScanner;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.IContentContext;
import org.pdfclown.documents.contents.Resources;
import org.pdfclown.documents.contents.RotationEnum;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.documents.interaction.navigation.page.Transition;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfInteger;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfNumber;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReal;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.objects.Rectangle;
import org.pdfclown.util.NotImplementedException;
import org.pdfclown.util.math.geom.Dimension;

/**
  Document page [PDF:1.6:3.6.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.0
  @version 0.1.2, 12/28/12
*/
@PDF(VersionEnum.PDF10)
public final class Page
  extends PdfObjectWrapper<PdfDictionary>
  implements IContentContext,
    Printable
{
  /*
    NOTE: Inheritable attributes are NOT early-collected, as they are NOT part
    of the explicit representation of a page. They are retrieved every time
    clients call.
  */
  // <class>
  // <classes>
  /**
    Annotations tab order [PDF:1.6:3.6.2].
  */
  @PDF(VersionEnum.PDF15)
  public enum TabOrderEnum
  {
    // <class>
    // <static>
    // <fields>
    /**
      Row order.
    */
    Row(PdfName.R),
    /**
      Column order.
    */
    Column(PdfName.C),
    /**
      Structure order.
    */
    Structure(PdfName.S);
    // </fields>

    // <interface>
    // <public>
    /**
      Gets the tab order corresponding to the given value.
    */
    public static TabOrderEnum get(
      PdfName value
      )
    {
      for(TabOrderEnum tabOrder : TabOrderEnum.values())
      {
        if(tabOrder.getCode().equals(value))
          return tabOrder;
      }
      return null;
    }
    // </public>
    // </interface>
    // </static>

    // <dynamic>
    // <fields>
    private final PdfName code;
    // </fields>

    // <constructors>
    private TabOrderEnum(
      PdfName code
      )
    {this.code = code;}
    // </constructors>

    // <interface>
    // <public>
    public PdfName getCode(
      )
    {return code;}
    // </public>
    // </interface>
    // </dynamic>
    // </class>
  }
  // </classes>

  // <static>
  // <fields>
  public static final Set<PdfName> InheritableAttributeKeys = new HashSet<PdfName>();
  static
  {
    InheritableAttributeKeys.add(PdfName.Resources);
    InheritableAttributeKeys.add(PdfName.MediaBox);
    InheritableAttributeKeys.add(PdfName.CropBox);
    InheritableAttributeKeys.add(PdfName.Rotate);
  }
  // </fields>

  // <interface>
  // <public>
  /**
    Gets the attribute value corresponding to the specified key, possibly recurring to its ancestor
    nodes in the page tree.

    @param pageObject Page object.
    @param key Attribute key.
  */
  public static PdfDirectObject getInheritableAttribute(
    PdfDictionary pageObject,
    PdfName key
    )
  {
    /*
      NOTE: It moves upward until it finds the inherited attribute.
    */
    PdfDictionary dictionary = pageObject;
    while(true)
    {
      PdfDirectObject entry = dictionary.get(key);
      if(entry != null)
        return entry;

      dictionary = (PdfDictionary)dictionary.resolve(PdfName.Parent);
      if(dictionary == null)
      {
        // Isn't the page attached to the page tree?
        /* NOTE: This condition is illegal. */
        if(pageObject.get(PdfName.Parent) == null)
          throw new RuntimeException("Inheritable attributes unreachable: Page objects MUST be inserted into their document's Pages collection before being used.");

        return null;
      }
    }
  }

  public static Page wrap(
    PdfDirectObject baseObject
    )
  {return baseObject == null ? null : new Page(baseObject);}
  // </public>
  // </interface>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new page within the specified document context, using the default size.

    @param context Document where to place this page.
  */
  public Page(
    Document context
    )
  {this(context, null);}

  /**
    Creates a new page within the specified document context.

    @param context Document where to place this page.
    @param size Page size. In case of <code>null</code>, uses the default size.
  */
  public Page(
    Document context,
    Dimension2D size
    )
  {
    super(
      context,
      new PdfDictionary(
        new PdfName[]
        {
          PdfName.Type,
          PdfName.Contents
        },
        new PdfDirectObject[]
        {
          PdfName.Page,
          context.getFile().register(new PdfStream())
        }
        )
      );
    if(size != null)
    {setSize(size);}
  }

  private Page(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Page clone(
    Document context
    )
  {return (Page)super.clone(context);}

  /**
    Gets the page's behavior in response to trigger events.
  */
  @PDF(VersionEnum.PDF12)
  public PageActions getActions(
    )
  {return new PageActions(getBaseDataObject().get(PdfName.AA, PdfDictionary.class));}

  /**
    Gets the annotations associated to the page.
  */
  public PageAnnotations getAnnotations(
    )
  {return new PageAnnotations(getBaseDataObject().get(PdfName.Annots, PdfArray.class), this);}

  /**
    Gets the extent of the page's meaningful content (including potential white space) as intended
    by the page's creator [PDF:1.7:10.10.1].

    @see #getCropBox()
  */
  @PDF(VersionEnum.PDF13)
  public Rectangle2D getArtBox(
    )
  {
    /*
      NOTE: The default value is the page's crop box.
    */
    PdfDirectObject artBoxObject = getInheritableAttribute(PdfName.ArtBox);
    return artBoxObject != null ? Rectangle.wrap(artBoxObject).toRectangle2D() : getCropBox();
  }

  /**
    Gets the page article beads.
  */
  public PageArticleElements getArticleElements(
    )
  {return new PageArticleElements(getBaseDataObject().get(PdfName.B, PdfArray.class), this);}

  /**
    Gets the region to which the contents of the page should be clipped when output in a production
    environment [PDF:1.7:10.10.1].
    <p>This may include any extra bleed area needed to accommodate the physical limitations of
    cutting, folding, and trimming equipment. The actual printed page may include printing marks
    that fall outside the bleed box.</p>

    @see #getCropBox()
  */
  @PDF(VersionEnum.PDF13)
  public Rectangle2D getBleedBox(
    )
  {
    /*
      NOTE: The default value is the page's crop box.
    */
    PdfDirectObject bleedBoxObject = getInheritableAttribute(PdfName.BleedBox);
    return bleedBoxObject != null ? Rectangle.wrap(bleedBoxObject).toRectangle2D() : getCropBox();
  }

  /**
    Gets the region to which the contents of the page are to be clipped (cropped) when displayed or
    printed [PDF:1.7:10.10.1].
    <p>Unlike the other boxes, the crop box has no defined meaning in terms of physical page
    geometry or intended use; it merely imposes clipping on the page contents. However, in the
    absence of additional information, the crop box determines how the page's contents are to be
    positioned on the output medium.</p>

    @see #getBox()
  */
  public Rectangle2D getCropBox(
    )
  {
    /*
      NOTE: The default value is the page's media box.
    */
    PdfDirectObject cropBoxObject = getInheritableAttribute(PdfName.CropBox);
    return cropBoxObject != null ? Rectangle.wrap(cropBoxObject).toRectangle2D() : getBox();
  }

  /**
    Gets the page's display duration.
    <p>The page's display duration (also called its advance timing)
    is the maximum length of time, in seconds, that the page is displayed
    during presentations before the viewer application automatically advances
    to the next page.</p>
    <p>By default, the viewer does not advance automatically.</p>
  */
  @PDF(VersionEnum.PDF11)
  public double getDuration(
    )
  {
    PdfNumber<?> durationObject = (PdfNumber<?>)getBaseDataObject().get(PdfName.Dur);
    return durationObject == null ? 0 : durationObject.getDoubleValue();
  }

  /**
    Gets the index of the page.
  */
  public int getIndex(
    )
  {
    /*
      NOTE: We'll scan sequentially each page-tree level above this page object
      collecting page counts. At each level we'll scan the kids array from the
      lower-indexed item to the ancestor of this page object at that level.
    */
    PdfReference ancestorKidReference = (PdfReference)getBaseObject();
    PdfReference parentReference = (PdfReference)getBaseDataObject().get(PdfName.Parent);
    PdfDictionary parent = (PdfDictionary)parentReference.getDataObject();
    PdfArray kids = (PdfArray)parent.resolve(PdfName.Kids);
    int index = 0;
    for(
      int i = 0;
      true;
      i++
      )
    {
      PdfReference kidReference = (PdfReference)kids.get(i);
      // Is the current-level counting complete?
      // NOTE: It's complete when it reaches the ancestor at the current level.
      if(kidReference.equals(ancestorKidReference)) // Ancestor node.
      {
        // Does the current level correspond to the page-tree root node?
        if(!parent.containsKey(PdfName.Parent))
        {
          // We reached the top: counting's finished.
          return index;
        }
        // Set the ancestor at the next level!
        ancestorKidReference = parentReference;
        // Move up one level!
        parentReference = (PdfReference)parent.get(PdfName.Parent);
        parent = (PdfDictionary)parentReference.getDataObject();
        kids = (PdfArray)parent.resolve(PdfName.Kids);
        i = -1;
      }
      else // Intermediate node.
      {
        PdfDictionary kid = (PdfDictionary)kidReference.getDataObject();
        if(kid.get(PdfName.Type).equals(PdfName.Page))
          index++;
        else
          index += ((PdfInteger)kid.get(PdfName.Count)).getRawValue();
      }
    }
  }

  /**
    Gets the page size.
  */
  public Dimension2D getSize(
    )
  {return Dimension.get(getBox());}

  /**
    Gets the tab order to be used for annotations on the page.
  */
  @PDF(VersionEnum.PDF15)
  public TabOrderEnum getTabOrder(
    )
  {return TabOrderEnum.get((PdfName)getBaseDataObject().get(PdfName.Tabs));}

  /**
    Gets the transition effect to be used when displaying the page during presentations.
  */
  @PDF(VersionEnum.PDF11)
  public Transition getTransition(
    )
  {return Transition.wrap(getBaseDataObject().get(PdfName.Trans));}

  /**
    Gets the intended dimensions of the finished page after trimming [PDF:1.7:10.10.1].
    <p>It may be smaller than the media box to allow for production-related content, such as
    printing instructions, cut marks, or color bars.</p>

    @see #getCropBox()
  */
  @PDF(VersionEnum.PDF13)
  public Rectangle2D getTrimBox(
    )
  {
    /*
      NOTE: The default value is the page's crop box.
    */
    PdfDirectObject trimBoxObject = getInheritableAttribute(PdfName.TrimBox);
    return trimBoxObject != null ? Rectangle.wrap(trimBoxObject).toRectangle2D() : getCropBox();
  }

  /**
    @see #getActions()
  */
  public void setActions(
    PageActions value
    )
  {getBaseDataObject().put(PdfName.AA, value.getBaseObject());}

  /**
    @see #getAnnotations()
  */
  public void setAnnotations(
    PageAnnotations value
    )
  {getBaseDataObject().put(PdfName.Annots, value.getBaseObject());}

  /**
    @see #getArtBox()
  */
  public void setArtBox(
    Rectangle2D value
    )
  {getBaseDataObject().put(PdfName.ArtBox, new Rectangle(value).getBaseDataObject());}

  /**
    @see #getBleedBox()
  */
  public void setBleedBox(
    Rectangle2D value
    )
  {getBaseDataObject().put(PdfName.BleedBox, new Rectangle(value).getBaseDataObject());}

  /**
    @see #getBox()
  */
  public void setBox(
    Rectangle2D value
    )
  {getBaseDataObject().put(PdfName.MediaBox, new Rectangle(value).getBaseDataObject());}

  /**
    @see #getCropBox()
  */
  public void setCropBox(
    Rectangle2D value
    )
  {getBaseDataObject().put(PdfName.CropBox, new Rectangle(value).getBaseDataObject());}

  /**
    @see #getDuration()
  */
  public void setDuration(
    double value
    )
  {getBaseDataObject().put(PdfName.Dur, value == 0 ? null : PdfReal.get(value));}

  /**
    @see #getRotation()
  */
  public void setRotation(
    RotationEnum value
    )
  {getBaseDataObject().put(PdfName.Rotate, value.getCode());}

  /**
    @see #getSize()
  */
  public void setSize(
    Dimension2D value
    )
  {
    Rectangle2D box;
    try
    {box = getBox();}
    catch (Exception e)
    {box = new Rectangle2D.Double();}
    box.setRect(box.getX(), box.getY(), value.getWidth(), value.getHeight());
    setBox(box);
  }

  /**
    @see #getTabOrder()
  */
  public void setTabOrder(
    TabOrderEnum value
    )
  {getBaseDataObject().put(PdfName.Tabs,value.getCode());}

  /**
    @see #getTransition()
  */
  public void setTransition(
    Transition value
    )
  {getBaseDataObject().put(PdfName.Trans, value.getBaseObject());}

  /**
    @see #getTrimBox()
  */
  public void setTrimBox(
    Rectangle2D value
    )
  {getBaseDataObject().put(PdfName.TrimBox, new Rectangle(value).getBaseDataObject());}

  // <IContentContext>
  @Override
  public Rectangle2D getBox(
    )
  {return Rectangle.wrap(getInheritableAttribute(PdfName.MediaBox)).toRectangle2D();}

  @Override
  public Contents getContents(
    )
  {
    PdfDirectObject contentsObject = getBaseDataObject().get(PdfName.Contents);
    if(contentsObject == null)
    {getBaseDataObject().put(PdfName.Contents, contentsObject = getFile().register(new PdfStream()));}
    return Contents.wrap(contentsObject, this);
  }

  @Override
  public Resources getResources(
    )
  {
    Resources resources = Resources.wrap(getInheritableAttribute(PdfName.Resources));
    return resources != null ? resources : Resources.wrap(getBaseDataObject().get(PdfName.Resources, PdfDictionary.class));
  }

  @Override
  public RotationEnum getRotation(
    )
  {return RotationEnum.valueOf((PdfInteger)getInheritableAttribute(PdfName.Rotate));}

  @Override
  public void render(
    Graphics2D context,
    Dimension2D size
    )
  {
    ContentScanner scanner = new ContentScanner(getContents());
    scanner.render(context,size);
  }

  // <IContentEntity>
  @Override
  public ContentObject toInlineObject(
    PrimitiveComposer composer
    )
  {throw new NotImplementedException();}

  @Override
  public XObject toXObject(
    Document context
    )
  {
    FormXObject form;
    {
      form = new FormXObject(context, getBox());
      form.setResources(
        context.equals(getDocument())
          ? getResources() // Same document: reuses the existing resources.
          : getResources().clone(context) // Alien document: clones the resources.
        );

      // Body (contents).
      {
        IBuffer formBody = form.getBaseDataObject().getBody();
        PdfDataObject contentsDataObject = getBaseDataObject().resolve(PdfName.Contents);
        if(contentsDataObject instanceof PdfStream)
        {formBody.append(((PdfStream)contentsDataObject).getBody());}
        else
        {
          for(PdfDirectObject contentStreamObject : (PdfArray)contentsDataObject)
          {formBody.append(((PdfStream)contentStreamObject.resolve()).getBody());}
        }
      }
    }
    return form;
  }
  // </IContentEntity>
  // </IContentContext>

  // <Printable>
  @Override
  public int print(
    Graphics graphics,
    PageFormat pageFormat,
    int pageIndex
    ) throws PrinterException
  {
    //TODO:verify pageIndex correspondence!
    render(
      (Graphics2D)graphics,
      new Dimension(//TODO:verify page resolution!
        pageFormat.getWidth(),
        pageFormat.getHeight()
        )
      );

    return Printable.PAGE_EXISTS;
  }
  // </Printable>
  // </public>

  // <private>
  private PdfDirectObject getInheritableAttribute(
    PdfName key
    )
  {return getInheritableAttribute(getBaseDataObject(), key);}
  // </private>
  // </interface>
  // </dynamic>
  // </class>
}