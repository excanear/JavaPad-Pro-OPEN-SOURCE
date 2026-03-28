@echo off
REM Create native installer using jpackage (Windows). Requires JDK 14+ with jpackage.
set JPACKAGE=jpackage
if not exist %JPACKAGE% (
  if defined JAVA_HOME (
    set JPACKAGE=%JAVA_HOME%\bin\jpackage.exe
  )
)
if not exist %JPACKAGE% (
  echo jpackage not found. Ensure JDK 14+ is installed and JAVA_HOME is set.
  exit /b 2
)

REM Ensure dist/JavaPadPro.jar exists
if not exist dist\JavaPadPro.jar (
  echo dist\JavaPadPro.jar not found. Run the build first.
  exit /b 3
)










)
necho jpackage finished. Output directory: .\  exit /b %ERRORLEVEL%  echo jpackage failedif %ERRORLEVEL% NEQ 0 (
n%JPACKAGE% --name JavaPadPro --input jpkg_input --main-jar JavaPadPro.jar --main-class com.javapad.Main --type exe --app-version 0.1.0 --vendor "JavaPad" --win-shortcut --win-menu --verbosecopy dist\JavaPadPro.jar jpkg_input\JavaPadPro.jar >nulmkdir jpkg_inputif exist jpkg_input rmdir /s /q jpkg_inputnREM Create input folder for jpackage