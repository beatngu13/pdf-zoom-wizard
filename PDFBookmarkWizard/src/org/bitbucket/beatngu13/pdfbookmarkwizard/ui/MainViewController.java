package org.bitbucket.beatngu13.pdfbookmarkwizard.ui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbucket.beatngu13.pdfbookmarkwizard.core.Wizard;
import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.interaction.navigation.document.Destination.ModeEnum;
import org.pdfclown.files.SerializationModeEnum;

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
	 * <code>TextField</code> for the infix to use in case of creating copies, <code>null</code> if 
	 * the original document will be overwritten.
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
		runButton.disableProperty().bind(directoryTextField.textProperty().isEqualTo(""));
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
							+ "\" will be ";
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
			stateText.setText("INVALID COPIES INFIX");
			logger.info("Invald copies infix.");
		}
		
		return valid;
	}
	
	/**
	 * Creates a new {@link Wizard} instance and starts modification on {@link #rootDirectory}.
	 */
	private void run() {
		String copiesInfix = copiesCheckBox.isSelected() ? copiesTextField.getText() : null;
		Wizard wizard = new Wizard(rootDirectory, copiesInfix, computeVersion(), 
		SerializationModeEnum.Standard, computeModeEnum(), computeZoom());
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
	 * Computes {@link Version} according to the picked value in {@link #versionChoiceBox}.
	 * 
	 * @return Chosen version number, <code>null</code> if "Retain existing" was picked.
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
	
	/**
	 * Computes {@link ModeEnum} according to the picked value in {@link #zoomChoiceBox}.
	 * 
	 * @return Chosen mode.
	 */
	private ModeEnum computeModeEnum() {
		ModeEnum mode = null;
		
		switch (zoomChoiceBox.getValue()) {
		case "Fit page":
			mode = ModeEnum.Fit;
			break;
		case "Actual size":
			mode = ModeEnum.XYZ;
			break;
		case "Fit width":
			mode = ModeEnum.FitHorizontal;
			break;
		case "Fit visible":
			mode = ModeEnum.FitBoundingBoxHorizontal;
			break;
		case "Inherit zoom":
			mode = ModeEnum.XYZ;
		}
		
		return mode;
	}
	
	/**
	 * Computes zoom according to {@link #zoomChoiceBox} when a new Wizard task is started.
	 * 
	 * @return Chosen zoom, <code>null</code> if "Actual size" or "Fit visible" weren't picked.
	 */
	private Double computeZoom() {
		Double zoom = null;
		
		switch (zoomChoiceBox.getValue()) {
		case "Actual size":
			zoom = 1.0;
			break;
		case "Fit visible":
			zoom = 0.0;
		}
		
		return zoom;
	}

	/**
	 * @return {@link #mainView}.
	 */
	public Parent getMainView() {
		return mainView;
	}
	
}
