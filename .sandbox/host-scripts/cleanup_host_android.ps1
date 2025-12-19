# Cleanup Android Studio Host Installation
# Removes all Android-related files from the host system

Write-Host "=== Android Studio Host Cleanup ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "This will remove all Android Studio and SDK files from your host system." -ForegroundColor Yellow
Write-Host "The Sandbox setup (.sandbox\android-sdk) will NOT be affected!" -ForegroundColor Green
Write-Host ""

$confirm = Read-Host "Continue? (yes/no)"
if ($confirm -ne "yes") {
    Write-Host "Cancelled." -ForegroundColor Yellow
    exit
}

Write-Host ""
Write-Host "Checking for Android installations..." -ForegroundColor Cyan
Write-Host ""

$cleaned = @()

# 1. User Profile .android folder
$userAndroid = Join-Path $env:USERPROFILE ".android"
if (Test-Path $userAndroid) {
    $item = Get-Item $userAndroid
    if ($item.LinkType -eq "SymbolicLink") {
        Write-Host "[SKIP] $userAndroid (Symlink to Sandbox - keeping it!)" -ForegroundColor Green
    } else {
        Write-Host "[DELETE] $userAndroid" -ForegroundColor Red
        Remove-Item $userAndroid -Recurse -Force -ErrorAction SilentlyContinue
        $cleaned += $userAndroid
    }
} else {
    Write-Host "[OK] $userAndroid (not found)" -ForegroundColor Gray
}

# 2. LocalAppData Android SDK
$localAndroid = Join-Path $env:LOCALAPPDATA "Android"
if (Test-Path $localAndroid) {
    Write-Host "[DELETE] $localAndroid" -ForegroundColor Red
    Remove-Item $localAndroid -Recurse -Force -ErrorAction SilentlyContinue
    $cleaned += $localAndroid
} else {
    Write-Host "[OK] $localAndroid (not found)" -ForegroundColor Gray
}

# 3. Android Studio Settings (AppData\Roaming)
$studioSettings = Get-ChildItem "$env:APPDATA\Google" -Filter "AndroidStudio*" -Directory -ErrorAction SilentlyContinue
foreach ($dir in $studioSettings) {
    Write-Host "[DELETE] $($dir.FullName)" -ForegroundColor Red
    Remove-Item $dir.FullName -Recurse -Force -ErrorAction SilentlyContinue
    $cleaned += $dir.FullName
}
if (-not $studioSettings) {
    Write-Host "[OK] $env:APPDATA\Google\AndroidStudio* (not found)" -ForegroundColor Gray
}

# 4. Android Studio Settings (AppData\Local)
$studioSettingsLocal = Get-ChildItem "$env:LOCALAPPDATA\Google" -Filter "AndroidStudio*" -Directory -ErrorAction SilentlyContinue
foreach ($dir in $studioSettingsLocal) {
    Write-Host "[DELETE] $($dir.FullName)" -ForegroundColor Red
    Remove-Item $dir.FullName -Recurse -Force -ErrorAction SilentlyContinue
    $cleaned += $dir.FullName
}
if (-not $studioSettingsLocal) {
    Write-Host "[OK] $env:LOCALAPPDATA\Google\AndroidStudio* (not found)" -ForegroundColor Gray
}

# 5. Program Files Android Studio Installation
$programAndroid = "$env:ProgramFiles\Android"
if (Test-Path $programAndroid) {
    Write-Host "[DELETE] $programAndroid" -ForegroundColor Red
    Remove-Item $programAndroid -Recurse -Force -ErrorAction SilentlyContinue
    $cleaned += $programAndroid
} else {
    Write-Host "[OK] $programAndroid (not found)" -ForegroundColor Gray
}

# 6. Gradle Cache (optional)
$gradleCache = Join-Path $env:USERPROFILE ".gradle"
if (Test-Path $gradleCache) {
    Write-Host "[FOUND] $gradleCache" -ForegroundColor Yellow
    $cleanGradle = Read-Host "  Delete Gradle cache? (yes/no)"
    if ($cleanGradle -eq "yes") {
        Write-Host "[DELETE] $gradleCache" -ForegroundColor Red
        Remove-Item $gradleCache -Recurse -Force -ErrorAction SilentlyContinue
        $cleaned += $gradleCache
    } else {
        Write-Host "[KEEP] $gradleCache" -ForegroundColor Green
    }
} else {
    Write-Host "[OK] $gradleCache (not found)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== Cleanup Complete ===" -ForegroundColor Green

if ($cleaned.Count -gt 0) {
    Write-Host ""
    Write-Host "Removed $($cleaned.Count) item(s):" -ForegroundColor Yellow
    foreach ($item in $cleaned) {
        Write-Host "  - $item" -ForegroundColor Gray
    }
} else {
    Write-Host "No Android files found on host system - already clean!" -ForegroundColor Green
}

Write-Host ""
Write-Host "Your Sandbox SDK is safe at: .sandbox\android-sdk\" -ForegroundColor Cyan
Write-Host ""
pause
