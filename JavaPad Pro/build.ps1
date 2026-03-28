# Build script (PowerShell): compiles Java sources and creates dist/JavaPadPro.jar
$cwd = Get-Location
if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
if (Test-Path sources.txt) { Remove-Item sources.txt -Force }
Get-ChildItem -Recurse -Filter '*.java' | ForEach-Object { $_.FullName } | Out-File sources.txt -Encoding utf8
$files = Get-Content sources.txt

javac -d out $files
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed (javac returned $LASTEXITCODE)"
    exit 1
}

if (-not (Test-Path dist)) { New-Item -ItemType Directory -Path dist | Out-Null }

jar cfe dist\JavaPadPro.jar com.javapad.Main -C out .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to create jar (jar returned $LASTEXITCODE)"
    exit 1
}

Write-Host "Build successful: dist\JavaPadPro.jar"
exit 0
