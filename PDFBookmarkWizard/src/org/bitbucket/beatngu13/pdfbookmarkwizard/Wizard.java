/**
 * 
 */
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

import javafx.concurrent.Task;

/**
 * @author danielkraus1986@gmail.com
 *
 */
public class Wizard extends Task<Void> {
	
	private static final Logger logger = Logger.getLogger(Wizard.class.getName());
	
	private final java.io.File rootDirectory;
	private final Version version;
	private final SerializationModeEnum serializationMode;
	private final ModeEnum mode;
	private final Double zoom;
	private int fileCount;
	private int bookmarkCount;
	
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
		// TODO Does an exception has been thrown?
		logger.info("Successfully modified " + fileCount + " files.");
		
		return null;
	}
	
	private void modifiyFiles(java.io.File[] files) {
		for (java.io.File file : files) {
			String filename = file.getName();
			
			if (filename.endsWith(".pdf")) {
				fileCount++;
				logger.info("Processing \"" + filename + "\".");
				
				try {
					File pdf = new File(file.getAbsolutePath());
					Document document = pdf.getDocument();
					bookmarkCount = 0;
					
					modifyBookmarks(document.getBookmarks());
					document.setVersion(version);
					pdf.save(serializationMode);
					pdf.close();
					logger.info("Successfully modified " + bookmarkCount + " bookmarks in \"" 
							+ filename + "\".");
				} catch (FileNotFoundException e) {
					logger.log(Level.SEVERE, "Could not create " + File.class.getName() 
							+ " object.", e);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Could not save modified \"" + file.getAbsolutePath() 
							+ "\".", e);
				}
			} else if (file.isDirectory()) {
				modifiyFiles(file.listFiles());
			}
		}
	}
	
	private void modifyBookmarks(Bookmarks bookmarks) {
		for (Bookmark bookmark : bookmarks) {
			
			if (bookmark.getTarget() instanceof GoToDestination<?>) {
				Destination destination = ((GoToDestination<?>) bookmark.getTarget())
						.getDestination();

				destination.setMode(mode);
				destination.setZoom(zoom);
				bookmarkCount++;
				logger.fine("Successfully set \"" + bookmark.getTitle() 
						+ "\" to use 'Inherhit Zoom'.");
			}
			
			if (bookmark.getBookmarks().size() != 0) {
				modifyBookmarks(bookmark.getBookmarks());
			}
		}
	}

}
