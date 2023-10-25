package com.github.beatngu13.pdfzoomwizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

final class VersionUtil {

	private static final Logger logger = LoggerFactory.getLogger(VersionUtil.class);

	private static final String VERSION_PROPERTIES = "/pdfzoomwizard.properties";
	private static final String VERSION_KEY = "version";
	private static final String VERSION_FALLBACK = "N/A";
	private static final String VERSION = readVersion();

	private static String readVersion() {
		try (InputStream inputStream = VersionUtil.class.getResourceAsStream(VERSION_PROPERTIES)) {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties.getProperty(VERSION_KEY);
		} catch (Exception e) {
			logger.error("Exception while reading version.", e);
			return VERSION_FALLBACK;
		}
	}

	private VersionUtil() {
	}

	public static String getVersion() {
		return VERSION;
	}

}
