package com.github.beatngu13.pdfzoomwizard.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;

class BookmarkUtilTest {

	@Test
	void bookmark_title_should_be_used_when_available() throws Exception {
		String title = "foo";
		Bookmark bm = mock(Bookmark.class);
		when(bm.getTitle()).thenReturn(title);
		assertThat(BookmarkUtil.getTitle(bm)).isEqualTo(title);
	}

	@Test
	void fallback_title_should_be_used_when_exception_occurs() throws Exception {
		Bookmark bm = mock(Bookmark.class);
		when(bm.getTitle()).thenThrow(ClassCastException.class);
		assertThat(BookmarkUtil.getTitle(bm)).isEqualTo("N/A");
	}

}
