package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZoomTest {

	@Test
	void to_string_should_be_well_formated() {
		assertThat(Zoom.ACTUAL_SIZE).hasToString("Actual size");
		assertThat(Zoom.FIT_PAGE).hasToString("Fit page");
		assertThat(Zoom.FIT_VISIBLE).hasToString("Fit visible");
		assertThat(Zoom.FIT_WIDTH).hasToString("Fit width");
		assertThat(Zoom.INHERIT_ZOOM).hasToString("Inherit zoom");
	}

}
