@ECHO OFF
@cls
ECHO === build Barcode4J Utils ===
ECHO mvn -U -e clean install
set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Xmx4192m
call mvn -U -e clean install
pause
