package com.github.beatngu13.pdfzoomwizard.core;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import org.approvaltests.Approvals;
import org.approvaltests.namer.NamedEnvironment;
import org.approvaltests.namer.NamerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatCode;

class WizardIT {

	@Nested
	class WithPdf {

		File tempSamplePdf;

		@BeforeEach
		void setUp(@TempDir Path temp) throws Exception {
			Path samplePdf = Paths.get("src/test/resources/sample.pdf");
			tempSamplePdf = temp.resolve("temp-sample.pdf").toFile();
			Files.copy(samplePdf, tempSamplePdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@ParameterizedTest
		@EnumSource(Zoom.class)
		void zoom_should_be_applied_properly(Zoom zoom) throws Exception {
			try (NamedEnvironment env = NamerFactory.withParameters(zoom)) {
				verify(zoom);
			}
		}

		void verify(Zoom zoom) throws Exception {
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

	@Nested
	class WithNonPdf {

		@Test
		void should_not_crash_execution(@TempDir Path temp) throws Exception {
			File nonPdf = temp.resolve("foo.bar").toFile();
			nonPdf.createNewFile();
			Wizard cut = new Wizard(nonPdf, null, Zoom.ACTUAL_SIZE);
			assertThatCode(cut::call).doesNotThrowAnyException();
		}

	}

}
