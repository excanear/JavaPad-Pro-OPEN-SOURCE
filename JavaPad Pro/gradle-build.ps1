Write-Host "Building project with Gradle wrapper (if present)..."
if (Test-Path .\gradlew.ps1 -PathType Leaf -or Test-Path .\gradlew -PathType Leaf) {
  & .\gradlew clean build
} else {
  & gradle clean build
}
if ($LASTEXITCODE -ne 0) {
  Write-Error "Gradle build failed"
  exit $LASTEXITCODE
}
Write-Host "Build finished successfully."