package com.github.beatngu13.pdfzoomwizard.core;

import org.pdfclown.documents.interaction.navigation.document.Bookmark;

class BookmarkUtil {

	public static String getTitle(Bookmark bm) {
		try {
			return bm.getTitle();
		} catch (ClassCastException e) {
			return "N/A";
		}
	}

}
