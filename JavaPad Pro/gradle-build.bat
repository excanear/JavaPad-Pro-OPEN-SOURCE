@echo off
REM Build using Gradle wrapper if available, otherwise use system gradle










echo Build finished successfully.)  exit /b %ERRORLEVEL%  echo Gradle build failedif %ERRORLEVEL% NEQ 0 ()  gradle clean build) else (  gradlew.bat clean buildnif exist gradlew.bat (