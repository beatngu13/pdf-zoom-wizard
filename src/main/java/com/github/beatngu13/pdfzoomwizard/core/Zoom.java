package com.github.beatngu13.pdfzoomwizard.core;

public enum Zoom {

	ACTUAL_SIZE, FIT_PAGE, FIT_VISIBLE, FIT_WIDTH, INHERIT_ZOOM;

	@Override
	public String toString() {
		String name = name();
		name = name.replace('_', ' ');
		name = name.substring(0, 1) + name.substring(1).toLowerCase();
		return name;
	}

}
