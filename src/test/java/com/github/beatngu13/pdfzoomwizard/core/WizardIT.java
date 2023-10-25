package com.github.beatngu13.pdfzoomwizard.core;

import com.github.beatngu13.pdfzoomwizard.TestUtil;
import com.itextpdf.kernel.pdf.PdfObject;
import org.approvaltests.Approvals;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class WizardIT {

	@Nested
	class WithPdf {

		String pdfPrefix = "temp";
		String pdfSuffix = ".pdf";
		String pdfName = pdfPrefix + pdfSuffix;
		File pdf;

		@BeforeEach
		void setUp(@TempDir Path temp) throws Exception {
			var samplePdf = Paths.get("src/test/resources/sample.pdf");
			pdf = temp.resolve(pdfName).toFile();
			Files.copy(samplePdf, pdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@ParameterizedTest
		@EnumSource(Zoom.class)
		void zoom_should_be_applied_properly(Zoom zoom) {
			var zoomName = TestUtil.toStringNormalized(zoom);
			new Wizard(pdf, null, zoom).call();
			List<PdfObject> pdfObjects = TestUtil.getAllPdfObjects(pdf);
			Approvals.verify(pdfObjects, Approvals.NAMES.withParameters(zoomName));
		}

		@Test
		void should_overwrite_pdf_if_infix_is_null() {
			new Wizard(pdf, null, Zoom.INHERIT_ZOOM).call();
			assertThat(pdf.getParentFile())
					.isDirectoryContaining(file -> file.getName().equals(pdfName));
		}

		@Test
		void should_copy_pdf_if_infix_is_not_null() {
			var pdfInfix = "-infix";
			new Wizard(pdf, pdfInfix, Zoom.INHERIT_ZOOM).call();
			var pdfCopyName = pdfPrefix + pdfInfix + pdfSuffix;
			assertThat(pdf.getParentFile())
					.isDirectoryContaining(file -> file.getName().equals(pdfName))
					.isDirectoryContaining(file -> file.getName().equals(pdfCopyName));
		}

	}

	@Nested
	class WithNonPdf {

		@Test
		void should_not_crash_execution(@TempDir Path temp) throws Exception {
			var nonPdf = temp.resolve("foo.bar").toFile();
			nonPdf.createNewFile();
			var cut = new Wizard(nonPdf, null, Zoom.ACTUAL_SIZE);
			assertThatCode(cut::call).doesNotThrowAnyException();
		}

	}

}
