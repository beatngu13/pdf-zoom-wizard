package org.bitbucket.beatngu13.pdfbookmarkwizard.ui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbucket.beatngu13.pdfbookmarkwizard.core.Wizard;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

/**
 * Provides a JavaFX-based Wizard UI.
 * 
 * @author danielkraus1986@gmail.com
 *
 */
public class MainViewController {
	
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(MainViewController.class.getName());
	
	/**
	 * @see #browseButton
	 */
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	/**
	 * Root directory to work with.
	 */
	private File rootDirectory;
	/**
	 * Occurs when {@link #runButton} is being clicked.
	 */
	private WarningViewController warningController;
	
	/**
	 * Wizard UI container.
	 */
	@FXML
	private Parent mainView;
	/**
	 * <code>TextField</code> for {@link #rootDirectory}.
	 */
	@FXML
	private TextField directoryTextField;
	/**
	 * Uses {@link #directoryChooser} to set {@link #rootDirectory}.
	 */
	@FXML
	private Button browseButton;
	/**
	 * <code>TextField</code> for the infix which is used in case of creating copies.
	 */
	@FXML
	private TextField copiesTextField;
	/**
	 * Enables the creation of copies.
	 */
	@FXML
	private CheckBox copiesCheckBox;
	/**
	 * {@link VersionEnum} to use for modification.
	 */
	@FXML
	private ChoiceBox<String> zoomChoiceBox;
	/**
	 * {@link SerializationModeEnum} to use for modification.
	 */
	@FXML
	private ChoiceBox<String> versionChoiceBox;
	/**
	 * Displays the current state of the Wizard. Basically the values of {@link State} are used.
	 */
	@FXML
	private Text stateText;
	/**
	 * Calls {@link #run()} if the user confirms to proceed.
	 */
	@FXML
	private Button runButton;
	
	/**
	 * Creates a new <code>MainViewController</code> instance.
	 */
	public MainViewController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
			
			// TODO Adding the controller within the FXML file fails.
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not load FXML file.", e);
		}
		directoryChooser.setTitle("Choose root directory");
		runButton.disableProperty().bind(directoryTextField.textProperty().isEqualTo("").or(
				stateText.textProperty().isEqualTo(State.RUNNING.toString())));
		copiesTextField.disableProperty().bind(copiesCheckBox.selectedProperty().not());
		
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			
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
				if (validateInput()) {
					// TODO Bad style.
					warningController = warningController == null ? new WarningViewController(
							runButton.getScene().getWindow()) : warningController;
					String messagePrefix = "All files in \"" + rootDirectory.getAbsolutePath() 
							+ "\" and its enclosing subdirectories will be ";
					String messageInfix = !copiesCheckBox.isSelected() ? "overwritten!" 
							: "copied!";
					String messageSuffix = "\n\nAre you sure to proceed?";
					
					if (warningController.show(messagePrefix + messageInfix + messageSuffix)) {
						MainViewController.this.run();
					}
				}
			}
			
		});
	}
	
	/**
	 * Validates the given UI input.
	 * 
	 * @return <code>true</code> if everything is valid, else <code>false</code>.
	 */
	private boolean validateInput() {
		boolean valid = true;
		
		if (rootDirectory == null
				|| !rootDirectory.getAbsolutePath().equals(directoryTextField.getText())) {
			rootDirectory = new File(directoryTextField.getText());
		}
		
		if (!rootDirectory.isDirectory()) {
			valid = false;
			stateText.setText("INVALID DIRECTORY");
			logger.info("\"" + rootDirectory.getAbsolutePath()
					+ "\" is an invalid directory.");
		}
		
		if (copiesCheckBox.isSelected() && copiesTextField.getText().isEmpty()) {
			valid = false;
			stateText.setText("INVALID FILENAME INFIX");
			logger.info("Invald filename infix.");
		}
		
		return valid;
	}
	
	/**
	 * Creates a new {@link Wizard} instance and starts modification on {@link #rootDirectory}.
	 */
	private void run() {
		String filenameInfix = copiesCheckBox.isSelected() ? copiesTextField.getText() : null;
		Wizard wizard = new Wizard(rootDirectory, filenameInfix, zoomChoiceBox.getValue(), 
				versionChoiceBox.getValue());
		Thread thread = new Thread(wizard);
		
		wizard.messageProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				MainViewController.this.stateText.setText(newValue);
			}
		});
		
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @return {@link #mainView}.
	 */
	public Parent getMainView() {
		return mainView;
	}
	
}
