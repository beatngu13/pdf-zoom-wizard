package com.github.beatngu13.pdfzoomwizard.core;

import org.pdfclown.documents.interaction.navigation.document.Bookmark;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class BookmarkUtil {

	static final String BOOKMARK_TITLE_FALLBACK = "N/A";

	public static String getTitle(Bookmark bm) {
		try {
			return bm.getTitle();
		} catch (ClassCastException e) {
			log.warn("Exception while getting bookmark title, using '{}'.", BOOKMARK_TITLE_FALLBACK, e);
			return BOOKMARK_TITLE_FALLBACK;
		}
	}

}
