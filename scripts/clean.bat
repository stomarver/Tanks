@echo off
setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0.."
for %%I in ("%ROOT_DIR%") do set "ROOT_DIR=%%~fI"
set "OUT_DIR=%ROOT_DIR%\out"
set "SRC_DIR=%ROOT_DIR%\src"
set "TMP_SOURCES=%TEMP%\takns-sources-%RANDOM%%RANDOM%.txt"

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

> "%TMP_SOURCES%" (
  for /r "%SRC_DIR%" %%F in (*.java) do (
    echo %%~fF
  )
)

findstr /v /i "\\TaknsApplet.java" "%TMP_SOURCES%" > "%TMP_SOURCES%.filtered"
move /y "%TMP_SOURCES%.filtered" "%TMP_SOURCES%" >nul

javac --release 8 -encoding windows-1252 -d "%OUT_DIR%" @"%TMP_SOURCES%"
if errorlevel 1 (
  javac -source 1.8 -target 1.8 -encoding windows-1252 -d "%OUT_DIR%" @"%TMP_SOURCES%"
  if errorlevel 1 (
    del "%TMP_SOURCES%"
    exit /b 1
  )
)

del "%TMP_SOURCES%"
echo Compiled classes to: %OUT_DIR%