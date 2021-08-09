# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.10.0] - 2021-08-09

### Changed

- Include version in native images names.
- Reduce native images sizes.

## [0.9.0] - 2021-07-01

### Changed

- Improve log messages.

### Fixed

- Missing class(es) in native image(s) (see [#224](https://github.com/beatngu13/pdf-zoom-wizard/issues/224)).

## [0.8.0] - 2021-06-30

### Changed

- Provide native executables for Linux, Mac, and Windows. As a consequence, the provided JAR now requires Java 11+.

## [0.7.0] - 2021-03-11

### Changed

- Various internal improvements that e.g. reduce memory footprint and required disk space.

## [0.6.0] - 2020-11-01

### Changed

- Copy PDF files as default (see [#157](https://github.com/beatngu13/pdf-zoom-wizard/issues/157)).

## [0.5.1] - 2020-07-23

### Fixed

- Invalid last directory (e.g. if deleted) preventing the file/directory chooser from opening (see [#126](https://github.com/beatngu13/pdf-zoom-wizard/issues/126)).

## [0.5.0] - 2020-03-30

### Added

- Remember last directory (see [#36](https://github.com/beatngu13/pdf-zoom-wizard/issues/36)).

### Changed

- Improve logging by mentioning applied zoom only once (at the beginning) and using the actual name (rather than exposing internals).

## [0.4.1] - 2019-04-24

### Fixed

- Closed bookmarks not being modified (see [#50](https://github.com/beatngu13/pdf-zoom-wizard/issues/50)).

## [0.4.0] - 2019-04-13

### Fixed

- Encrypted PDF files crashing execution (see [#34](https://github.com/beatngu13/pdf-zoom-wizard/issues/34)).
- `NullPointerException` on [`Bookmark#getTarget()`](http://clown.sourceforge.net/docs/api/org/pdfclown/documents/interaction/navigation/document/Bookmark.html#getTarget()) crashing execution (see [#46](https://github.com/beatngu13/pdf-zoom-wizard/issues/46)).

### Changed

- Rename log file under `${HOME}/.pdfzoomwizard/` from `log` to `log.txt`, which makes upload for GitHub issues easier.
- Improve logging in various places, including a log for the currently used PDF Zoom Wizard version on launch.

## [0.3.0] - 2019-02-11

### Added

- Dynamically resizing GUI content for a better UX.

### Changed

- Use native confirmation dialog before execution instead of a custom warning dialog.
- Rename label / text field "State" to "Info" and improve the corresponding messages.

### Fixed

- Non-PDF files crashing execution (see [#29](https://github.com/beatngu13/pdf-zoom-wizard/issues/29)).

## [0.2.0] - 2019-02-08

### Added

- Support for [`LocalDestination`](http://clown.sourceforge.net/docs/api/org/pdfclown/documents/interaction/navigation/document/LocalDestination.html)s (see [#22](https://github.com/beatngu13/pdf-zoom-wizard/issues/22)).
- Better logging experience with an improved console logger and a new file logger, which creates a log file under `${HOME}/.pdfzoomwizard/log`.

### Changed

- Set single file mode as default.

### Fixed

- `ClassCastException` on [`Bookmark#getTarget()`](http://clown.sourceforge.net/docs/api/org/pdfclown/documents/interaction/navigation/document/Bookmark.html#getTarget()) crashing execution (see [#20](https://github.com/beatngu13/pdf-zoom-wizard/issues/20)).

### Removed

- Experimental (and partly broken) feature for setting the PDF version.

## [0.1.2] - 2019-02-04

### Fixed

- Issues with bookmark titles of LaTeX-based PDFs (see [#17](https://github.com/beatngu13/pdf-zoom-wizard/issues/17)).

## [0.1.1] - 2018-05-27

### Fixed

- Several serialization issues by switching to incremental mode, leaving the original PDF data intact (see PDF Reference, `v1.6`, section 2.2.7).

## [0.1.0] - 2018-02-27

### Added

- GitHub port of the former PDF Bookmark Wizard (`v0.0.4.1`) that offers the same functionality.
