package com.github.beatngu13.pdfzoomwizard.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

class WizardIT {

	File tempSamplePdf;

	@BeforeEach
	void setUp(@TempDir Path temp) throws Exception {
		Path samplePdf = Paths.get("src/test/resources/sample.pdf");
		tempSamplePdf = temp.resolve("temp-sample.pdf").toFile();
		Files.copy(samplePdf, tempSamplePdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	// ParameterizedTest currently not supported by ApprovalTests (see
	// https://github.com/approvals/ApprovalTests.Java/issues/36/).

	@Test
	void actual_size_should_be_applied_properly() throws Exception {
		test(Zoom.ACTUAL_SIZE);
	}

	@Test
	void fit_page_should_be_applied_properly() throws Exception {
		test(Zoom.FIT_PAGE);
	}

	@Test
	void fit_visible_should_be_applied_properly() throws Exception {
		test(Zoom.FIT_VISIBLE);
	}

	@Test
	void fit_width_should_be_applied_properly() throws Exception {
		test(Zoom.FIT_WIDTH);
	}

	@Test
	void inherit_zoom_should_be_applied_properly() throws Exception {
		test(Zoom.INHERIT_ZOOM);
	}

	void test(Zoom zoom) throws Exception {
		new Wizard(tempSamplePdf, null, zoom).call();
		List<PdfObject> pdfObjects = getAllBookmarks(tempSamplePdf).stream() //
				.map(PdfOutline::getDestination) //
				.map(PdfDestination::getPdfObject) //
				.collect(Collectors.toList());
		Approvals.verify(pdfObjects);
	}

	List<PdfOutline> getAllBookmarks(File pdf) throws Exception {
		PdfDocument doc = new PdfDocument(new PdfReader(tempSamplePdf));
		List<PdfOutline> outlines = doc.getOutlines(true).getAllChildren();
		List<PdfOutline> allBookmarks = getAllBookmarks(outlines);
		doc.close();
		return allBookmarks;
	}

	List<PdfOutline> getAllBookmarks(List<PdfOutline> outlines) {
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
