# Gradle Configuration
# Redirects Gradle user home to persistent storage via environment variable

param($DevPath = "C:\dev")

Write-Host "Configuring Gradle paths..." -ForegroundColor Yellow

$gradleHome = Join-Path $DevPath ".sandbox\gradle-home"

# Create gradle-home directory in sandbox if it doesn't exist
if (-not (Test-Path $gradleHome)) {
    New-Item -ItemType Directory -Path $gradleHome -Force | Out-Null
    Write-Host "  Created: $gradleHome" -ForegroundColor Green
}

# Set environment variable for current session
$env:GRADLE_USER_HOME = $gradleHome

# Set machine-level environment variable (persists across processes)
[Environment]::SetEnvironmentVariable("GRADLE_USER_HOME", $gradleHome, "Machine")

Write-Host "  GRADLE_USER_HOME set to: $gradleHome" -ForegroundColor Green
Write-Host "  Dependencies will be cached across sandbox restarts!" -ForegroundColor Cyan
Write-Host "  Note: Using environment variable instead of symlink for better compatibility" -ForegroundColor Gray
