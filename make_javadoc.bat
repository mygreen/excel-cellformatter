@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_7%" == "" (
    set JAVA_HOME="%JAVA_HOME_7%"
)

set MAVEN_OPTS=-Dhttps.protocols=TLSv1.1,TLSv1.2

mkdir target
call mvn javadoc:javadoc > target/javadoc.log 2>&1 

start target/javadoc.log

