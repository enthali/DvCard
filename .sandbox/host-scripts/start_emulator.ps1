# Start Android Emulator on Host
# Uses the SDK from the sandbox directory

$ErrorActionPreference = "Stop"

# Get project root (two levels up from host-scripts)
$projectRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$sdkPath = Join-Path $projectRoot ".sandbox\android-sdk"
$avdPath = Join-Path $sdkPath ".android\avd"
$emulatorPath = Join-Path $sdkPath "emulator\emulator.exe"

# Check if SDK exists
if (-not (Test-Path $sdkPath)) {
    Write-Host "Android SDK not found!" -ForegroundColor Red
    Write-Host "Please run the sandbox at least once to download the SDK."
    exit 1
}

# Check if emulator exists
if (-not (Test-Path $emulatorPath)) {
    Write-Host "Emulator not found in SDK!" -ForegroundColor Red
    Write-Host "Please install the Android Emulator via Android Studio first."
    exit 1
}

# Create AVD directory if it doesn't exist
if (-not (Test-Path $avdPath)) {
    New-Item -ItemType Directory -Path $avdPath -Force | Out-Null
}

# Set environment variables to use SANDBOX AVDs
$env:ANDROID_SDK_ROOT = $sdkPath
$env:ANDROID_HOME = $sdkPath
$env:ANDROID_AVD_HOME = $avdPath
$env:PATH = "$sdkPath\emulator;$sdkPath\platform-tools;$env:PATH"

Write-Host "=== Android Emulator Launcher ===" -ForegroundColor Cyan
Write-Host "SDK Path: $sdkPath"
Write-Host "AVD Path: $avdPath"
Write-Host ""

# List available AVDs
Write-Host "Available AVDs:" -ForegroundColor Yellow
$avdOutput = & $emulatorPath -list-avds
$avds = @($avdOutput | Where-Object { $_ -ne "" })

if ($avds.Count -eq 0) {
    Write-Host ""
    Write-Host "No AVDs found in Sandbox!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Create one in Android Studio (in the Sandbox):" -ForegroundColor Yellow
    Write-Host "  1. Start the Sandbox (.sandbox\host-scripts\start_sandbox.bat)"
    Write-Host "  2. Open Android Studio"
    Write-Host "  3. Tools → Device Manager → Create Device"
    Write-Host "  4. Select a device (e.g. Pixel 8 Pro)"
    Write-Host "  5. Select a system image (e.g. Android 15 / API 36)"
    Write-Host "  6. Finish"
    Write-Host ""
    Write-Host "The AVD will be saved in the persistent SDK and available on the host!"
    pause
    exit 1
}

# Show numbered list
for ($i = 0; $i -lt $avds.Count; $i++) {
    Write-Host "  [$($i+1)] $($avds[$i])" -ForegroundColor Cyan
}

Write-Host ""
if ($avds.Count -eq 1) {
    $selection = "1"
    Write-Host "Only one AVD found, starting: $($avds[0])" -ForegroundColor Green
} else {
    $selection = Read-Host "Select AVD number [1-$($avds.Count)] or press Enter for #1"
    if (-not $selection) {
        $selection = "1"
    }
}

$selectedIndex = [int]$selection - 1
if ($selectedIndex -lt 0 -or $selectedIndex -ge $avds.Count) {
    Write-Host "Invalid selection!" -ForegroundColor Red
    exit 1
}

$avdName = $avds[$selectedIndex]

Write-Host ""
Write-Host "Starting emulator: $avdName" -ForegroundColor Green
Write-Host "Close this window to stop the emulator"
Write-Host ""

# Start emulator
& $emulatorPath -avd $avdName -gpu host
