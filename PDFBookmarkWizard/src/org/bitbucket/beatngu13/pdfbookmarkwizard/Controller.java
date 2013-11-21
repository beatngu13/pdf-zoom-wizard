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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Provides a JavaFX-based UI based on <code>View.fxml</code> and takes place as the controller 
 * according to the MVC pattern. 
 * 
 * @author danielkraus1986@gmail.com
 *
 */
public class Controller {
	
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(Controller.class.getName());
	
	/**
	 * @see #browseButton
	 */
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	/**
	 * Root directory to work with.
	 */
	private File rootDirectory;
	
	/**
	 * Main UI.
	 */
	@FXML
	private Parent view;
	/**
	 * Displaying absolute path of {@link #rootDirectory}.
	 */
	@FXML
	private TextField directoryTextField;
	/**
	 * Uses {@link #directoryChooser} to set {@link #rootDirectory}.
	 */
	@FXML
	private Button browseButton;
	/**
	 * {@link VersionEnum} to use within {@link #run()}.
	 */
	@FXML
	private ChoiceBox<String> zoomChoiceBox;
	/**
	 * {@link SerializationModeEnum} to use within {@link #run()}.
	 */
	@FXML
	private ChoiceBox<String> versionChoiceBox;
	/**
	 * Displaying wheter a {@link Wizard} instance is (still) running or not.
	 */
	@FXML
	private Text statusText;
	/**
	 * Calls {@link #run()}.
	 */
	@FXML
	private Button runButton;
	
	/**
	 * Creates a new <code>Controller</code> instance.
	 */
	public Controller() {
		directoryChooser.setTitle("Choose root directory");
		
		try {
			// TODO Why is the static loader not working?
			FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
			// TODO Add controller within FXML file.
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
		
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				rootDirectory = directoryChooser.showDialog(view.getScene().getWindow());
				
				if (rootDirectory != null) {
					directoryTextField.setText(rootDirectory.getAbsolutePath());
				}
			}

		});
		
		runButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Extract warning dialog.
//				Stage warningDialog = new Stage();
//				warningDialog.initOwner(runButton.getScene().getWindow());
//				warningDialog.initModality(Modality.WINDOW_MODAL);
//				
//				GridPane gridPane = new GridPane();
//				gridPane.setAlignment(Pos.CENTER);
//				gridPane.add(new Text("Warning! All PDF files within " + rootDirectory
//						+ " will be overwritten!"), 0, 0);
//				gridPane.add(new Button("OK"), 1, 1);
//				
//				Scene warningScene = new Scene(gridPane);
//				warningDialog.setScene(warningScene);
//				warningDialog.show();
//				
				Controller.this.run();
			}
			
		});
	}
	
	/**
	 * Creates a new {@link Wizard} instance and calls it to run modification on 
	 * {@link #rootDirectory}.
	 */
	private void run() {
		if (rootDirectory == null || rootDirectory.getAbsolutePath() 
				!= directoryTextField.getText()) {
			rootDirectory = new File(directoryTextField.getText());
		}
		
		if (rootDirectory.isDirectory()) {
			Wizard wizard = new Wizard(rootDirectory, computeVersion(), 
					SerializationModeEnum.Incremental, computeModeEnum(), computeZoom());
			statusText.textProperty().bind(wizard.messageProperty());
			
			try {
				wizard.call();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Fatal Wizard error.", e);
			}
		} else logger.severe("\"" + rootDirectory.getAbsolutePath() 
				+ "\" is not a valid directory.");
	}
	
	// TODO Add more values.
	/**
	 * Computes {@link Version} according to {@link #versionChoiceBox} when a new Wizard task is 
	 * started.
	 * 
	 * @return Chosen version number. <code>null</code> should'nt be returned due to the predefined
	 * values within the choice box.
	 */
	private Version computeVersion() {
		Version version = null;
		
		switch (versionChoiceBox.getValue()) {
		case "1.0":
			version = VersionEnum.PDF10.getVersion();
			break;
		case "1.1":
			version = VersionEnum.PDF11.getVersion();
			break;
		case "1.2":
			version = VersionEnum.PDF12.getVersion();
			break;
		case "1.3":
			version = VersionEnum.PDF13.getVersion();
			break;
		case "1.4":
			version = VersionEnum.PDF14.getVersion();
			break;
		case "1.5":
			version = VersionEnum.PDF15.getVersion();
			break;
		case "1.6":
			version = VersionEnum.PDF16.getVersion();
			break;
		case "1.7":
			version = VersionEnum.PDF17.getVersion();
		}
		
		return version;
	}
	
	// TODO Add values.
	/**
	 * Computes {@link ModeEnum} according to {@link #zoomChoiceBox} when a new Wizard task is 
	 * started.
	 * 
	 * @return Chosen mode. <code>null</code> should'nt be returned due to the predefined values 
	 * within the choice box.
	 */
	private ModeEnum computeModeEnum() {
		ModeEnum mode = null;
		
		switch (zoomChoiceBox.getValue()) {
		case "Inherit zoom":
			mode = ModeEnum.XYZ;
		}
		
		return mode;
	}
	
	// TODO Add values.
	/**
	 * Computes zoom according to {@link #zoomChoiceBox} when a new Wizard task is started.
	 * 
	 * @return Chosen zoom.
	 */
	private Double computeZoom() {
		Double zoom = null;
		
		switch (zoomChoiceBox.getValue()) {
		case "Inherit zoom":
			zoom = null;
		}
		
		return zoom;
	}

	/**
	 * @return {@link #view}.
	 */
	public Parent getView() {
		return view;
	}
	
}
