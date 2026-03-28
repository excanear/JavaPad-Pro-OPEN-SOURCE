@echo off
REM Build and package ExamplePlugin into plugins\ExamplePlugin.jar
if not exist out (
  echo Compiled classes not found in out\. Run build.bat first.
  exit /b 1
)

if not exist src\main\java\com\javapad\plugins\impl\ExamplePlugin.java (
  echo ExamplePlugin source not found.
  exit /b 1
)

if not exist plugins mkdir plugins
if exist plugin_out rmdir /s /q plugin_out
mkdir plugin_out

javac -d plugin_out -classpath out src\main\java\com\javapad\plugins\impl\ExamplePlugin.java
if ERRORLEVEL 1 (
  echo Compilation failed.
  exit /b 1
)

if exist plugins\ExamplePlugin.jar del /q plugins\ExamplePlugin.jar
jar cf plugins\ExamplePlugin.jar -C plugin_out .
if ERRORLEVEL 1 (
  echo Failed to create jar.
  exit /b 1
)

rmdir /s /q plugin_out
echo Plugin jar created: plugins\ExamplePlugin.jar
exit /b 0
