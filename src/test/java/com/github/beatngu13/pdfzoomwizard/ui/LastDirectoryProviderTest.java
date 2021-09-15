package com.github.beatngu13.pdfzoomwizard.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import static com.github.beatngu13.pdfzoomwizard.ui.LastDirectoryProvider.LAST_DIRECTORY_PREFERENCES_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LastDirectoryProviderTest {

	Preferences prefs;
	LastDirectoryProvider cut;

	@BeforeEach
	void setUp() {
		prefs = mock(Preferences.class);
		cut = new LastDirectoryProvider();
	}

	@Test
	void set_should_not_accept_null() {
		assertThatThrownBy(() -> cut.set(null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Last directory must not be null.");
	}

	@Test
	void set_should_not_accept_non_existing_file() {
		var nonExistingFile = new File("non-existing-file");

		assertThatThrownBy(() -> cut.set(nonExistingFile))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Last directory must be an existing directory.");
	}

	@Test
	void set_should_not_accept_non_directory(@TempDir Path temp) throws Exception {
		var nonDirectory = temp.resolve("non-directory").toFile();
		nonDirectory.createNewFile();

		assertThatThrownBy(() -> cut.set(nonDirectory))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Last directory must be an existing directory.");
	}

	@Test
	void set_should_put_last_directory_in_preferences(@TempDir Path temp) {
		var lastDirectory = temp.resolve("directory").toFile();
		lastDirectory.mkdir();

		cut.set(lastDirectory, prefs);

		verify(prefs).put(LAST_DIRECTORY_PREFERENCES_KEY, lastDirectory.getAbsolutePath());
	}

	@Test
	void get_should_yield_null_if_last_directory_is_absent() {
		assertThat(cut.get(prefs)).isNull();
	}

	@Test
	void get_should_yield_null_if_last_directory_does_not_exist() {
		var lastDirectoryPath = "non-existing-directory";
		when(prefs.get(eq(LAST_DIRECTORY_PREFERENCES_KEY), any())).thenReturn(lastDirectoryPath);

		assertThat(cut.get(prefs)).isNull();
	}

	@Test
	void get_should_yield_null_if_last_directory_is_not_a_directory(@TempDir Path temp) throws Exception {
		var nonDirectory = temp.resolve("non-directory").toFile();
		nonDirectory.createNewFile();

		var lastDirectoryPath = nonDirectory.getAbsolutePath();
		when(prefs.get(eq(LAST_DIRECTORY_PREFERENCES_KEY), any())).thenReturn(lastDirectoryPath);

		assertThat(cut.get(prefs)).isNull();
	}

	@Test
	void get_should_yield_last_directory_from_preferences(@TempDir Path temp) {
		var lastDirectory = temp.resolve("directory").toFile();
		lastDirectory.mkdir();

		var lastDirectoryPath = lastDirectory.getAbsolutePath();
		when(prefs.get(eq(LAST_DIRECTORY_PREFERENCES_KEY), any())).thenReturn(lastDirectoryPath);

		assertThat(cut.get(prefs)).isEqualTo(new File(lastDirectoryPath));
	}

}
