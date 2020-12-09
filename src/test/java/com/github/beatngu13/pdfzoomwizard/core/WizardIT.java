package com.github.beatngu13.pdfzoomwizard.core;

import com.github.beatngu13.pdfzoomwizard.TestUtil;
import com.itextpdf.kernel.pdf.PdfObject;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class WizardIT {

	@Nested
	class WithPdf {

		File pdf;

		@BeforeEach
		void setUp(@TempDir Path temp) throws Exception {
			Path original = Paths.get("src/test/resources/sample.pdf");
			pdf = temp.resolve("temp.pdf").toFile();
			Files.copy(original, pdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@ParameterizedTest
		@EnumSource(Zoom.class)
		void zoom_should_be_applied_properly(Zoom zoom) {
			String normalized = TestUtil.toStringNormalized(zoom);
			try (NamedEnvironment env = NamerFactory.withParameters(normalized)) {
				new Wizard(pdf, null, zoom).call();
				List<PdfObject> pdfObjects = TestUtil.getAllPdfObjects(pdf);
				Approvals.verify(pdfObjects);
			}
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
