package org.bitbucket.beatngu13.pdfbookmarkwizard;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Provides a JavaFX-based modal warning dialog, displaying a given message.
 * 
 * @author danielkraus1986@gmail.com
 *
 */
public class WarningController {
	
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(WarningController.class.getName());
	
	/**
	 * {@link Stage} instance.
	 */
	private Stage stage = new Stage();
	/**
	 * Indicates whether the user decided to proceed by clicking {@link #okButton}, or to cancel by 
	 * clicking {@link #cancelButton}.
	 */
	private boolean proceed = false;
	
	/**
	 * Warning dialog container.
	 */
	@FXML
	private Parent warningView;
	/**
	 * Displays additional warning information.
	 */
	@FXML
	private Text warningText;
	/**
	 * Sets {@link #proceed} to <code>true</code> and closes the warning dialog.
	 */
	@FXML
	private Button okButton;
	/**
	 * Leaves {@link #proceed} being <code>false</code> and closes the warning dialog.
	 */
	@FXML
	private Button cancelButton;
	
	/**
	 * Creates a new <code>WarningController</code> instance.
	 * 
	 * @param owner Calling window.
	 */
	public WarningController(Window owner) {
		stage.setTitle("Warning");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("WarningView.fxml"));
			
			// XXX Adding the controller within the FXML file fails.
			loader.setController(this);
			loader.load();
			stage.setScene(new Scene(warningView, 350.0, 185.0));
			warningText.wrappingWidthProperty().bind(
					stage.getScene().widthProperty().subtract(20.0));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not load FXML file.", e);
		}
		
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				proceed = true;
				stage.close();
			}
		});
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
	}
	
	/**
	 * Shows the modal warning dialog and blocks the calling Window.
	 * 
	 * @param message Warning message to be displayed.
	 * @return {@link #proceed}.
	 */
	public boolean show(String message) {
		proceed = false;
		
		warningText.setText(message);
		stage.showAndWait();
		
		return proceed;
	}

}
