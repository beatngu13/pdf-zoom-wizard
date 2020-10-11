![.github/workflows/build.yml](https://github.com/beatngu13/pdf-zoom-wizard/workflows/.github/workflows/build.yml/badge.svg)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=com.github.beatngu13%3Apdfzoomwizard&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.beatngu13%3Apdfzoomwizard)
[![Total downloads status](https://img.shields.io/github/downloads/beatngu13/pdf-zoom-wizard/total.svg?style=flat)](https://github.com/beatngu13/pdf-zoom-wizard/releases)

# PDF Zoom Wizard

A new and extended implementation of the [PDF Bookmark Wizard](https://bitbucket.org/beatngu13/pdfbookmarkwizard/) for bookmarks *and* links (work in progress) zoom settings. Check out the [wiki](https://github.com/beatngu13/pdf-zoom-wizard/wiki/) to see how to get started.

## (Technical) Road Map

The road map below shows the current project status from a technical point of view. Have a look at the [changelog](https://github.com/beatngu13/pdf-zoom-wizard/blob/master/CHANGELOG.md) for more details on past changes.

- [x] Convert to Maven project
- [x] Enhance build process with Travis CI
- [x] Add changelog and Semantic Versioning
- [x] Tests (!)
- [x] Improve logging using SLF4J and Logback
- [ ] Refactor entire project
- [ ] Extract Wizard interface for new implementation (e.g. with [iText](https://itextpdf.com/) or [PDFBox](https://pdfbox.apache.org/))
- [ ] Implement zoom settings for links
- [ ] Implement command-line interface (CLI)
- [ ] Add integration tests for GUI and CLI
- [ ] Create self-contained app
- [ ] Migrate to current OpenJDK
- [x] Add ControlsFX for (modal) dialogs
- [x] Offer dynamically resizing GUI content

## Donation

The Wizard is free software under [GNU GPLv3](https://gnu.org/licenses/gpl-3.0.en.html). If you like, you can support its development with a donation:

[![PayPal](https://paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SYDFV6342B4T4)
