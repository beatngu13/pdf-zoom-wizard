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

package org.pdfclown.files;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.pdfclown.documents.Document;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObjectWrapper;
import org.pdfclown.objects.PdfString;
import org.pdfclown.objects.PdfString.SerializationModeEnum;
import org.pdfclown.tokens.CharsetName;
import org.pdfclown.tokens.Writer;

/**
  File identifier [PDF:1.7:10.3].

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.1.2
  @version 0.1.2, 12/21/12
*/
public final class FileIdentifier
  extends PdfObjectWrapper<PdfArray>
{
  // <class>
  // <static>
  // <public>
  /**
    Gets an existing file identifier.

    @param baseObject Base object to wrap.
  */
  public static FileIdentifier wrap(
    PdfDirectObject baseObject
    )
  {return baseObject != null ? new FileIdentifier(baseObject) : null;}
  // </public>

  // <private>
  private static PdfArray createBaseDataObject(
    )
  {return new PdfArray(PdfString.Default, PdfString.Default);}

  private static void digest(
    MessageDigest digest,
    Object value
    ) throws UnsupportedEncodingException
  {digest.update(value.toString().getBytes(CharsetName.ISO88591));}
  // </private>
  // </static>

  // <dynamic>
  // <constructors>
  /**
    Creates a new direct file identifier.
  */
  public FileIdentifier(
    )
  {this(createBaseDataObject());}

  /**
    Creates a new indirect file identifier.
  */
  public FileIdentifier(
    File context
    )
  {super(context, createBaseDataObject());}

  /**
    Instantiates an existing file identifier.

    @param baseObject Base object.
  */
  private FileIdentifier(
    PdfDirectObject baseObject
    )
  {super(baseObject);}
  // </constructors>

  // <interface>
  // <public>
  @Override
  public FileIdentifier clone(
    Document context
    )
  {return (FileIdentifier)super.clone(context);}

  /**
    Gets the permanent identifier based on the contents of the file at the time it was originally
    created.
  */
  public String getBaseID(
    )
  {return (String)((PdfString)getBaseDataObject().get(0)).getValue();}

  /**
    Gets the changing identifier based on the file's contents at the time it was last updated.
  */
  public String getVersionID(
    )
  {return (String)((PdfString)getBaseDataObject().get(1)).getValue();}

  /**
    Computes a new version identifier based on the file's contents.
    This method is typically invoked internally during file serialization.

    @param writer File serializer.
  */
  public void update(
    Writer writer
    )
  {
    /*
      NOTE: To help ensure the uniqueness of file identifiers, it is recommended that they are
      computed by means of a message digest algorithm such as MD5 [PDF:1.7:10.3].
    */
    MessageDigest md5;
    try
    {md5 = MessageDigest.getInstance("MD5");}
    catch(NoSuchAlgorithmException e)
    {throw new RuntimeException("MD5 algorithm unavailable.", e);}

    File file = writer.getFile();
    try
    {
      // File identifier computation is fulfilled with this information:
      // a) Current time.
      digest(md5, new Long(System.currentTimeMillis()));

      // b) File location.
      if(file.getPath() != null)
      {digest(md5, file.getPath());}

      // c) File size.
      digest(md5, new Long(writer.getStream().getLength()));

      // d) Entries in the document information dictionary.
      for(Map.Entry<PdfName,PdfDirectObject> informationObjectEntry : file.getDocument().getInformation().getBaseDataObject().entrySet())
      {
        digest(md5, informationObjectEntry.getKey());
        digest(md5, informationObjectEntry.getValue());
      }
    }
    catch(UnsupportedEncodingException e)
    {throw new RuntimeException("File identifier digest failed.", e);}

    /*
      NOTE: File identifier is an array of two byte strings [PDF:1.7:10.3]:
       1) a permanent identifier based on the contents of the file at the time it was originally
         created. It does not change when the file is incrementally updated;
       2) a changing identifier based on the file's contents at the time it was last updated.
      When a file is first written, both identifiers are set to the same value. If both identifiers
      match when a file reference is resolved, it is very likely that the correct file has been
      found. If only the first identifier matches, a different version of the correct file has been
      found.
    */
    PdfString versionID = new PdfString(md5.digest(), SerializationModeEnum.Hex);
    getBaseDataObject().set(1, versionID);
    if(getBaseDataObject().get(0).equals(PdfString.Default))
    {getBaseDataObject().set(0, versionID);}
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
