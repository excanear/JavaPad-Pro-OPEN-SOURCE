@echo off
REM Build script: compiles Java sources and creates dist\JavaPadPro.jar
setlocal enabledelayedexpansion

if not exist out mkdir out
if exist sources.txt del /q sources.txt
for /r %%f in (*.java) do @echo %%f >> sources.txt

javac -d out @sources.txt
if ERRORLEVEL 1 (
  echo Compilation failed.
  exit /b 1
)

if not exist dist mkdir dist
jar cfe dist\JavaPadPro.jar com.javapad.Main -C out .
if ERRORLEVEL 1 (
  echo Failed to create jar.
  exit /b 1
)

echo Build successful: dist\JavaPadPro.jar
endlocal
exit /b 0
