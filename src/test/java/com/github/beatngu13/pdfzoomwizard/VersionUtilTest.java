package com.github.beatngu13.pdfzoomwizard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionUtilTest {

	@Test
	void version_should_not_be_blank() {
		assertThat(VersionUtil.getVersion()).isNotBlank();
	}

}
