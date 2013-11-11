/**
 * 
 */
package org.bitbucket.beatngu13.pdfbookmarkwizard;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.navigation.document.Destination.ModeEnum;
import org.pdfclown.files.SerializationModeEnum;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

/**
 * @author danielkraus1986@gmail.com
 *
 */
public class Controller {
	
	private static final Logger logger = Logger.getLogger(Controller.class.getName());
	
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File rootDirectory;
	@FXML
	private Pane mainView;
	@FXML
	private TextField directoryTextField;
	@FXML
	private Button directoryButton;
	@FXML
	private Text statusText;
	@FXML
	private Button runButton;
	
	public Controller() {
		directoryChooser.setTitle("Choose root directory");
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not load FXML file.", e);
		}
		
		directoryTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				runButton.setDisable(newValue.isEmpty());
			}
			
		});
		
		directoryButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				rootDirectory = directoryChooser.showDialog(mainView.getScene().getWindow());
				
				if (rootDirectory != null) {
					directoryTextField.setText(rootDirectory.getAbsolutePath());
				}
			}

		});
		
		runButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Add a modal warning dialog.
				Controller.this.run();
			}
			
		});
	}

	private void run() {
		// TODO What happens in case of a second call?
		rootDirectory = rootDirectory == null ? new File(directoryTextField.getText()) : 
			rootDirectory;
		
		if (rootDirectory.isDirectory()) {
			// TODO Add appropriate controls to MainView.
			Version version = VersionEnum.PDF14.getVersion();
			SerializationModeEnum serializationMode = SerializationModeEnum.Incremental;
			ModeEnum mode = ModeEnum.XYZ;
			Double zoom = null;
			Wizard wizard = new Wizard(rootDirectory, version, serializationMode, mode, zoom);
			
			try {
				wizard.call();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Fatal Wizard error.", e);
			}
		} else logger.severe(rootDirectory.getAbsolutePath() + " is not a valid directory.");
	}

	/**
	 * @return {@link #mainView}.
	 */
	public Pane getMainView() {
		return mainView;
	}
	
}
