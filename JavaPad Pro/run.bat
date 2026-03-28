@echo off
if not exist dist\JavaPadPro.jar (
  echo Jar not found. Run build.bat first.
  exit /b 1
)

java -jar dist\JavaPadPro.jar
exit /b 0
