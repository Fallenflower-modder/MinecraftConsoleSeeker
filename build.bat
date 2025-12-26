@echo off
chcp 65001 > nul
call gradlew.bat build --info
pause