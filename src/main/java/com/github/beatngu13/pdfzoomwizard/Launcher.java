package com.github.beatngu13.pdfzoomwizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launches the Wizard.
 *
 * @author Daniel Kraus
 */
public class Launcher extends Application {

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.info("Launching PDF Zoom Wizard (version {}).", VersionUtil.getVersion());
		var loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
		primaryStage.setTitle("PDF Zoom Wizard");
		primaryStage.setScene(new Scene(loader.load()));
		primaryStage.show();
	}

}
