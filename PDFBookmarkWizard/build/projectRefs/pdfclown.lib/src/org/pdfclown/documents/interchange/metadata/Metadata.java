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

package org.pdfclown.documents.interchange.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pdfclown.PDF;
import org.pdfclown.VersionEnum;
import org.pdfclown.bytes.IBuffer;
import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfStream;
import org.xml.sax.SAXException;

/**
  Metadata stream [PDF:1.6:10.2.2].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.1
  @version 0.1.2, 12/21/12
*/
@PDF(VersionEnum.PDF14)
public final class Metadata
  extends PdfObjectWrapper<PdfStream>
{
  // <class>
  // <dynamic>
  // <constructors>
  public Metadata(
    Document context
    )
  {
    super(
      context,
      new PdfStream(
        new PdfDictionary(
          new PdfName[]
          {
            PdfName.Type,
            PdfName.Subtype
          },
          new PdfDirectObject[]
          {
            PdfName.Metadata,
            PdfName.XML
          }
          ))
      );
  }

  public Metadata(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public Metadata clone(
    Document context
    )
  {return (Metadata)super.clone(context);}

  /**
    Gets the metadata contents.
  */
  public org.w3c.dom.Document getContent(
    )
  {
    org.w3c.dom.Document content;
    {
      // 1. Instantiate the document deserializer!
      DocumentBuilder contentDeserializer;
      try
      {contentDeserializer = DocumentBuilderFactory.newInstance().newDocumentBuilder();}
      catch(ParserConfigurationException e)
      {throw new RuntimeException(e);}

      // 2. Get the document contents!
      IBuffer contentBody = getBaseDataObject().getBody();
      if(contentBody.getLength() > 0)
      {
        InputStream contentStream = new ByteArrayInputStream(contentBody.toByteArray());

        // 3. Parse the document contents!
        try
        {content = contentDeserializer.parse(contentStream);}
        catch(SAXException e)
        {throw new RuntimeException("XML parsing failed.", e);}
        catch(IOException e)
        {throw new RuntimeException("XML reading failed.", e);}
        finally
        {
          try
          {contentStream.close();}
          catch(IOException e)
          {throw new RuntimeException(e);}
        }
      }
      else
      {content = null;}
    }
    return content;
  }

  /**
    @see #getContent()
  */
  public void setContent(
    org.w3c.dom.Document value
    )
  {
    // 1. Instantiate the document serializer!
    Transformer contentSerializer;
    try
    {contentSerializer = TransformerFactory.newInstance().newTransformer();}
    catch(TransformerConfigurationException e)
    {throw new RuntimeException(e);}

    // 2. Get the document contents!
    ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
    try
    {contentSerializer.transform(new DOMSource(value), new StreamResult(contentStream));}
    catch(TransformerException e)
    {throw new RuntimeException(e);}

    // 3. Store the document contents into the stream body!
    IBuffer body = getBaseDataObject().getBody();
    body.setLength(0);
    body.write(contentStream.toByteArray());
    try
    {contentStream.close();}
    catch(IOException e)
    {throw new RuntimeException(e);}
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
