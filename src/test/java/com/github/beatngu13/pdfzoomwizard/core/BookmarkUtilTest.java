package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pdfclown.documents.interaction.navigation.document.Bookmark;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkUtilTest {

	@Mock
	Bookmark bookmark;

	@Test
	void bookmark_title_should_be_used_when_available() {
		var title = "foo";
		when(bookmark.getTitle()).thenReturn(title);
		assertThat(BookmarkUtil.getTitle(bookmark)).isEqualTo(title);
	}

	@Test
	void fallback_title_should_be_used_when_exception_occurs() {
		when(bookmark.getTitle()).thenThrow(RuntimeException.class);
		assertThat(BookmarkUtil.getTitle(bookmark)).isEqualTo(BookmarkUtil.BOOKMARK_TITLE_FALLBACK);
	}

}
