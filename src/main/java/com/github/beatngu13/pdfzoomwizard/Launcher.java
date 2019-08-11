package com.github.beatngu13.pdfzoomwizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Launches the Wizard.
 * 
 * @author Daniel Kraus
 *
 */
@Slf4j
public class Launcher extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		log.info("Launching PDF Zoom Wizard v{}.", getClass().getPackage().getImplementationVersion());
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
		primaryStage.setTitle("PDF Zoom Wizard");
		primaryStage.setScene(new Scene(loader.load()));
		primaryStage.show();
	}

}
