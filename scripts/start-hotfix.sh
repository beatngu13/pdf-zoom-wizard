#!/usr/bin/env sh

set -x

./mvnw -B gitflow:hotfix-start -DhotfixBranch="$1"
