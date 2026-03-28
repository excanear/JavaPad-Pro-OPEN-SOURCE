# Build script to compile and package the ExamplePlugin into plugins/ExamplePlugin.jar
param()

if (-not (Test-Path out)) {
    Write-Error "Compiled classes not found in ./out. Run build.ps1 first to compile the project."
    exit 1
}

$pluginSrc = "src/main/java/com/javapad/plugins/impl/ExamplePlugin.java"
if (-not (Test-Path $pluginSrc)) {
    Write-Error "ExamplePlugin source not found: $pluginSrc"
    exit 1
}

if (-not (Test-Path plugins)) { New-Item -ItemType Directory -Path plugins | Out-Null }
if (Test-Path plugin_out) { Remove-Item plugin_out -Recurse -Force }
New-Item -ItemType Directory -Path plugin_out | Out-Null

& javac -d plugin_out -classpath out $pluginSrc
if ($LASTEXITCODE -ne 0) { Write-Error "Compilation of plugin failed"; exit 1 }

if (Test-Path plugins\ExamplePlugin.jar) { Remove-Item plugins\ExamplePlugin.jar -Force }
& jar cf plugins\ExamplePlugin.jar -C plugin_out .
if ($LASTEXITCODE -ne 0) { Write-Error "Failed to create plugin jar"; exit 1 }

Remove-Item plugin_out -Recurse -Force
Write-Host "Plugin jar created: plugins\ExamplePlugin.jar"
exit 0
