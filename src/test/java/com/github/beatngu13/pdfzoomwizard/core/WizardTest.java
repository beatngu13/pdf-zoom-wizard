package com.github.beatngu13.pdfzoomwizard.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;
import org.pdfclown.objects.PdfObjectWrapper;

class WizardTest {

	@SuppressWarnings("unchecked")
	@Test
	void exception_in_get_target_shouldnt_crash_execution() throws Exception {
		Wizard cut = new Wizard(null, null, Zoom.ACTUAL_SIZE);

		Bookmark bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(mock(Bookmarks.class));
		when(bookmark.getTarget()).thenThrow(IllegalArgumentException.class);

		Iterator<Bookmark> iter = mock(Iterator.class);
		when(iter.hasNext()).thenReturn(true, false);
		when(iter.next()).thenReturn(bookmark);

		Bookmarks bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(iter);

		cut.modifyBookmarks(bookmarks);
	}

	@Test
	void non_pdfs_shouldnt_crash_execution(@TempDir Path temp) throws Exception {
		File nonPdf = temp.resolve("foo.bar").toFile();
		nonPdf.createNewFile();
		Wizard cut = new Wizard(nonPdf, null, Zoom.ACTUAL_SIZE);
		cut.call();
	}

	@SuppressWarnings("unchecked")
	@Test
	void closed_bookmarks_should_be_modified() throws Exception {
		Wizard cut = spy(new Wizard(null, null, Zoom.ACTUAL_SIZE));

		Iterator<Bookmark> childrenIter = mock(Iterator.class);
		when(childrenIter.hasNext()).thenReturn(false);

		Bookmarks children = mock(Bookmarks.class);
		when(children.iterator()).thenReturn(childrenIter);
		// Negative count means closed bookmark.
		when(children.size()).thenReturn(-1);

		Bookmark bookmark = mock(Bookmark.class);
		when(bookmark.getBookmarks()).thenReturn(children);
		when(bookmark.getTarget()).thenReturn(mock(PdfObjectWrapper.class));

		Iterator<Bookmark> bookmarksIter = mock(Iterator.class);
		when(bookmarksIter.hasNext()).thenReturn(true, false);
		when(bookmarksIter.next()).thenReturn(bookmark);

		Bookmarks bookmarks = mock(Bookmarks.class);
		when(bookmarks.iterator()).thenReturn(bookmarksIter);

		cut.modifyBookmarks(bookmarks);

		verify(cut).modifyBookmarks(children);
	}

}
