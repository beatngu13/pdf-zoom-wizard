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
package com.github.beatngu13.pdfzoomwizard.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pdfclown.Version;
import org.pdfclown.VersionEnum;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.Destination.ModeEnum;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.util.parsers.ParseException;

import javafx.concurrent.Task;

/**
 * Applies {@link #mode} and {@link #zoom} to the bookmarks of a single PDF file
 * or a whole directory (subdirectories included). This implementation is based
 * on the <a href="http://www.stefanochizzolini.it/en/projects/clown/">PDF
 * Clown</a> library by Stefano Chizzolini.
 * 
 * @author danielkraus1986@gmail.com
 *
 */
public class Wizard extends Task<Void> {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger logger = Logger.getLogger(Wizard.class.getName());

	/**
	 * @see {@link SerializationModeEnum}
	 */
	private final SerializationModeEnum serializationMode = SerializationModeEnum.Incremental;
	/**
	 * Total number of modified files.
	 */
	private int fileCount;
	/**
	 * Total number of modified bookmarks.
	 */
	private int bookmarkCountGlobal;
	/**
	 * Number of modified bookmarks within the current processed PDF file.
	 */
	private int bookmarkCountLocal;

	/**
	 * Directory or file to work with.
	 */
	private java.io.File root;
	/**
	 * <i>Filename&lt;infix&gt;.pdf</i> for copies, <code>null</code> if the
	 * original document will be overwritten.
	 */
	private String filenameInfix;
	/**
	 * Zoom to apply to all bookmarks.
	 */
	private Double zoom;
	/**
	 * Mode to apply to all bookmarks.
	 */
	private ModeEnum mode;
	/**
	 * Version number for serialization, <code>null</code> if the original version
	 * will be inherited.
	 */
	private Version version;

	/**
	 * Creates a new <code>Wizard</code> instance.
	 * 
	 * @param root
	 *            Sets {@link #root}.
	 * @param filenameInfix
	 *            Sets {@link #filenameInfix}.
	 * @param zoom
	 *            Sets {@link #zoom}.
	 * @param version
	 *            version Sets {@link #version}.
	 */
	public Wizard(java.io.File root, String filenameInfix, String zoom, String version) {
		this.root = root;
		this.filenameInfix = filenameInfix;

		computeZoom(zoom);
		computeVersion(version);
	}

	@Override
	protected Void call() throws Exception {
		logger.info("Start working in \"" + root.getAbsolutePath() + "\". All PDF documents will be saved as version "
				+ version + " with serialization mode " + serializationMode + ".");
		modifyFiles(root);
		logger.info("Modified " + bookmarkCountGlobal + " bookmarks in " + fileCount + " file(s).");

		return null;
	}

	/**
	 * Computes {@link #version}.
	 * 
	 * @param version
	 *            Value given by the calling instance.
	 */
	private void computeVersion(String version) {
		switch (version) {
		case "1.0":
			this.version = VersionEnum.PDF10.getVersion();
			break;
		case "1.1":
			this.version = VersionEnum.PDF11.getVersion();
			break;
		case "1.2":
			this.version = VersionEnum.PDF12.getVersion();
			break;
		case "1.3":
			this.version = VersionEnum.PDF13.getVersion();
			break;
		case "1.4":
			this.version = VersionEnum.PDF14.getVersion();
			break;
		case "1.5":
			this.version = VersionEnum.PDF15.getVersion();
			break;
		case "1.6":
			this.version = VersionEnum.PDF16.getVersion();
			break;
		case "1.7":
			this.version = VersionEnum.PDF17.getVersion();
		}
	}

	/**
	 * Computes {@link #zoom} and {@link #mode}.
	 * 
	 * @param zoom
	 *            Value given by the calling instance.
	 */
	private void computeZoom(String zoom) {
		switch (zoom) {
		case "Fit page":
			mode = ModeEnum.Fit;
			break;
		case "Actual size":
			this.zoom = 1.0;
			mode = ModeEnum.XYZ;
			break;
		case "Fit width":
			mode = ModeEnum.FitHorizontal;
			break;
		case "Fit visible":
			this.zoom = 0.0;
			mode = ModeEnum.FitBoundingBoxHorizontal;
			break;
		case "Inherit zoom":
			mode = ModeEnum.XYZ;
		}
	}

	/**
	 * Modifies each PDF file which is found by depth-first search and calls
	 * {@link #modifyBookmarks(Bookmarks)} on it.
	 * 
	 * @param file
	 *            Directory or file to work with.
	 */
	public void modifyFiles(java.io.File file) {
		if (file.isDirectory()) {
			java.io.File[] files = file.listFiles();

			for (java.io.File f : files) {
				modifyFiles(f);
			}
		} else {
			String filename = file.getName();
			logger.info("Processing \"" + filename + "\".");

			try (File pdf = new File(file.getAbsolutePath())) {
				bookmarkCountLocal = 0;
				Document document = pdf.getDocument();
				modifyBookmarks(document.getBookmarks());

				// FIXME Broken PDF versioning, probably caused by a PDF Clown bug.
				if (version != null) {
					document.setVersion(version);
				}

				if (filenameInfix != null) {
					java.io.File output = new java.io.File(
							file.getAbsolutePath().replace(".pdf", filenameInfix + ".pdf"));
					pdf.save(output, serializationMode);
				} else {
					pdf.save(serializationMode);
				}
				fileCount++;
				logger.info("Successfully modified " + bookmarkCountLocal + " bookmarks in \"" + filename + "\".");
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE,
						"Could not create " + File.class.getName() + " instance of \"" + file.getAbsolutePath() + "\".",
						e);
			} catch (ParseException e) {
				logger.log(Level.SEVERE, "Could not parse \"" + file.getAbsolutePath() + "\".", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Could not save \"" + file.getAbsolutePath() + "\".", e);
			}
		}
	}

	/**
	 * Modifies each bookmark which is found by depth-first seach and applies
	 * {@link #mode} and {@link #zoom} to it.
	 * 
	 * @param bookmarks
	 *            Collection of bookmarks to modify.
	 */
	private void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			// TODO Change to bookmark.getBookmarks().isEmpty when it's implemented.
			if (bookmark.getBookmarks().size() != 0) {
				modifyBookmarks(bookmark.getBookmarks());
			}

			if (bookmark.getTarget() instanceof GoToDestination<?>) {
				// FIXME PDFs containing bookmarks with broken destinations sometimes don't
				// serialize.
				try {
					Destination destination = ((GoToDestination<?>) bookmark.getTarget()).getDestination();

					destination.setMode(mode);
					destination.setZoom(zoom);
					bookmarkCountGlobal++;
					bookmarkCountLocal++;
					logger.fine("Successfully set \"" + bookmark.getTitle() + "\" to use mode " + mode + " and zoom "
							+ zoom + ".");
				} catch (Exception e) {
					logger.severe("\"" + bookmark.getTitle() + "\" has a broken destination.");
				}
			}
		}
	}

	@Override
	protected void running() {
		super.running();
		updateMessage(State.RUNNING.toString());
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage(State.SUCCEEDED.toString());
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage(State.FAILED.toString());
	}

}
