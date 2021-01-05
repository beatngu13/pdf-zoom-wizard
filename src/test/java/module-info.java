open module com.github.beatngu13.pdfzoomwizard {

	requires java.prefs;
	requires javafx.controls;
	requires javafx.fxml;
	requires pdfclown;
	requires slf4j.api;

	exports com.github.beatngu13.pdfzoomwizard;

	// test

	requires org.junit.jupiter.engine;
	requires org.junit.jupiter.api;
	requires org.junit.jupiter.params;
	requires org.assertj.core;
	requires org.mockito;
	// transitive via org.mockito
	requires net.bytebuddy;
	requires net.bytebuddy.agent;
	requires approvaltests;
	// transitive via approvaltests
	requires java.sql;

}
