@echo off
setlocal

set "ROOT_DIR=%~dp0.."
for %%I in ("%ROOT_DIR%") do set "ROOT_DIR=%%~fI"
set "OUT_DIR=%ROOT_DIR%\out"

if not exist "%OUT_DIR%" (
  call "%ROOT_DIR%\scripts\compile.bat"
  if errorlevel 1 exit /b 1
)

java -cp "%OUT_DIR%" com.mojang.takns.Takns %*
