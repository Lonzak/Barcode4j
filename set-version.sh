#!/bin/sh
echo '=== Setting version to' $1 '==='

MAVEN_OPTS="-Dfile.encoding=UTF-8 -Xmx512m"
mvn versions:set -DnewVersion=$1

find . -name '*.versionsBackup' -delete