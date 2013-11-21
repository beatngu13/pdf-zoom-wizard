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

package org.pdfclown.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pdfclown.bytes.Buffer;
import org.pdfclown.bytes.IOutputStream;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.Pages;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfObject;
import org.pdfclown.objects.PdfReference;
import org.pdfclown.objects.PdfStream;

/**
  Tool for page management.

  @author Stefano Chizzolini (http://www.stefanochizzolini.it)
  @since 0.0.6
  @version 0.1.2, 12/28/12
*/
public final class PageManager
{
  /*
    NOTE: As you can read on the PDF Clown's User Guide, referential operations on high-level object such as pages
    can be done at two levels:
      1. shallow, involving page references but NOT their data within the document;
      2. deep, involving page data within the document.
    This means that, for example, if you remove a page reference (shallow level) from the pages collection,
    the data of that page (deep level) are still within the document!
  */

  // <class>
  // <static>
  // <interface>
  // <public>
  /**
    Gets the data size of the specified page expressed in bytes.

    @param page Page whose data size has to be calculated.
  */
  public static long getSize(
    Page page
    )
  {return getSize(page, new HashSet<PdfReference>());}

  /**
    Gets the data size of the specified page expressed in bytes.

    @param page Page whose data size has to be calculated.
    @param visitedReferences References to data objects excluded from calculation.
      This set is useful, for example, to avoid recalculating the data size of shared resources.
      During the operation, this set is populated with references to visited data objects.
  */
  public static long getSize(
    Page page,
    Set<PdfReference> visitedReferences
    )
  {return getSize(page.getBaseObject(), visitedReferences, true);}
  // </public>

  // <private>
  /**
    Gets the data size of the specified object expressed in bytes.

    @param object Data object whose size has to be calculated.
    @param visitedReferences References to data objects excluded from calculation.
      This set is useful, for example, to avoid recalculating the data size of shared resources.
      During the operation, this set is populated with references to visited data objects.
    @param isRoot Whether this data object represents the page root.
  */
  private static long getSize(
    PdfDirectObject object,
    Set<PdfReference> visitedReferences,
    boolean isRoot
    )
  {
    long dataSize = 0;
    {
      PdfDataObject dataObject = PdfObject.resolve(object);

      // 1. Evaluating the current object...
      if(object instanceof PdfReference)
      {
        PdfReference reference = (PdfReference)object;
        if(visitedReferences.contains(reference))
          return 0; // Avoids circular references.

        if(dataObject instanceof PdfDictionary
          && PdfName.Page.equals(((PdfDictionary)dataObject).get(PdfName.Type))
          && !isRoot)
          return 0; // Avoids references to other pages.

        visitedReferences.add(reference);

        // Calculate the data size of the current object!
        IOutputStream buffer = new Buffer();
        reference.getIndirectObject().writeTo(buffer, reference.getFile());
        dataSize += buffer.getLength();
      }

      // 2. Evaluating the current object's children...
      Collection<PdfDirectObject> values = null;
      {
        if(dataObject instanceof PdfStream)
        {dataObject = ((PdfStream)dataObject).getHeader();}
        if(dataObject instanceof PdfDictionary)
        {values = ((PdfDictionary)dataObject).values();}
        else if(dataObject instanceof PdfArray)
        {values = (PdfArray)dataObject;}
      }
      if(values != null)
      {
        // Calculate the data size of the current object's children!
        for(PdfDirectObject value : values)
        {dataSize += getSize(value, visitedReferences, false);}
      }
    }
    return dataSize;
  }
  // </private>
  // </interface>
  // </static>

  // <dynamic>
  // <fields>
  private Document document;
  private Pages pages;
  // </fields>

  // <constructors>
  public PageManager(
    )
  {this(null);}

  public PageManager(
    Document document
    )
  {setDocument(document);}
  // </constructors>

  // <interface>
  // <public>
  /**
    Appends a document to the end of the document.

    @param document Document to be added.
  */
  public void add(
    Document document
    )
  {add(document.getPages());}

  /**
    Inserts a document at the specified position in the document.

    @param index Position at which the document has to be inserted.
    @param document Document to be inserted.
  */
  public void add(
    int index,
    Document document
    )
  {add(index,document.getPages());}

  /**
    Appends a collection of pages to the end of the document.

    @param pages Pages to be added.
  */
  @SuppressWarnings("unchecked")
  public void add(
    Collection<Page> pages
    )
  {
    // Add the source pages to the document (deep level)!
    Collection<Page> importedPages = (Collection<Page>)document.include(pages); // NOTE: Alien pages MUST be contextualized (i.e. imported).

    // Add the imported pages to the pages collection (shallow level)!
    this.pages.addAll(importedPages);
  }

  /**
    Inserts a collection of pages at the specified position in the document.

    @param index Position at which the pages have to be inserted.
    @param pages Pages to be inserted.
  */
  @SuppressWarnings("unchecked")
  public void add(
    int index,
    Collection<Page> pages
    )
  {
    // Add the source pages to the document (deep level)!
    Collection<Page> importedPages = (Collection<Page>)document.include(pages); // NOTE: Alien pages MUST be contextualized (i.e. imported).

    // Add the imported pages to the pages collection (shallow level)!
    if(index >= this.pages.size())
    {this.pages.addAll(importedPages);}
    else
    {this.pages.addAll(index, importedPages);}
  }

  /**
    Extracts a page range from the document.

    @param startIndex The beginning index, inclusive.
    @param endIndex The ending index, exclusive.
    @return Extracted page range.
  */
  @SuppressWarnings("unchecked")
  public Document extract(
    int startIndex,
    int endIndex
    )
  {
    @SuppressWarnings("resource")
    Document extractedDocument = new File().getDocument();
    {
      // Add the pages to the target file!
      /*
        NOTE: To be added to an alien document,
        pages MUST be contextualized within it first,
        then added to the target pages collection.
      */
      extractedDocument.getPages().addAll(
        (Collection<Page>)extractedDocument.include(
          pages.subList(startIndex,endIndex)
          )
        );
    }
    return extractedDocument;
  }

  /**
    Moves a page range to a target position within the document.

    @param startIndex The beginning index, inclusive.
    @param endIndex The ending index, exclusive.
    @param targetIndex The target index.
  */
  public void move(
    int startIndex,
    int endIndex,
    int targetIndex
    )
  {
    int pageCount = pages.size();

    List<Page> movingPages = pages.subList(startIndex, endIndex);

    // Temporarily remove the pages from the pages collection!
    /*
      NOTE: Shallow removal (only page references are removed, as their data are kept in the document).
    */
    pages.removeAll(movingPages);

    // Adjust indexes!
    pageCount -= movingPages.size();
    if(targetIndex > startIndex)
    {targetIndex -= movingPages.size();} // Adjusts the target position due to shifting for temporary page removal.

    // Reinsert the pages at the target position!
    /*
      NOTE: Shallow addition (only page references are added, as their data are already in the document).
    */
    if(targetIndex >= pageCount)
    {pages.addAll(movingPages);}
    else
    {pages.addAll(targetIndex, movingPages);}
  }

  /**
    Gets the document being managed.
  */
  public Document getDocument(
    )
  {return document;}

  /**
    Removes a page range from the document.

    @param startIndex The beginning index, inclusive.
    @param endIndex The ending index, exclusive.
  */
  public void remove(
    int startIndex,
    int endIndex
    )
  {
    List<Page> removingPages = pages.subList(startIndex, endIndex);

    // Remove the pages from the pages collection!
    /* NOTE: Shallow removal. */
    pages.removeAll(removingPages);

    // Remove the pages from the document (decontextualize)!
    /* NOTE: Deep removal. */
    document.exclude(removingPages);
  }

  /**
    Sets the document to manipulate.
  */
  public void setDocument(
    Document value
    )
  {
    document = value;
    pages = document.getPages();
  }

  /**
    Bursts the document into single-page documents.

    @return Split subdocuments.
  */
  public List<Document> split(
    )
  {
    List<Document> documents = new ArrayList<Document>();
    for(Page page : pages)
    {
      @SuppressWarnings("resource")
      Document pageDocument = new File().getDocument();
      pageDocument.getPages().add(page.clone(pageDocument));
      documents.add(pageDocument);
    }
    return documents;
  }

  /**
    Splits the document into multiple subdocuments delimited by the specified page indexes.

    @param indexes Split page indexes.
    @return Split subdocuments.
  */
  public List<Document> split(
    int... indexes
    )
  {
    List<Document> documents = new ArrayList<Document>();
    {
      int startIndex = 0;
      for(int index : indexes)
      {
        documents.add(extract(startIndex, index));
        startIndex = index;
      }
      documents.add(extract(startIndex, pages.size()));
    }
    return documents;
  }

  /**
    Splits the document into multiple subdocuments on maximum file size.

    @param maxDataSize Maximum data size (expressed in bytes) of target files.
      Note that resulting files may be a little bit larger than this value, as file data include (along with actual page data)
      some extra structures such as cross reference tables.
    @return Split documents.
  */
  public List<Document> split(
    long maxDataSize
    )
  {
    List<Document> documents = new ArrayList<Document>();
    {
      int startPageIndex = 0;
      long incrementalDataSize = 0;
      Set<PdfReference> visitedReferences = new HashSet<PdfReference>();
      for(Page page : pages)
      {
        long pageDifferentialDataSize = getSize(page, visitedReferences);
        incrementalDataSize += pageDifferentialDataSize;
        if(incrementalDataSize > maxDataSize) // Data size limit reached.
        {
          int endPageIndex = page.getIndex();

          // Split the current document page range!
          documents.add(extract(startPageIndex, endPageIndex));

          startPageIndex = endPageIndex;
          incrementalDataSize = getSize(page, visitedReferences = new HashSet<PdfReference>());
        }
      }
      // Split the last document page range!
      documents.add(extract(startPageIndex, pages.size()));
    }
    return documents;
  }
  // </public>
  // </interface>
  // </dynamic>
  // </class>
}
