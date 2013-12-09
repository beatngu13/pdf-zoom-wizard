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
import javafx.scene.control.ChoiceBox;
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
public class ViewController {
	
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(ViewController.class.getName());
	
	/**
	 * @see #browseButton
	 */
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	/**
	 * Root directory to work with.
	 */
	private File rootDirectory;
	/**
	 * Displays a modal warning dialog when {@link #runButton} is being clicked.
	 */
	private WarningController warningController;
	
	/**
	 * Actual UI content.
	 */
	@FXML
	private Parent view;
	/**
	 * Displays absolute path of {@link #rootDirectory}.
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
	 * Displays whether a {@link Wizard} instance is (still) running or not.
	 */
	@FXML
	private Text stateText;
	/**
	 * Calls {@link #run()} if the confirms to proceed within the following modal warning dialog.
	 */
	@FXML
	private Button runButton;
	
	/**
	 * Creates a new <code>ViewController</code> instance.
	 */
	public ViewController() {
		directoryChooser.setTitle("Choose root directory");
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
			
			// FIXME Adding controller within FXML file is not working.
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
				if (rootDirectory == null
						|| !rootDirectory.getAbsolutePath().equals(directoryTextField.getText())) {
					rootDirectory = new File(directoryTextField.getText());
				}
				// TODO Bad style?
				warningController = warningController == null ? 
						new WarningController(runButton.getScene().getWindow()) : warningController;
				String warningMessage = "All files within \"" + rootDirectory.getAbsolutePath() 
						+ "\" will be overwritten! \n" + "Are you sure to proceed?";
				
				if (warningController.show(warningMessage)) {
					ViewController.this.run();
				}
			}
			
		});
	}
	
	/**
	 * Creates a new {@link Wizard} instance and calls it to run modification on 
	 * {@link #rootDirectory}.
	 */
	private void run() {
		if (rootDirectory.isDirectory()) {
			Wizard wizard = new Wizard(rootDirectory, computeVersion(), 
					SerializationModeEnum.Incremental, computeModeEnum(), computeZoom());
			stateText.textProperty().bind(wizard.messageProperty());
			
			Thread thread = new Thread(wizard);
			thread.setDaemon(true);
			thread.start();
		} else {
			// TODO Add appropriate user message or make directoryTextField uneditable.
			logger.severe("\"" + rootDirectory.getAbsolutePath()
					+ "\" is not a valid directory.");
		}
	}
	
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
	
	/**
	 * Computes {@link ModeEnum} according to {@link #zoomChoiceBox} when a new Wizard task is 
	 * started.
	 * 
	 * @return Chosen mode. <code>null</code> won't be returned due to predefined values within the
	 * choice box.
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
	 * @return Chosen zoom.
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
	 * @return {@link #view}.
	 */
	public Parent getView() {
		return view;
	}
	
}
