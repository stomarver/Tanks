@echo off
setlocal

set "ROOT_DIR=%~dp0.."
for %%I in ("%ROOT_DIR%") do set "ROOT_DIR=%%~fI"
set "OUT_DIR=%ROOT_DIR%\out"
set "DIST_DIR=%ROOT_DIR%\dist"
set "JAR_PATH=%DIST_DIR%\takns.jar"

call "%ROOT_DIR%\scripts\clean.bat"
if errorlevel 1 exit /b 1

if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"

jar cfe "%JAR_PATH%" com.mojang.takns.Takns -C "%OUT_DIR%" .
if errorlevel 1 exit /b 1

echo Fat JAR created: %JAR_PATH%
echo Run with: java -jar %JAR_PATH%
