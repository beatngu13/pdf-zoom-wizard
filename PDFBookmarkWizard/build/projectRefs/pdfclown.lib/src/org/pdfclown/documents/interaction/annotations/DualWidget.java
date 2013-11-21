/*
  Copyright 2008-2012 Stefano Chizzolini. http://www.pdfclown.org

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

package org.pdfclown.documents.interaction.annotations;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.xObjects.FormXObject;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.util.math.geom.Dimension;

/**
  Dual-state widget annotation.
  <p>As its name implies, it has two states: on and off.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.7
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF12)
public final class DualWidget
  extends Widget
{
  // <class>
  // <dynamic>
  // <constructors>
  /**
    @param widgetName Widget name. It corresponds to the on-state name.
  */
  public DualWidget(
    Page page,
    Rectangle2D box,
    String widgetName
    )
  {
    super(page,box);

    // Initialize the on-state appearance!
    /*
      NOTE: This is necessary to keep the reference to the on-state name.
    */
    Appearance appearance = new Appearance(page.getDocument());
    setAppearance(appearance);
    AppearanceStates normalAppearance = appearance.getNormal();
    normalAppearance.put(
      new PdfName(widgetName),
      new FormXObject(
        page.getDocument(),
        Dimension.get(box)
        )
      );
  }

  DualWidget(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public DualWidget clone(
    Document context
    )
  {return (DualWidget)super.clone(context);}

  public String getWidgetName(
    )
  {
    for(Map.Entry<PdfName,FormXObject> normalAppearanceEntry : getAppearance().getNormal().entrySet())
    {
      PdfName key = normalAppearanceEntry.getKey();
      if(!key.equals(PdfName.Off)) // 'On' state.
        return key.getValue();
    }
    return null; // NOTE: It MUST NOT happen (on-state should always be defined).
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}