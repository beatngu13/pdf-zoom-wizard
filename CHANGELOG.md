# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Support for [`LocalDestination`](http://clown.sourceforge.net/docs/api/org/pdfclown/documents/interaction/navigation/document/LocalDestination.html)s (see [#22](https://github.com/beatngu13/pdf-zoom-wizard/issues/22)).
- Better logging experience with an improved console logger and a new file logger, which creates a log file under `${HOME}/.pdfzoomwizard/log`.

### Fixed

- Exception on [`Bookmark#getTarget()`](http://clown.sourceforge.net/docs/api/org/pdfclown/documents/interaction/navigation/document/Bookmark.html#getTarget()) crashing execution (see [#20](https://github.com/beatngu13/pdf-zoom-wizard/issues/20)).

### Changed

- Set single file mode as default.

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
