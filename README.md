[![Build Status](https://travis-ci.org/beatngu13/pdf-zoom-wizard.svg?branch=master)](https://travis-ci.org/beatngu13/pdf-zoom-wizard)

# PDF Zoom Wizard

A new and extended implementation of the [PDF Bookmark Wizard](https://bitbucket.org/beatngu13/pdfbookmarkwizard/) for bookmarks *and* links (work in progress).

The [road map](#road-map) below shows the current project status. Release `v0.1.0` is a simple GitHub port of the former PDF Bookmark Wizard (`v0.0.4.1`) that offers the same functionality. Have a look at the [changelog](https://github.com/beatngu13/pdf-zoom-wizard/blob/master/CHANGELOG.md) for the changes in subsequent releases.

To see how to get started, check out the [wiki](https://github.com/beatngu13/pdf-zoom-wizard/wiki/).

## Road map

- [x] Convert to Maven project
- [x] Enhance build process with Travis CI
- [x] Add changelog and Semantic Versioning
- [ ] Tests (!)
- [ ] Improve logging using SLF4J and Logback
- [ ] Refactor entire project
- [ ] Extract Wizard interface for new implementation (e.g. with [iText](https://itextpdf.com/) or [PDFBox](https://pdfbox.apache.org/))
- [ ] Implement zoom settings for links
- [ ] Create self-contained app
- [ ] Migrate to current OpenJDK
- [ ] Add ControlsFX for (modal) dialogs
- [ ] Offer dynamically resizing GUI content

## Donation

The Wizard is free software under [GNU GPLv3](https://gnu.org/licenses/gpl-3.0.en.html). If you like, you can support its development with a donation:

[![PayPal](https://paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SYDFV6342B4T4)
