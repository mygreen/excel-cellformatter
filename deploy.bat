@echo off

%~d0
cd %~p0

call env.bat

call mvnw -version

call mvnw clean deploy

pause
