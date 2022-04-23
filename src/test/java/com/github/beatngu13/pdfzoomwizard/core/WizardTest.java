package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.api.Test;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.objects.PdfObjectWrapper;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WizardTest {

	@Test
	void get_target_exception_should_not_crash_execution() {
		var cut = new Wizard(null, null, Zoom.ACTUAL_SIZE);

		var bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(mock(Bookmarks.class));
		when(bookmark.getTarget()).thenThrow(RuntimeException.class);

		var bookmarkIter = Collections.singleton(bookmark).iterator();

		var bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(bookmarkIter);

		assertThatCode(() -> cut.modifyBookmarks(bookmarks)).doesNotThrowAnyException();
	}

	@SuppressWarnings("unchecked")
	@Test
	void closed_bookmarks_should_be_modified() {
		var cut = spy(new Wizard(null, null, Zoom.ACTUAL_SIZE));

		var childBookmarksIter = Collections.<Bookmark>emptyIterator();

		var childBookmarks = mock(Bookmarks.class);
		when(childBookmarks.iterator()).thenReturn(childBookmarksIter);
		// Negative count means closed bookmark.
		when(childBookmarks.size()).thenReturn(-1);

		var bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(childBookmarks);
		when(bookmark.getTarget()).thenReturn(mock(PdfObjectWrapper.class));

		var bookmarksIter = Collections.singleton(bookmark).iterator();

		var bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(bookmarksIter);

		cut.modifyBookmarks(bookmarks);

		verify(cut).modifyBookmarks(childBookmarks);
	}

}
