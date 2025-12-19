# Android Studio Configuration
# Redirects .android user directory and IDE settings to persistent SDK storage

param($DevPath = "C:\dev")

Write-Host "Configuring Android Studio paths..." -ForegroundColor Yellow

$sdkPath = Join-Path $DevPath ".sandbox\android-sdk"
$androidUserPath = Join-Path $sdkPath ".android"
$userProfileAndroid = Join-Path $env:USERPROFILE ".android"

# IDE Settings paths
$ideSettingsPath = Join-Path $DevPath ".sandbox\android-studio-settings"
$ideSettingsRoaming = Join-Path $ideSettingsPath "roaming"
$ideSettingsLocal = Join-Path $ideSettingsPath "local"

# Create .android directory in SDK if it doesn't exist
if (-not (Test-Path $androidUserPath)) {
    New-Item -ItemType Directory -Path $androidUserPath -Force | Out-Null
    Write-Host "  Created: $androidUserPath" -ForegroundColor Green
}

# Create IDE settings directories
if (-not (Test-Path $ideSettingsRoaming)) {
    New-Item -ItemType Directory -Path $ideSettingsRoaming -Force | Out-Null
}
if (-not (Test-Path $ideSettingsLocal)) {
    New-Item -ItemType Directory -Path $ideSettingsLocal -Force | Out-Null
}

# Remove existing .android if it exists
if (Test-Path $userProfileAndroid) {
    Remove-Item $userProfileAndroid -Force -Recurse -ErrorAction SilentlyContinue
}

# Create symlink for .android
Write-Host "  Creating symlink: $userProfileAndroid -> $androidUserPath" -ForegroundColor Yellow
New-Item -ItemType SymbolicLink -Path $userProfileAndroid -Target $androidUserPath -Force | Out-Null

# Symlink IDE Settings (Roaming)
$googleRoaming = Join-Path $env:APPDATA "Google"
if (-not (Test-Path $googleRoaming)) {
    New-Item -ItemType Directory -Path $googleRoaming -Force | Out-Null
}

# Find existing AndroidStudio folders and remove them
Get-ChildItem $googleRoaming -Filter "AndroidStudio*" -Directory -ErrorAction SilentlyContinue | 
    ForEach-Object { Remove-Item $_.FullName -Force -Recurse -ErrorAction SilentlyContinue }

# Symlink IDE Settings (Local)
$googleLocal = Join-Path $env:LOCALAPPDATA "Google"
if (-not (Test-Path $googleLocal)) {
    New-Item -ItemType Directory -Path $googleLocal -Force | Out-Null
}

Get-ChildItem $googleLocal -Filter "AndroidStudio*" -Directory -ErrorAction SilentlyContinue | 
    ForEach-Object { Remove-Item $_.FullName -Force -Recurse -ErrorAction SilentlyContinue }

# Detect Android Studio version from installation
$studioInstallPath = "$env:ProgramFiles\Android\Android Studio"
if (Test-Path $studioInstallPath) {
    # Read build.txt to get version
    $buildTxt = Join-Path $studioInstallPath "build.txt"
    if (Test-Path $buildTxt) {
        $buildInfo = Get-Content $buildTxt -First 1
        # Extract version like "AI-252.5952.40" -> "AndroidStudio2025.2"
        if ($buildInfo -match 'AI-(\d{3})\.') {
            $versionCode = $matches[1]
            $year = "20" + $versionCode.Substring(0,2)
            $major = [int]$versionCode.Substring(2,1)
            $studioVersion = "AndroidStudio$year.$major"
            
            $settingsRoamingLink = Join-Path $googleRoaming $studioVersion
            $settingsLocalLink = Join-Path $googleLocal $studioVersion
            
            Write-Host "  Creating IDE settings symlinks for $studioVersion..." -ForegroundColor Yellow
            New-Item -ItemType SymbolicLink -Path $settingsRoamingLink -Target $ideSettingsRoaming -Force -ErrorAction SilentlyContinue | Out-Null
            New-Item -ItemType SymbolicLink -Path $settingsLocalLink -Target $ideSettingsLocal -Force -ErrorAction SilentlyContinue | Out-Null
            
            Write-Host "  IDE settings redirected to persistent storage!" -ForegroundColor Green
        }
    }
}

# Add SDK tools to System PATH
$sdkPlatformTools = Join-Path $sdkPath "platform-tools"
$sdkEmulator = Join-Path $sdkPath "emulator"

$currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$pathsToAdd = @($sdkPlatformTools, $sdkEmulator)

foreach ($pathToAdd in $pathsToAdd) {
    if ($currentPath -notlike "*$pathToAdd*") {
        $newPath = "$pathToAdd;$currentPath"
        [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
        $env:PATH = "$pathToAdd;$env:PATH"
        Write-Host "  Added to PATH: $pathToAdd" -ForegroundColor Green
    }
}

Write-Host "  Android user directory redirected to persistent storage!" -ForegroundColor Green
Write-Host "  ADB and Emulator tools added to system PATH!" -ForegroundColor Cyan
