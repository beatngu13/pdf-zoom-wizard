package com.github.beatngu13.pdfzoomwizard.core;

import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BookmarkUtil {

	private static final Logger logger = LoggerFactory.getLogger(BookmarkUtil.class);

	static final String BOOKMARK_TITLE_FALLBACK = "N/A";

	private BookmarkUtil() {
	}

	public static String getTitle(Bookmark bm) {
		try {
			return bm.getTitle();
		} catch (RuntimeException e) {
			logger.warn("Exception while getting bookmark title, using '{}'.", BOOKMARK_TITLE_FALLBACK, e);
			return BOOKMARK_TITLE_FALLBACK;
		}
	}

}
