@cls
@ECHO OFF
ECHO === build Barcode4J without junit tests===

set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Xmx4192m -Dmaven.test.skip.exec=true

call mvn -e clean install
PAUSE
