package com.github.beatngu13.pdfzoomwizard;

import com.github.beatngu13.pdfzoomwizard.core.Zoom;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TestUtil {

	public String toStringNormalized(Zoom zoom) {
		// Zoom name
		String normalized = zoom.toString();
		// zoom name
		normalized = normalized.toLowerCase();
		// zoom_name
		normalized = normalized.replaceAll(" ", "_");
		return normalized;
	}

	public static List<PdfObject> getAllPdfObjects(File pdf) {
		return getAllBookmarks(pdf).stream()
				.map(PdfOutline::getDestination)
				.map(PdfDestination::getPdfObject)
				.collect(Collectors.toList());
	}

	private static List<PdfOutline> getAllBookmarks(File pdf) {
		try (PdfDocument doc = new PdfDocument(new PdfReader(pdf))) {
			List<PdfOutline> outlines = doc.getOutlines(true).getAllChildren();
			List<PdfOutline> allBookmarks = getAllBookmarks(outlines);
			return allBookmarks;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static List<PdfOutline> getAllBookmarks(List<PdfOutline> outlines) {
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
