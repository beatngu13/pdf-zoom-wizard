package com.github.beatngu13.pdfzoomwizard.core;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Stream;

final class WizardITUtil {

	record Bookmark(String title, String data) {
	}

	private WizardITUtil() {
	}

	static List<Bookmark> getBookmarks(File pdf) {
		return streamOutlines(pdf)
				.map(WizardITUtil::toBookmark)
				.toList();
	}

	private static Stream<PdfOutline> streamOutlines(File pdf) {
		try (PdfDocument doc = new PdfDocument(new PdfReader(pdf))) {
			PdfOutline outlines = doc.getOutlines(true);
			if (outlines == null) {
				return Stream.empty();
			}
			return outlines
					.getAllChildren()
					.stream()
					.flatMap(WizardITUtil::streamOutlines);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Stream<PdfOutline> streamOutlines(PdfOutline outline) {
		Stream<PdfOutline> allChildren = outline.getAllChildren()
				.stream()
				.flatMap(WizardITUtil::streamOutlines);
		return Stream.concat(Stream.of(outline), allChildren);
	}

	private static Bookmark toBookmark(PdfOutline outline) {
		String title = outline.getTitle();
		PdfDestination destination = outline.getDestination();
		String data = destination == null
				? "No destination"
				: destination.getPdfObject().toString();
		return new Bookmark(title, data);
	}

}
