Write-Host "Creating native installer with jpackage..."
$jp = "jpackage"
if (-not (Get-Command $jp -ErrorAction SilentlyContinue)) {
  if ($env:JAVA_HOME) { $jp = Join-Path $env:JAVA_HOME "bin\jpackage.exe" }
}
if (-not (Test-Path $jp)) { Write-Error "jpackage not found. Install JDK 14+ and set JAVA_HOME."; exit 2 }
if (-not (Test-Path .\dist\JavaPadPro.jar)) { Write-Error "dist\JavaPadPro.jar not found. Run build first."; exit 3 }
if (Test-Path .\jpkg_input) { Remove-Item -Recurse -Force .\jpkg_input }
New-Item -ItemType Directory -Path .\jpkg_input | Out-Null
Copy-Item .\dist\JavaPadPro.jar .\jpkg_input\JavaPadPro.jar
& $jp --name JavaPadPro --input jpkg_input --main-jar JavaPadPro.jar --main-class com.javapad.Main --type exe --app-version 0.1.0 --vendor "JavaPad" --win-shortcut --win-menu --verbose
if ($LASTEXITCODE -ne 0) { Write-Error "jpackage failed"; exit $LASTEXITCODE }
Write-Host "jpackage finished."
