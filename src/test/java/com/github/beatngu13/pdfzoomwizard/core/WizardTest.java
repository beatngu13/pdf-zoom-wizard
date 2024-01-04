package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;
import org.pdfclown.documents.interaction.navigation.document.Bookmarks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WizardTest {

	@Mock
	Bookmark bookmark;
	@Mock
	Bookmarks bookmarks;
	@Mock(strictness = Strictness.LENIENT)
	Bookmarks childBookmarks;

	Iterator<Bookmark> bookmarksIterator;
	Iterator<Bookmark> childBookmarksIterator;

	Wizard cut = new Wizard(null, null, Zoom.ACTUAL_SIZE);

	@BeforeEach
	void setUp() {
		bookmarksIterator = List.of(bookmark).iterator();
		childBookmarksIterator = Collections.emptyIterator();

		when(bookmark.getBookmarks()).thenReturn(childBookmarks);
		when(bookmarks.iterator()).thenReturn(bookmarksIterator);
		when(childBookmarks.iterator()).thenReturn(childBookmarksIterator);
	}

	@Test
	void get_target_exception_should_not_crash_execution() {
		when(bookmark.getTarget()).thenThrow(RuntimeException.class);

		assertThatCode(() -> cut.modifyBookmarks(bookmarks)).doesNotThrowAnyException();
	}

	@Test
	void get_target_null_should_not_crash_execution() {
		when(bookmark.getTarget()).thenReturn(null);

		assertThatCode(() -> cut.modifyBookmarks(bookmarks)).doesNotThrowAnyException();
	}

	@Test
	void closed_bookmarks_should_be_modified() {
		var cut = spy(this.cut);
		when(childBookmarks.size()).thenReturn(-1);

		cut.modifyBookmarks(bookmarks);

		verify(cut).modifyBookmarks(childBookmarks);
	}

	@Test
	@Timeout(1)
	void endless_bookmark_iterator_should_be_stopped() {
		var bookmarksIterator = Stream.generate(() -> bookmark).iterator();
		when(bookmarks.iterator()).thenReturn(bookmarksIterator);

		assertThatCode(() -> cut.modifyBookmarks(bookmarks)).doesNotThrowAnyException();
	}

}
