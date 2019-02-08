package com.github.beatngu13.pdfzoomwizard.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;

public class WizardIT {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	File tempSamplePdf;
	List<PdfOutline> bookmarksBefore;

	@Before
	public void setUp() throws Exception {
		Path samplePdf = Paths.get("src/test/resources/sample.pdf");
		tempSamplePdf = temp.newFile("temp-sample.pdf");
		Files.copy(samplePdf, tempSamplePdf.toPath(), StandardCopyOption.REPLACE_EXISTING);

		bookmarksBefore = getAllBookmarks(tempSamplePdf);
	}

	@Test
	public void only_bookmark_zoom_level_should_change() throws Exception {
		// Given.
		Wizard cut = new Wizard(tempSamplePdf, null, "Inherit zoom");

		// When.
		cut.call();

		// Then.
		List<PdfOutline> bookmarksAfter = getAllBookmarks(tempSamplePdf);
		SoftAssertions softly = new SoftAssertions();
		for (int i = 0; i < bookmarksBefore.size(); i++) {
			PdfOutline bmBefore = bookmarksBefore.get(i);
			PdfOutline bmAfter = bookmarksAfter.get(i);
			PdfArray arrBefore = (PdfArray) bmBefore.getDestination().getPdfObject();
			PdfArray arrAfter = (PdfArray) bmAfter.getDestination().getPdfObject();

			// PdfIndirectReference#equals compares memory address.
			softly.assertThat(arrAfter.get(0)).hasToString(arrBefore.get(0).toString());
			softly.assertThat(arrAfter.get(2)).isEqualTo(arrBefore.get(2));
			softly.assertThat(arrAfter.get(1)).isEqualTo(PdfName.XYZ);
			softly.assertThat(arrAfter.get(3)).isEqualTo(PdfNull.PDF_NULL);
			softly.assertThat(arrAfter.get(4)).isEqualTo(PdfNull.PDF_NULL);
		}
		softly.assertAll();
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
