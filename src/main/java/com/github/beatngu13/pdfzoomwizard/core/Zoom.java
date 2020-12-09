package com.github.beatngu13.pdfzoomwizard.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pdfclown.documents.interaction.navigation.document.Destination.ModeEnum;

@Getter
@RequiredArgsConstructor
public enum Zoom {

	ACTUAL_SIZE(1.0, ModeEnum.XYZ),
	FIT_PAGE(null, ModeEnum.Fit),
	FIT_VISIBLE(0.0, ModeEnum.FitBoundingBoxHorizontal),
	FIT_WIDTH(null, ModeEnum.FitHorizontal),
	INHERIT_ZOOM(null, ModeEnum.XYZ);

	private final Double zoom;
	private final ModeEnum mode;

	@Override
	public String toString() {
		// ZOOM_NAME
		String name = name();
		// ZOOM NAME
		name = name.replace('_', ' ');
		// Zoom name
		name = name.substring(0, 1) + name.substring(1).toLowerCase();
		return name;
	}

}
