@echo off

%~d0
cd %~p0

call env.bat

call mvnw -version

mkdir target
call mvnw javadoc:javadoc > target/javadoc.log 2>&1 

start target/javadoc.log

