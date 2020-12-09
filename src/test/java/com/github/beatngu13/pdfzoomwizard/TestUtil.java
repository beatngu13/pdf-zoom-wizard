package com.github.beatngu13.pdfzoomwizard;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TestUtil {

	public static List<PdfOutline> getAllBookmarks(File pdf) throws Exception {
		PdfDocument doc = new PdfDocument(new PdfReader(pdf));
		List<PdfOutline> outlines = doc.getOutlines(true).getAllChildren();
		List<PdfOutline> allBookmarks = getAllBookmarks(outlines);
		doc.close();
		return allBookmarks;
	}

	public static List<PdfOutline> getAllBookmarks(List<PdfOutline> outlines) {
		List<PdfOutline> allBookmarks = new ArrayList<>();

		for (PdfOutline bookmark : outlines) {
			allBookmarks.add(bookmark);

			List<PdfOutline> children = bookmark.getAllChildren();
			if (!children.isEmpty()) {
				allBookmarks.addAll(getAllBookmarks(children));
			}
		}

		return allBookmarks;
	}

}
