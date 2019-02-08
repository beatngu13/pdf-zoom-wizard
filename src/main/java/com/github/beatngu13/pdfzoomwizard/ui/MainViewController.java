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
import java.io.IOException;

import com.github.beatngu13.pdfzoomwizard.core.Wizard;

import javafx.animation.FadeTransition;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
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
	 * Occurs when {@link #runButton} is being clicked.
	 */
	private WarningViewController warningController;
	/**
	 * Indicates whether multiple or single files will be processed.
	 */
	private boolean multipleMode = false;

	/**
	 * Wizard UI container.
	 */
	@FXML
	private Parent mainView;
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
	 * Creates a new <code>MainViewController</code> instance.
	 */
	public MainViewController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));

			// TODO Adding the controller within the FXML file fails.
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			log.error("Could not load FXML file.", e);
		}

		runButton.disableProperty().bind(rootTextField.textProperty().isEqualTo("")
				.or(stateText.textProperty().isEqualTo(State.RUNNING.toString())));
		copyTextField.disableProperty().bind(copyCheckBox.selectedProperty().not());

		directoryChooser.setTitle("Choose a directory");
		fileChooser.setTitle("Choose a file");

		// TODO Best practice?
		modeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				multipleMode = !rootLabel.getText().equals("Directory:");

				FadeTransition fadeOut = new FadeTransition(Duration.millis(300.0), rootLabel);
				fadeOut.setFromValue(1.0);
				fadeOut.setToValue(0.0);
				fadeOut.play();

				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						rootLabel.setText(multipleMode ? "Directory:" : "File:");

						FadeTransition fadeIn = new FadeTransition(Duration.millis(300.0), rootLabel);
						fadeIn.setFromValue(0.0);
						fadeIn.setToValue(1.0);
						fadeIn.play();
					}
				});
			}
		});

		browseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				root = multipleMode ? directoryChooser.showDialog(mainView.getScene().getWindow())
						: fileChooser.showOpenDialog(mainView.getScene().getWindow());

				if (root != null) {
					File parentFile = root.getParentFile();

					rootTextField.setText(root.getAbsolutePath());
					directoryChooser.setInitialDirectory(parentFile);
					fileChooser.setInitialDirectory(parentFile);
				}
			}

		});

		runButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (validateInput()) {
					// TODO Best practice?
					warningController = warningController == null
							? new WarningViewController(runButton.getScene().getWindow())
							: warningController;

					String messagePrefix = multipleMode
							? "All files in \"" + root.getAbsolutePath()
									+ "\" and its enclosing subdirectories will be "
							: "\"" + root.getAbsolutePath() + "\" will be ";
					String messageInfix = !copyCheckBox.isSelected() ? "overwritten!" : "copied!";
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
	 * Creates a new {@link Wizard} instance and starts modification on
	 * {@link #root}.
	 */
	private void run() {
		String filenameInfix = copyCheckBox.isSelected() ? copyTextField.getText() : null;
		Wizard wizard = new Wizard(root, filenameInfix, zoomChoiceBox.getValue());
		Thread thread = new Thread(wizard);

		wizard.messageProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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
