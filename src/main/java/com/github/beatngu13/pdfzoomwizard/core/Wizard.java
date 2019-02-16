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
package com.github.beatngu13.pdfzoomwizard.core;

import java.io.IOException;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.Destination.ModeEnum;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.objects.PdfObjectWrapper;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * Applies {@link #mode} and {@link #zoom} to the bookmarks of a single PDF file
 * or a whole directory (subdirectories included). This implementation is based
 * on the <a href="http://www.stefanochizzolini.it/en/projects/clown/">PDF
 * Clown</a> library by Stefano Chizzolini.
 * 
 * @author Daniel Kraus
 *
 */
@Slf4j
public class Wizard extends Task<Void> {

	/**
	 * File extension for PDFs.
	 */
	private static final String PDF_FILE_EXTENSION = ".pdf";
	/**
	 * @see {@link SerializationModeEnum}
	 */
	private static final SerializationModeEnum SERIALIZATION_MODE = SerializationModeEnum.Incremental;
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
	 * Creates a new <code>Wizard</code> instance.
	 * 
	 * @param root          Sets {@link #root}.
	 * @param filenameInfix Sets {@link #filenameInfix}.
	 * @param zoom          Sets {@link #zoom}.
	 */
	public Wizard(java.io.File root, String filenameInfix, Zoom zoom) {
		this.root = root;
		this.filenameInfix = filenameInfix;

		computeZoom(zoom);
	}

	@Override
	protected Void call() throws Exception {
		log.info("Start working on '{}'. PDF document(s) will be saved with serialization mode '{}'.",
				root.getAbsolutePath(), SERIALIZATION_MODE);
		modifyFiles(root);
		log.info("Modified {} bookmark(s) in {} file(s).", bookmarkCountGlobal, fileCount);

		return null;
	}

	/**
	 * Computes {@link #zoom} and {@link #mode}.
	 * 
	 * @param zoom Value given by the calling instance.
	 */
	private void computeZoom(Zoom zoom) {
		switch (zoom) {
		case ACTUAL_SIZE:
			this.zoom = 1.0;
			mode = ModeEnum.XYZ;
			break;
		case FIT_PAGE:
			this.zoom = null;
			mode = ModeEnum.Fit;
			break;
		case FIT_VISIBLE:
			this.zoom = 0.0;
			mode = ModeEnum.FitBoundingBoxHorizontal;
			break;
		case FIT_WIDTH:
			this.zoom = null;
			mode = ModeEnum.FitHorizontal;
			break;
		case INHERIT_ZOOM:
			this.zoom = null;
			mode = ModeEnum.XYZ;
			break;
		default:
			throw new IllegalArgumentException("Unkown zoom: " + zoom + ".");
		}
	}

	/**
	 * Modifies each PDF file which is found by depth-first search and calls
	 * {@link #modifyBookmarks(Bookmarks)} on it.
	 * 
	 * @param file Directory or file to work with.
	 */
	public void modifyFiles(java.io.File file) {
		if (file.isDirectory()) {
			java.io.File[] files = file.listFiles();

			for (java.io.File f : files) {
				modifyFiles(f);
			}
		} else {
			String filename = file.getName();

			if (!filename.endsWith(PDF_FILE_EXTENSION)) {
				log.warn("Skipping '{}'.", filename);
				return;
			}

			log.info("Processing '{}'.", filename);

			try (File pdf = new File(file.getAbsolutePath())) {
				bookmarkCountLocal = 0;
				Document document = pdf.getDocument();
				modifyBookmarks(document.getBookmarks());

				if (filenameInfix != null) {
					java.io.File output = new java.io.File(
							file.getAbsolutePath().replace(PDF_FILE_EXTENSION, filenameInfix + PDF_FILE_EXTENSION));
					pdf.save(output, SERIALIZATION_MODE);
				} else {
					pdf.save(SERIALIZATION_MODE);
				}
				fileCount++;
				log.info("Modified {} bookmark(s) in '{}'.", bookmarkCountLocal, filename);
			} catch (Exception e) {
				log.error("Exception while processing file '{}'.", file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Modifies each bookmark which is found by depth-first seach and applies
	 * {@link #mode} and {@link #zoom} to it.
	 * 
	 * @param bookmarks Collection of bookmarks to modify.
	 */
	void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			// Bookmarks#isEmpty() not implemented.
			if (bookmark.getBookmarks().size() > 0) {
				modifyBookmarks(bookmark.getBookmarks());
			}

			// XXX Bookmarks with broken destinations sometimes cause trouble.
			try {
				PdfObjectWrapper<?> target = bookmark.getTarget();

				if (target instanceof GoToDestination<?>) {
					Destination destination = ((GoToDestination<?>) target).getDestination();
					modifyDestination(bookmark, destination);
				} else if (target instanceof LocalDestination) {
					Destination destination = (LocalDestination) target;
					modifyDestination(bookmark, destination);
				} else {
					log.warn("Bookmark '{}' has an unknown target type: {}.", BookmarkUtil.getTitle(bookmark),
							target.getClass());
				}
			} catch (Exception e) {
				log.error("Exception while processing bookmark '{}'.", BookmarkUtil.getTitle(bookmark), e);
			}
		}
	}

	/**
	 * Modifies the given destination and applies {@link #mode} and {@link #zoom} to
	 * it.
	 * 
	 * @param bookmark    Bookmark the given destination belongs to.
	 * @param destination Destination to modify.
	 */
	private void modifyDestination(Bookmark bookmark, Destination destination) {
		destination.setMode(mode);
		destination.setZoom(zoom);
		bookmarkCountGlobal++;
		bookmarkCountLocal++;
		log.info("Modified bookmark '{}' to use mode '{}' and zoom '{}'.", BookmarkUtil.getTitle(bookmark), mode, zoom);
	}

	@Override
	protected void running() {
		super.running();
		updateMessage("Processing");
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage("Succeeded");
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage("Failed");
	}

}
