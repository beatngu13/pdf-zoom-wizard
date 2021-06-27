package com.github.beatngu13.pdfzoomwizard.ui;

import org.slf4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.prefs.Preferences;

class LastDirectoryProvider {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LastDirectoryProvider.class);

	/**
	 * Preferences key for the absolute path of the last directory.
	 */
	static final String LAST_DIRECTORY_PREFERENCES_KEY = "last_directory";

	public File get() {
		var prefs = Preferences.userNodeForPackage(LastDirectoryProvider.class);
		return get(prefs);
	}

	/**
	 * Visible for testing.
	 */
	File get(Preferences prefs) {
		var lastDirPath = prefs.get(LAST_DIRECTORY_PREFERENCES_KEY, null);
		logger.debug("Get last directory: {}", lastDirPath);
		if (lastDirPath == null) {
			return null;
		}
		var lastDir = new File(lastDirPath);
		if (!lastDir.isDirectory()) {
			return null;
		}
		return lastDir;
	}

	public void set(File lastDir) {
		var prefs = Preferences.userNodeForPackage(LastDirectoryProvider.class);
		set(lastDir, prefs);
	}

	/**
	 * Visible for testing.
	 */
	void set(File lastDir, Preferences prefs) {
		validate(lastDir);
		var lastDirPath = lastDir.getAbsolutePath();
		logger.debug("Set last directory: {}", lastDirPath);
		prefs.put(LAST_DIRECTORY_PREFERENCES_KEY, lastDirPath);
	}

	private static void validate(File lastDir) {
		Objects.requireNonNull(lastDir, "Last directory must not be null.");
		if (!lastDir.isDirectory()) {
			throw new IllegalArgumentException("Last directory must be an existing directory.");
		}
	}

}
