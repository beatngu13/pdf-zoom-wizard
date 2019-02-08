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
package com.github.beatngu13.pdfzoomwizard;

import com.github.beatngu13.pdfzoomwizard.ui.MainViewController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches the Wizard.
 * 
 * @author Daniel Kraus
 *
 */
public class Launcher extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("PDF Zoom Wizard");
		primaryStage.setScene(new Scene(new MainViewController().getMainView(), 425.0, 250.0));
		primaryStage.show();
	}

}
