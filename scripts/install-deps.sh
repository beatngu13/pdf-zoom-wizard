#!/usr/bin/env sh

set -x

./mvnw -B install:install-file \
		-Dfile=deps/org/pdfclown/pdfclown/0.1.2/pdfclown.jar \
		-DgroupId=org.pdfclown \
		-DartifactId=pdfclown \
		-Dversion=0.1.2 \
		-Dpackaging=jar
