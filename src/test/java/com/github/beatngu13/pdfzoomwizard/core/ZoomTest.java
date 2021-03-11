package com.github.beatngu13.pdfzoomwizard.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ZoomTest {

	@ParameterizedTest
	@MethodSource("args")
	void to_string_should_be_formatted(Zoom zoom, String formatted) {
		assertThat(zoom).hasToString(formatted);
	}

	static Stream<Arguments> args() {
		return Stream.of(
				arguments(Zoom.ACTUAL_SIZE, "Actual size"),
				arguments(Zoom.FIT_PAGE, "Fit page"),
				arguments(Zoom.FIT_VISIBLE, "Fit visible"),
				arguments(Zoom.FIT_WIDTH, "Fit width"),
				arguments(Zoom.INHERIT_ZOOM, "Inherit zoom")
		);
	}

}
