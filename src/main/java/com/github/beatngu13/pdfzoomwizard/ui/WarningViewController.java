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
 * Copyright 2013-2014 Daniel Kraus
 */
package com.github.beatngu13.pdfzoomwizard.ui;

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
 * @author Daniel Kraus
 *
 */
public class WarningViewController {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(WarningViewController.class.getName());

	/**
	 * {@link Stage} instance.
	 */
	private Stage stage = new Stage();
	/**
	 * Indicates whether the user decided to proceed by clicking {@link #okButton},
	 * or to cancel by clicking {@link #cancelButton}.
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
	 * Leaves {@link #proceed} being <code>false</code> and closes the warning
	 * dialog.
	 */
	@FXML
	private Button cancelButton;

	/**
	 * Creates a new <code>WarningController</code> instance.
	 * 
	 * @param owner
	 *            Calling window.
	 */
	public WarningViewController(Window owner) {
		stage.setTitle("Warning");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("WarningView.fxml"));

			// TODO Adding the controller within the FXML file fails.
			loader.setController(this);
			loader.load();
			stage.setScene(new Scene(warningView, 350.0, 185.0));
			warningText.wrappingWidthProperty().bind(stage.getScene().widthProperty().subtract(20.0));
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
	 * @param message
	 *            Warning message to be displayed.
	 * @return {@link #proceed}.
	 */
	public boolean show(String message) {
		proceed = false;

		warningText.setText(message);
		stage.showAndWait();

		return proceed;
	}

}
