package com.github.beatngu13.pdfzoomwizard;

import com.github.beatngu13.pdfzoomwizard.core.Zoom;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestUtil {

	private TestUtil() {
	}

	public static String toStringNormalized(Zoom zoom) {
		return zoom.toString()
				.toLowerCase()
				.replaceAll(" ", "_");
	}

	public static List<PdfObject> getAllPdfObjects(File pdf) {
		return getAllBookmarks(pdf)
				.map(PdfOutline::getDestination)
				.map(PdfDestination::getPdfObject)
				.collect(Collectors.toList());
	}

	private static Stream<PdfOutline> getAllBookmarks(File pdf) {
		try (PdfDocument doc = new PdfDocument(new PdfReader(pdf))) {
			return doc.getOutlines(true)
					.getAllChildren()
					.stream()
					.flatMap(TestUtil::streamRecursive);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Stream<PdfOutline> streamRecursive(PdfOutline bookmark) {
		Stream<PdfOutline> allChildren = bookmark.getAllChildren()
				.stream()
				.flatMap(TestUtil::streamRecursive);
		return Stream.concat(Stream.of(bookmark), allChildren);
	}

}
