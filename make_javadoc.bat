@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_7%" == "" (
    set JAVA_HOME="%JAVA_HOME_7%"
)

call mvn javadoc:javadoc -Dhttps.protocols=TLSv1.2 > target/javadoc.log 2>&1 

start target/javadoc.log

