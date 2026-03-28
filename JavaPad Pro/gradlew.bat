@echo off
where gradle >nul 2>&1
if %ERRORLEVEL%==0 (
  gradle %*
) else (
  echo Gradle não encontrado. Para criar o wrapper execute: gradle wrapper
  echo Ou instale o Gradle: https://gradle.org/install
  exit /b 1
)
