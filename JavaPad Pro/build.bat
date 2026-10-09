@echo off
REM Build script: compiles Java sources and creates dist\JavaPadPro.jar
setlocal enabledelayedexpansion

if not exist out mkdir out
if exist sources.txt del /q sources.txt
REM No argfile (@sources.txt) do javac, '\' e escape e espacos separam args.
REM Entao escrevemos cada caminho entre aspas e com barras normais '/'.
for /r %%f in (*.java) do (
  set "JP_SRC=%%f"
  echo "!JP_SRC:\=/!">> sources.txt
)

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
