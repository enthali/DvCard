# Android Studio Installation
param($CacheDir, $DesktopPath)

Write-Host "Checking for latest Android Studio version..."

# Fetch latest stable version info
try {
    $downloadPage = Invoke-WebRequest -Uri "https://developer.android.com/studio" -UseBasicParsing
    $downloadPage.Content -match 'android-studio-(\d+\.\d+\.\d+\.\d+)-windows\.exe' | Out-Null
    $latestVersion = $matches[1]
    
    if (-not $latestVersion) {
        Write-Warning "Could not detect latest version, using fallback version 2025.2.2.7"
        $latestVersion = "2025.2.2.7"
    } else {
        Write-Host "Latest stable version: $latestVersion" -ForegroundColor Green
    }
} catch {
    Write-Warning "Failed to check for latest version: $_"
    Write-Host "Using fallback version 2025.2.2.7"
    $latestVersion = "2025.2.2.7"
}

$tool = @{
    Name = "Android Studio"
    Version = $latestVersion
    File = "AndroidStudio-$latestVersion-Setup.exe"
    Url = "https://redirector.gvt1.com/edgedl/android/studio/install/$latestVersion/android-studio-$latestVersion-windows.exe"
    Args = "/S"
    ExePath = "$env:ProgramFiles\Android\Android Studio\bin\studio64.exe"
}

$installerPath = Join-Path $CacheDir $tool.File

# Download if not cached
if (-not (Test-Path $installerPath)) {
    Write-Host "Downloading $($tool.Name) $($tool.Version) (~1.1 GB)..."
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    
    try {
        Invoke-WebRequest -Uri $tool.Url -OutFile $installerPath -UseBasicParsing
        Write-Host "Downloaded successfully!" -ForegroundColor Green
    } catch {
        Write-Warning "Download failed: $_"
        Write-Host "This might be due to an invalid version number or network issue."
        throw
    }
} else {
    Write-Host "Using cached $($tool.Name) $($tool.Version) installer" -ForegroundColor Green
}

# Install
Write-Host "Installing $($tool.Name)..."
$process = Start-Process -FilePath $installerPath -ArgumentList $tool.Args -PassThru -Wait

if ($process.ExitCode -eq 0 -or $process.ExitCode -eq 1223) {
    if ($process.ExitCode -eq 1223) {
        Write-Host "$($tool.Name) installed (user canceled some optional components)" -ForegroundColor Yellow
    } else {
        Write-Host "$($tool.Name) installed successfully!" -ForegroundColor Green
    }
    
    # Now setup persistent Android SDK with symlink
    $androidSdkHost = "C:\dev\.sandbox\android-sdk"
    $androidSdkUser = "$env:LOCALAPPDATA\Android\Sdk"
    
    if (-not (Test-Path $androidSdkHost)) {
        Write-Host "Creating persistent Android SDK directory..."
        New-Item -ItemType Directory -Path $androidSdkHost -Force | Out-Null
    }
    
    # If SDK directory exists but is not a symlink, move it and create symlink
    if (Test-Path $androidSdkUser) {
        $item = Get-Item $androidSdkUser
        if ($item.LinkType -ne "SymbolicLink") {
            Write-Host "Moving existing SDK to persistent storage..."
            Move-Item -Path $androidSdkUser -Destination $androidSdkHost -Force
            New-Item -ItemType SymbolicLink -Path $androidSdkUser -Target $androidSdkHost -Force | Out-Null
        }
    } else {
        # Create parent directory and symlink
        $androidParent = "$env:LOCALAPPDATA\Android"
        if (-not (Test-Path $androidParent)) {
            New-Item -ItemType Directory -Path $androidParent -Force | Out-Null
        }
        Write-Host "Creating symlink for Android SDK to persistent storage..."
        New-Item -ItemType SymbolicLink -Path $androidSdkUser -Target $androidSdkHost -Force | Out-Null
    }
    Write-Host "Android SDK will be stored persistently at C:\dev\.sandbox\android-sdk" -ForegroundColor Green
    
    # Create shortcut
    if (Test-Path $tool.ExePath) {
        $WshShell = New-Object -ComObject WScript.Shell
        $shortcut = $WshShell.CreateShortcut("$DesktopPath\Android Studio.lnk")
        $shortcut.TargetPath = $tool.ExePath
        $shortcut.WorkingDirectory = "C:\dev"
        $shortcut.Save()
        Write-Host "Desktop shortcut created!" -ForegroundColor Green
    }
} else {
    Write-Warning "$($tool.Name) installation exited with code $($process.ExitCode)"
}
