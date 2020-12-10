package com.github.beatngu13.pdfzoomwizard.core;

import com.google.common.annotations.VisibleForTesting;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.objects.PdfObjectWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Applies {@link #zoom} to the bookmarks of a single PDF file or a whole
 * directory (subdirectories included). This implementation is based on the
 * <a href="http://www.stefanochizzolini.it/en/projects/clown/">PDF Clown</a>
 * library by Stefano Chizzolini.
 *
 * @author Daniel Kraus
 */
@Slf4j
public class Wizard extends Task<Void> {

	/**
	 * The {@link Task#updateMessage(String)} when {@link Task#running()}.
	 */
	public static final String PROCESSING_MESSAGE = "Processing";
	/**
	 * The {@link Task#updateMessage(String)} when {@link Task#succeeded()}.
	 */
	public static final String SUCCEEDED_MESSAGE = "Succeeded";
	/**
	 * The {@link Task#updateMessage(String)} when {@link Task#failed()}.
	 */
	public static final String FAILED_MESSAGE = "Failed";

	/**
	 * File extension for PDFs.
	 */
	private static final String PDF_FILE_EXTENSION = ".pdf";
	/**
	 * @see {@link SerializationModeEnum}
	 */
	private static final SerializationModeEnum SERIALIZATION_MODE = SerializationModeEnum.Incremental;

	/**
	 * Directory or file to work with.
	 */
	private final File root;
	/**
	 * <i>Filename&lt;infix&gt;.pdf</i> for copies, <code>null</code> if the
	 * original document will be overwritten.
	 */
	private final String filenameInfix;
	/**
	 * Zoom to apply to all bookmarks.
	 */
	private final Zoom zoom;

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
	 * Creates a new <code>Wizard</code> instance.
	 *
	 * @param root          Sets {@link #root}.
	 * @param filenameInfix Sets {@link #filenameInfix}.
	 * @param zoom          Sets {@link #zoom}.
	 */
	public Wizard(File root, String filenameInfix, Zoom zoom) {
		this.root = root;
		this.filenameInfix = filenameInfix;
		this.zoom = zoom;
	}

	@Override
	protected Void call() {
		log.info(
				"Start working on '{}'. Bookmark(s) will be set to zoom '{}'. PDF document(s) will be saved with serialization mode '{}'.",
				root.getAbsolutePath(), zoom, SERIALIZATION_MODE);
		modifyFiles(root);
		log.info("Modified {} bookmark(s) in {} file(s).", bookmarkCountGlobal, fileCount);

		return null;
	}

	/**
	 * Modifies each PDF file which is found by depth-first search using {@link #modifyFile(File)}.
	 *
	 * @param file Directory or file to be modified.
	 */
	public void modifyFiles(File file) {
		try (Stream<Path> tree = Files.walk(file.toPath())) {
			tree.map(Path::toFile).forEach(this::modifyFile);
		} catch (IOException e) {
			log.error("Exception while walking file tree.", e);
		}
	}

	/**
	 * Modifies the given file using {@link #modifyBookmarks(Bookmarks)} if it is a PDF, otherwise does nothing.
	 *
	 * @param file File to be modified.
	 */
	private void modifyFile(File file) {
		String filename = file.getName();

		if (!filename.endsWith(PDF_FILE_EXTENSION)) {
			log.warn("Skipping '{}'.", filename);
			return;
		}

		log.info("Processing '{}'.", filename);

		try (org.pdfclown.files.File pdf = new org.pdfclown.files.File(file.getAbsolutePath())) {
			bookmarkCountLocal = 0;
			Document document = pdf.getDocument();
			modifyBookmarks(document.getBookmarks());

			if (filenameInfix != null) {
				File output = new File(
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

	/**
	 * Modifies each bookmark which is found by depth-first search using {@link #modifyBookmark(Bookmark)}.
	 *
	 * @param bookmarks Bookmarks to be modified.
	 */
	@VisibleForTesting
	void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			Bookmarks children = bookmark.getBookmarks();
			// Size might be positive (bookmark open) or negative (bookmark closed).
			if (children.size() != 0) {
				modifyBookmarks(children);
			}
			modifyBookmark(bookmark);
		}
	}

	/**
	 * Modifies the given bookmark using {@link #modifyDestination(Bookmark, Destination)}.
	 *
	 * @param bookmark Bookmark to be modified.
	 */
	private void modifyBookmark(Bookmark bookmark) {
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

	/**
	 * Modifies the given destination by applying {@link #zoom}.
	 *
	 * @param bookmark    Bookmark the given destination belongs to.
	 * @param destination Destination to modify.
	 */
	private void modifyDestination(Bookmark bookmark, Destination destination) {
		destination.setMode(zoom.getMode());
		destination.setZoom(zoom.getZoom());
		bookmarkCountGlobal++;
		bookmarkCountLocal++;
		log.info("Modified bookmark '{}'.", BookmarkUtil.getTitle(bookmark));
	}

	@Override
	protected void running() {
		super.running();
		updateMessage(PROCESSING_MESSAGE);
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage(SUCCEEDED_MESSAGE);
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage(FAILED_MESSAGE);
	}

}
