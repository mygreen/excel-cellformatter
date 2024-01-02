@echo off

%~d0
cd %~p0

call env.bat

call mvnw -version

call mvnw clean
mkdir target
call mvnw site -Dgpg.skip=true -Dfile.encoding=UTF-8 > target/site.log 2>&1

REM github-pages‚Ì‘Î‰
echo "" > .\target\site\.nojekyll

start target/site.log

