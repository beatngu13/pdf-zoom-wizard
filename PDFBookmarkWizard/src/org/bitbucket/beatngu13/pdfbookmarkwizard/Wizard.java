package org.bitbucket.beatngu13.pdfbookmarkwizard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pdfclown.Version;
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
 * Modifies all PDF files within a given directory and its enclosing subdirectories. Each bookmark 
 * will be set to use {@link #mode} and {@link #zoom}.
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
	 * Root directory to work with.
	 */
	private final java.io.File rootDirectory;
	/**
	 * Version number to use for serialization.
	 */
	private final Version version;
	/**
	 * Serialization mode.
	 */
	private final SerializationModeEnum serializationMode;
	/**
	 * Mode to apply to all bookmarks.
	 */
	private final ModeEnum mode;
	/**
	 * Zoom to apply to all bookmarks.
	 */
	private final Double zoom;
	/**
	 * Total number of modified files. 
	 */
	private int fileCount;
	/**
	 * Total number of modified bookmarks.
	 */
	private int bookmarkCount;
	/**
	 * Number of modified bookmarks within the currently processed PDF file.
	 */
	private int bookmarkCountLocal;
	
	/**
	 * Creates a new <code>Wizard</code> instance.
	 * 
	 * @param rootDirectory {@link #rootDirectory}.
	 * @param version {@link #version}.
	 * @param serializationMode {@link #serializationMode}.
	 * @param mode {@link #mode}.
	 * @param zoom {@link #zoom}.
	 */
	public Wizard(java.io.File rootDirectory, Version version, 
			SerializationModeEnum serializationMode, ModeEnum mode, Double zoom) {
		this.rootDirectory = rootDirectory;
		this.version = version;
		this.serializationMode = serializationMode;
		this.mode = mode;
		this.zoom = zoom;
	}

	@Override
	protected Void call() throws Exception {
		logger.info("Start working in \"" + rootDirectory.getAbsolutePath()
				+ "\". All PDF documents will be saved as version " + version
				+ " with serialization mode " + serializationMode + ".");
		
		modifiyFiles(rootDirectory.listFiles());
		
		logger.info("Modified " + bookmarkCount + " bookmarks in " + fileCount + " file(s).");
		
		return null;
	}
	
	/**
	 * Modifies each PDF file which is found by depth-first seach, starting from the given 
	 * list of files, and calls {@link #modifyBookmarks(Bookmarks)}.
	 * 
	 * @param files List of files within the root directory.
	 */
	private void modifiyFiles(java.io.File[] files) {
		for (java.io.File file : files) {
			String filename = file.getName();
			
			if (filename.endsWith(".pdf")) {
				logger.info("Processing \"" + filename + "\".");
				
				try {
					File pdf = new File(file.getAbsolutePath());
					Document document = pdf.getDocument();
					
					modifyBookmarks(document.getBookmarks());
					// FIXME Broken PDF versioning, probably caused by a PDF Clown bug.
					if (version != null) {
						document.setVersion(version);
					}
					pdf.save(serializationMode);
					pdf.close();
					fileCount++;
					bookmarkCountLocal = 0;
					
					logger.info("Successfully modified " + bookmarkCountLocal + " bookmarks in \"" 
							+ filename + "\".");
				} catch (FileNotFoundException e) {
					logger.log(Level.SEVERE, "Could not create " + File.class.getName() 
							+ " instance of \"" + file.getAbsolutePath() + "\".", e);
				} catch (ParseException e) {
					logger.log(Level.SEVERE, "\"" + file.getAbsolutePath() 
							+ "\" is not a PDF file.");
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Could not save modified \"" + file.getAbsolutePath() 
							+ "\".", e);
				}
			} else if (file.isDirectory()) {
				modifiyFiles(file.listFiles());
			}
		}
	}
	
	/**
	 * Modifies each bookmark which is found by depth-first seach, starting from the given 
	 * collection of bookmarks, and applies {@link #mode} and {@link #zoom}.
	 * 
	 * @param bookmarks Collection of bookmarks to modify.
	 */
	private void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			if (bookmark.getTarget() instanceof GoToDestination<?>) {
				Destination destination = ((GoToDestination<?>) bookmark.getTarget())
						.getDestination();

				destination.setMode(mode);
				destination.setZoom(zoom);
				bookmarkCount++;
				bookmarkCountLocal++;
				
				logger.fine("Successfully set \"" + bookmark.getTitle() 
						+ "\" to use mode " + mode + " and zoom " + zoom + ".");
			}
			
			if (bookmark.getBookmarks().size() != 0) {
				modifyBookmarks(bookmark.getBookmarks());
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
	
	// TODO Update counters.
	@Override
	protected void failed() {
		super.failed();
		updateMessage(State.FAILED.toString());
	}

}
