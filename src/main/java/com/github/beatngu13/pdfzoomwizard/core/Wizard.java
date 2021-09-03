package com.github.beatngu13.pdfzoomwizard.core;

import javafx.concurrent.Task;
import org.pdfclown.documents.interaction.actions.GoToDestination;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.documents.interaction.navigation.document.Destination;
import org.pdfclown.documents.interaction.navigation.document.LocalDestination;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.objects.PdfObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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
public class Wizard extends Task<Void> {

	private static final Logger logger = LoggerFactory.getLogger(Wizard.class);

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
	 * @see SerializationModeEnum
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
		logger.info("Start working on '{}'.", root.getAbsolutePath());
		logger.info("Bookmark(s) will be set to zoom '{}'.", zoom);
		logger.info("PDF document(s) will be saved with serialization mode '{}'.", SERIALIZATION_MODE);
		modifyFiles(root);
		logger.info("Modified {} bookmark(s) in {} file(s).", bookmarkCountGlobal, fileCount);
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
			throw new UncheckedIOException("Exception while walking file tree.", e);
		}
	}

	/**
	 * Modifies the given file using {@link #modifyBookmarks(Bookmarks)} if it is a PDF, otherwise does nothing.
	 *
	 * @param file File to be modified.
	 */
	private void modifyFile(File file) {
		var filename = file.getName();

		if (!filename.endsWith(PDF_FILE_EXTENSION)) {
			logger.warn("Skipping non-PDF file '{}'.", filename);
			return;
		}

		logger.info("Processing PDF file '{}'.", filename);

		try (var pdf = new org.pdfclown.files.File(file.getAbsolutePath())) {
			bookmarkCountLocal = 0;
			var document = pdf.getDocument();
			modifyBookmarks(document.getBookmarks());
			savePdf(pdf);
			fileCount++;
			logger.info("Modified {} bookmark(s) in '{}'.", bookmarkCountLocal, filename);
		} catch (Exception e) {
			logger.error("Exception while processing file '{}'.", file.getAbsolutePath(), e);
		}
	}

	/**
	 * Saves the given PDF. If {@link #filenameInfix} is not null, the PDF will be copied, otherwise overwritten.
	 *
	 * @param pdf PDF to be saved.
	 * @throws IOException If an I/O error occurs.
	 */
	private void savePdf(org.pdfclown.files.File pdf) throws IOException {
		if (filenameInfix == null) {
			// Overwrite PDF.
			pdf.save(SERIALIZATION_MODE);
		} else {
			// Copy PDF.
			var path = pdf.getPath().replace(PDF_FILE_EXTENSION, filenameInfix + PDF_FILE_EXTENSION);
			var copy = new File(path);
			pdf.save(copy, SERIALIZATION_MODE);
		}
	}

	/**
	 * Modifies each bookmark which is found by depth-first search using {@link #modifyBookmark(Bookmark)}.
	 * <p>
	 * Visible for testing.
	 *
	 * @param bookmarks Bookmarks to be modified.
	 */
	void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			var children = bookmark.getBookmarks();
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
				logger.warn("Bookmark '{}' has an unknown target type: {}.", BookmarkUtil.getTitle(bookmark),
						target.getClass());
			}
		} catch (Exception e) {
			logger.error("Exception while processing bookmark '{}'.", BookmarkUtil.getTitle(bookmark), e);
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
		logger.info("Modified bookmark '{}'.", BookmarkUtil.getTitle(bookmark));
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
