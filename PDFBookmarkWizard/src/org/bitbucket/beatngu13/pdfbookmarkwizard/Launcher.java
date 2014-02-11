package org.bitbucket.beatngu13.pdfbookmarkwizard;

import org.bitbucket.beatngu13.pdfbookmarkwizard.ui.MainViewController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches the Wizard.
 * 
 * @author danielkraus1986@gmail.com
 *
 */
public class Launcher extends Application {
	
	public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController controller = new MainViewController();
		
		primaryStage.setTitle("PDF Bookmark Wizard");
        primaryStage.setScene(new Scene(controller.getMainView(), 450.0, 200.0));
        primaryStage.show();
	}

}
