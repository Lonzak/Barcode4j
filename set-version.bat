@cls
@ECHO OFF
ECHO === Setting version to %1 ===

set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Xmx512m 

call mvn versions:set -DnewVersion=%1
del /s /q *.versionsBackup

