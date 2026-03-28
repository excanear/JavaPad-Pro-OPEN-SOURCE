# Build script that places JAR in TEMP to avoid OneDrive locks
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

$ts = (Get-Date).ToString('yyyyMMdd-HHmmss')
$jarName = Join-Path $env:TEMP "JavaPadPro-$ts.jar"
jar cfe $jarName com.javapad.Main -C out .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to create jar (jar returned $LASTEXITCODE)"
    exit 1
}

Write-Host "Build successful: $jarName"
exit 0
