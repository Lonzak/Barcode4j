@cls
@ECHO OFF
ECHO === build Barcode4j and updates the thirdparty dependency report ===

set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Xmx16384m -Dmaven.test.skip.exec=true
call mvn -e clean install -f ./pom.xml -Pupdate_thirdparty_dependencies_report
PAUSE