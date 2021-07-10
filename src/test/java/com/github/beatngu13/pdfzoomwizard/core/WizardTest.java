package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.api.Test;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.objects.PdfObjectWrapper;

import java.util.Collections;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WizardTest {

	@SuppressWarnings("unchecked")
	@Test
	void exception_in_get_target_shouldnt_crash_execution() {
		Wizard cut = new Wizard(null, null, Zoom.ACTUAL_SIZE);

		Bookmark bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(mock(Bookmarks.class));
		when(bookmark.getTarget()).thenThrow(RuntimeException.class);

		Iterator<Bookmark> iter = Collections.singleton(bookmark).iterator();

		Bookmarks bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(iter);

		assertThatCode(() -> cut.modifyBookmarks(bookmarks)).doesNotThrowAnyException();
	}

	@SuppressWarnings("unchecked")
	@Test
	void closed_bookmarks_should_be_modified() {
		Wizard cut = spy(new Wizard(null, null, Zoom.ACTUAL_SIZE));

		Iterator<Bookmark> childrenIter = Collections.emptyIterator();

		Bookmarks children = mock(Bookmarks.class);
		when(children.iterator()).thenReturn(childrenIter);
		// Negative count means closed bookmark.
		when(children.size()).thenReturn(-1);

		Bookmark bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(children);
		when(bookmark.getTarget()).thenReturn(mock(PdfObjectWrapper.class));

		Iterator<Bookmark> bookmarksIter = Collections.singleton(bookmark).iterator();

		Bookmarks bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(bookmarksIter);

		cut.modifyBookmarks(bookmarks);

		verify(cut).modifyBookmarks(children);
	}

}
