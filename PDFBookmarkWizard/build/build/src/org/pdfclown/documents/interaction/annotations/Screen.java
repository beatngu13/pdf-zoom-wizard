/*
  Copyright 2012 Stefano Chizzolini. http://www.pdfclown.org

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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.files.EmbeddedFile;
import org.pdfclown.documents.files.FileSpecification;
import org.pdfclown.documents.interaction.actions.Render;
import org.pdfclown.documents.interaction.actions.Render.OperationEnum;
import org.pdfclown.documents.interaction.forms.TextField;
import org.pdfclown.documents.multimedia.MediaClipData;
import org.pdfclown.documents.multimedia.MediaRendition;
import org.pdfclown.documents.multimedia.Rendition;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfString;

/**
  Screen annotation [PDF:1.6:8.4.5].
  <p>It specifies a region of a page upon which media clips may be played.</p>

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF15)
public final class Screen
  extends Annotation
{
  // <class>
  // <static>
  // <fields>
  private static final String PlayerPlaceholder = "%player%";
  /**
    Script for preview and rendering control.
    <p>NOTE: PlayerPlaceholder MUST be replaced with the actual player instance symbol.</p>
  */
  private static final String RenderScript = "if(" + PlayerPlaceholder + "==undefined){"
    + "var doc = this;"
    + "var settings={autoPlay:false,visible:false,volume:100,startAt:0};"
    + "var events=new app.media.Events({"
      + "afterFocus:function(event){try{if(event.target.isPlaying){event.target.pause();}else{event.target.play();}doc.getField('" + PlayerPlaceholder + "').setFocus();}catch(e){}},"
      + "afterReady:function(event){try{event.target.seek(event.target.settings.startAt);event.target.visible=true;}catch(e){}}"
      + "});"
    + "var " + PlayerPlaceholder + "=app.media.openPlayer({settings:settings,events:events});"
    + "}";
  // </fields>
  // </static>

  // <dynamic>
  // <constructors>
  public Screen(
    Page page,
    Rectangle2D box,
    String text,
    String mediaPath,
    String mimeType
    )
  {this(page, box, text, new File(mediaPath), mimeType);}

  public Screen(
    Page page,
    Rectangle2D box,
    String text,
    File mediaFile,
    String mimeType
    )
  {
    this(
      page, box, text,
      new MediaRendition(
        new MediaClipData(
          FileSpecification.get(
            EmbeddedFile.get(page.getDocument(), mediaFile),
            mediaFile.getName()
            ),
          mimeType
          )
        )
      );
  }

  public Screen(
    Page page,
    Rectangle2D box,
    String text,
    Rendition rendition
    )
  {
    super(page, PdfName.Screen, box, text);

    Render render = new Render(this, OperationEnum.PlayResume, rendition);
    {
      // Adding preview and play/pause control...
      /*
        NOTE: Mouse-related actions don't work when the player is active; therefore, in order to let
        the user control the rendering of the media clip (play/pause) just by mouse-clicking on the
        player, we can only rely on the player's focus event. Furthermore, as the player's focus can
        only be altered setting it on another widget, we have to define an ancillary field on the
        same page (so convoluted!).
      */
      String playerReference = "__player" + ((PdfReference)render.getBaseObject()).getObjectNumber();
      getDocument().getForm().getFields().add(new TextField(playerReference, new Widget(page, new Rectangle((int)box.getX(), (int)box.getY(), 0, 0)), "")); // Ancillary field.
      render.setScript(RenderScript.replace(PlayerPlaceholder, playerReference));
    }
    getActions().setOnPageOpen(render);

    if(rendition instanceof MediaRendition)
    {
      PdfObjectWrapper<?> data = ((MediaRendition)rendition).getClip().getData();
      if(data instanceof FileSpecification<?>)
      {
        // Adding fallback annotation...
        /*
          NOTE: In case of viewers which don't support video rendering, this annotation gently
          degrades to a file attachment that can be opened on the same location of the corresponding
          screen annotation.
        */
        FileAttachment attachment = new FileAttachment(page, box, text, (FileSpecification<?>)data);
        getBaseDataObject().put(PdfName.T, PdfString.get(((FileSpecification<?>)data).getPath()));
        // Force empty appearance to ensure no default icon is drawn on the canvas!
        attachment.getBaseDataObject().put(PdfName.AP, new PdfDictionary(new PdfName[]{PdfName.D, PdfName.R, PdfName.N}, new PdfDirectObject[]{new PdfDictionary(), new PdfDictionary(), new PdfDictionary()}));
      }
    }
  }

  Screen(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Screen clone(
    Document context
    )
  {return (Screen)super.clone(context);}
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
