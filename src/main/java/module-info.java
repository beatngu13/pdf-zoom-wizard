module com.github.beatngu13.pdfzoomwizard {

	requires java.prefs;
	requires javafx.controls;
	requires javafx.fxml;
	requires pdfclown;
	requires slf4j.api;

	exports com.github.beatngu13.pdfzoomwizard;

	opens com.github.beatngu13.pdfzoomwizard.ui to javafx.fxml;
	opens com.github.beatngu13.pdfzoomwizard.core to javafx.fxml;

}
