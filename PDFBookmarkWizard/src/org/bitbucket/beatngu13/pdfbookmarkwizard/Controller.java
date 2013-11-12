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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

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
				// TODO Add a warning modal dialog.
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
			// TODO Add appropriate controls set these options.
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
	 * @return {@link #view}.
	 */
	public Parent getView() {
		return view;
	}
	
}
