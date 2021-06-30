package com.github.beatngu13.pdfzoomwizard.ui;

import com.github.beatngu13.pdfzoomwizard.core.Wizard;
import com.github.beatngu13.pdfzoomwizard.core.Zoom;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Provides a JavaFX-based Wizard UI.
 *
 * @author Daniel Kraus
 */
public class MainViewController {

	private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);

	/**
	 * Provides the last directory for {@link #directoryChooser} and
	 * {@link #fileChooser}.
	 */
	private final LastDirectoryProvider lastDirProvider = new LastDirectoryProvider();
	/**
	 * Sets {@link #root}.
	 */
	private final DirectoryChooser directoryChooser = new DirectoryChooser();
	/**
	 * Sets {@link #root}.
	 */
	private final FileChooser fileChooser = new FileChooser();
	/**
	 * Root directory or file to work with.
	 */
	private File root;
	/**
	 * Indicates whether multiple or single files will be processed.
	 */
	private boolean multipleMode = false;

	/**
	 * Switches between the processing of multiple or single files.
	 */
	@FXML
	private ToggleGroup modeToggleGroup;
	/**
	 * <code>Label</code> for {@link #rootTextField}.
	 */
	@FXML
	private Label rootLabel;
	/**
	 * <code>TextField</code> for {@link #root}.
	 */
	@FXML
	private TextField rootTextField;
	/**
	 * Uses {@link #directoryChooser} respectively {@link #fileChooser} to set
	 * {@link #root}.
	 */
	@FXML
	private Button browseButton;
	/**
	 * <code>TextField</code> which is used for the copies infix.
	 */
	@FXML
	private TextField copyTextField;
	/**
	 * Enables the creation of copies.
	 */
	@FXML
	private CheckBox copyCheckBox;
	/**
	 * Value to use for bookmarks zoom level.
	 */
	@FXML
	private ChoiceBox<Zoom> zoomChoiceBox;
	/**
	 * Displays general information to the user.
	 */
	@FXML
	private Text infoText;
	/**
	 * Calls {@link #run()} if the user confirms to proceed.
	 */
	@FXML
	private Button runButton;

	/**
	 * Initializes FXML and bindings.
	 */
	public void initialize() {
		runButton.disableProperty().bind(rootTextField.textProperty().isEmpty().or(
				infoText.textProperty().isEqualTo(Wizard.PROCESSING_MESSAGE)));
		copyTextField.disableProperty().bind(copyCheckBox.selectedProperty().not());

		directoryChooser.setTitle("Choose a directory");
		fileChooser.setTitle("Choose a file");

		modeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			multipleMode = !rootLabel.getText().equals("Directory:");
			rootLabel.setText(multipleMode ? "Directory:" : "File:");

			var fadeOut = new FadeTransition(Duration.millis(300.0), rootLabel);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.play();

			var fadeIn = new FadeTransition(Duration.millis(300.0), rootLabel);
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.play();
		});

		browseButton.setOnAction(event -> {
			var lastDir = lastDirProvider.get();
			directoryChooser.setInitialDirectory(lastDir);
			fileChooser.setInitialDirectory(lastDir);

			var window = browseButton.getScene().getWindow();
			root = multipleMode ? directoryChooser.showDialog(window) : fileChooser.showOpenDialog(window);

			if (root != null) {
				var parentFile = multipleMode ? root : root.getParentFile();

				rootTextField.setText(root.getAbsolutePath());
				directoryChooser.setInitialDirectory(parentFile);
				fileChooser.setInitialDirectory(parentFile);
				lastDirProvider.set(parentFile);
			}
		});

		runButton.setOnAction(event -> {
			if (validateInput()) {
				var alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText(null);
				alert.setContentText(getConfirmationMessage());
				alert.showAndWait()
						.filter(response -> response == ButtonType.OK)
						.ifPresent(response -> run());
			}
		});
	}

	/**
	 * Validates the given UI input.
	 *
	 * @return <code>true</code> if everything is valid, else <code>false</code>.
	 */
	private boolean validateInput() {
		if (root == null || !root.getAbsolutePath().equals(rootTextField.getText())) {
			root = new File(rootTextField.getText());
		}

		if (!root.exists()) {
			return handleInvalidInput("Selected file/directory doesn't exist");
		}

		if (multipleMode && !root.isDirectory()) {
			return handleInvalidInput("Multiple files mode selected but file is selected");
		}

		if (!multipleMode && root.isDirectory()) {
			return handleInvalidInput("Singe file mode selected but directory is selected");
		}

		if (copyCheckBox.isSelected() && copyTextField.getText().isEmpty()) {
			return handleInvalidInput("Copy selected but filename infix is empty");
		}

		return true;
	}

	/**
	 * Handles invalid UI input.
	 *
	 * @param msg Warning message to log and show.
	 * @return Always <code>false</code>.
	 */
	private boolean handleInvalidInput(String msg) {
		logger.warn(msg);
		infoText.setText(msg);
		return false;
	}

	/**
	 * Creates the message for the confirmation dialog.
	 *
	 * @return Confirmation message for directory/file to be overwritten/copied.
	 */
	private String getConfirmationMessage() {
		var prefix = multipleMode
				? "All files in '" + root.getAbsolutePath() + "' and its enclosing subdirectories will be "
				: "File '" + root.getAbsolutePath() + "' will be ";
		var infix = copyCheckBox.isSelected() ? "copied." : "overwritten.";
		var suffix = "\n\nAre you sure to proceed?";
		return prefix + infix + suffix;
	}

	/**
	 * Creates a new {@link Wizard} instance and starts modification on
	 * {@link #root}.
	 */
	private void run() {
		var filenameInfix = copyCheckBox.isSelected() ? copyTextField.getText() : null;
		var wizard = new Wizard(root, filenameInfix, zoomChoiceBox.getValue());
		var thread = new Thread(wizard);
		// Can't be bound because infoText is also set within here.
		wizard.messageProperty().addListener((observable, oldValue, newValue) -> infoText.setText(newValue));
		thread.setDaemon(true);
		thread.start();
	}

}
