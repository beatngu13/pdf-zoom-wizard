/*
 * This file is part of the PDF Zoom Wizard.
 * 
 * The PDF Zoom Wizard is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation. <br><br>
 * 
 * The PDF Zoom Wizard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details. <br><br>
 * 
 * You should have received a copy of the GNU General Public License along with 
 * the PDF Zoom Wizard. If not, see 
 * <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>. <br><br>
 * 
 * Copyright 2013-2019 Daniel Kraus
 */
package com.github.beatngu13.pdfzoomwizard.ui;

import java.io.File;

import com.github.beatngu13.pdfzoomwizard.core.Wizard;

import javafx.animation.FadeTransition;
import javafx.concurrent.Worker.State;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a JavaFX-based Wizard UI.
 * 
 * @author Daniel Kraus
 *
 */
@Slf4j
public class MainViewController {

	/**
	 * Sets {@link #root}.
	 */
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	/**
	 * Sets {@link #root}.
	 */
	private FileChooser fileChooser = new FileChooser();
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
	private ChoiceBox<String> zoomChoiceBox;
	/**
	 * Displays the current state of the Wizard. Basically the values of
	 * {@link State} are used.
	 */
	@FXML
	private Text stateText;
	/**
	 * Calls {@link #run()} if the user confirms to proceed.
	 */
	@FXML
	private Button runButton;

	/**
	 * Initializes FXML and bindings.
	 */
	public void initialize() {
		runButton.disableProperty().bind(rootTextField.textProperty().isEmpty()
				.or(stateText.textProperty().isEqualTo(State.RUNNING.toString())));
		copyTextField.disableProperty().bind(copyCheckBox.selectedProperty().not());

		directoryChooser.setTitle("Choose a directory");
		fileChooser.setTitle("Choose a file");

		// TODO Best practice?
		modeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			multipleMode = !rootLabel.getText().equals("Directory:");

			FadeTransition fadeOut = new FadeTransition(Duration.millis(300.0), rootLabel);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.play();

			fadeOut.setOnFinished(event -> {
				rootLabel.setText(multipleMode ? "Directory:" : "File:");

				FadeTransition fadeIn = new FadeTransition(Duration.millis(300.0), rootLabel);
				fadeIn.setFromValue(0.0);
				fadeIn.setToValue(1.0);
				fadeIn.play();
			});
		});

		browseButton.setOnAction(event -> {
			root = multipleMode ? directoryChooser.showDialog(browseButton.getScene().getWindow())
					: fileChooser.showOpenDialog(browseButton.getScene().getWindow());

			if (root != null) {
				File parentFile = root.getParentFile();

				rootTextField.setText(root.getAbsolutePath());
				directoryChooser.setInitialDirectory(parentFile);
				fileChooser.setInitialDirectory(parentFile);
			}
		});

		runButton.setOnAction(event -> {
			if (validateInput()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText(null);
				alert.setContentText(getConfirmationMessage());
				alert.showAndWait() //
						.filter(response -> response == ButtonType.OK) //
						.ifPresent(response -> MainViewController.this.run());
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

		if (root == null || !root.getAbsolutePath().equals(rootTextField.getText())) {
			root = new File(rootTextField.getText());
		}

		if (multipleMode && !root.isDirectory()) {
			valid = false;
			stateText.setText("A FILE IS SELECTED");
			log.warn("'{}' is a file.", root.getAbsolutePath());
		} else if (!multipleMode && root.isDirectory()) {
			valid = false;
			stateText.setText("A DIRECTORY IS SELECTED");
			log.warn("'{}' is a directory.", root.getAbsolutePath());
		}

		if (copyCheckBox.isSelected() && copyTextField.getText().isEmpty()) {
			valid = false;
			stateText.setText("FILENAME INFIX IS EMPTY");
			log.warn("Filename infix is empty.");
		}

		return valid;
	}

	/**
	 * Creates the message for the confirmation dialog.
	 * 
	 * @return Confirmation message for directory/file to be overwritten/copied.
	 */
	private String getConfirmationMessage() {
		String prefix = multipleMode
				? "All files in \"" + root.getAbsolutePath() + "\" and its enclosing subdirectories will be "
				: "\"" + root.getAbsolutePath() + "\" will be ";
		String infix = !copyCheckBox.isSelected() ? "overwritten." : "copied.";
		String suffix = "\n\nAre you sure to proceed?";
		return prefix + infix + suffix;
	}

	/**
	 * Creates a new {@link Wizard} instance and starts modification on
	 * {@link #root}.
	 */
	private void run() {
		String filenameInfix = copyCheckBox.isSelected() ? copyTextField.getText() : null;
		Wizard wizard = new Wizard(root, filenameInfix, zoomChoiceBox.getValue());
		Thread thread = new Thread(wizard);
		wizard.messageProperty()
				.addListener((observable, oldValue, newValue) -> MainViewController.this.stateText.setText(newValue));
		thread.setDaemon(true);
		thread.start();
	}

}
