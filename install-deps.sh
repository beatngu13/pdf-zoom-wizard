#!/usr/bin/env sh

mvn install:install-file -Dfile=lib/org/pdfclown/pdfclown/0.1.2/pdfclown.jar -DgroupId=org.pdfclown -DartifactId=pdfclown -Dversion=0.1.2 -Dpackaging=jar
