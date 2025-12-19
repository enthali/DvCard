# Main Setup Script
$ErrorActionPreference = "Stop"
$ProgressPreference = 'SilentlyContinue'

# Allow script execution for the user
try {
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force -ErrorAction Stop
} catch {}

Write-Host "=== Android Development Sandbox Setup ===" -ForegroundColor Cyan
Write-Host ""

# Prepare cache directory
$cacheDir = "C:\dev\.sandbox\downloads"
if (-not (Test-Path $cacheDir)) {
    New-Item -ItemType Directory -Path $cacheDir -Force | Out-Null
}

$desktopPath = [Environment]::GetFolderPath("Desktop")
$setupDir = "C:\dev\.sandbox"
$installerDir = Join-Path $setupDir "app-installer"

# === PHASE 1: Install Applications (Parallel) ===
Write-Host "--- Phase 1: Installing Development Tools (Parallel) ---" -ForegroundColor Yellow

# Find all *.install.ps1 scripts
$installScripts = Get-ChildItem -Path $installerDir -Filter "*.install.ps1" -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty Name

if ($installScripts.Count -eq 0) {
    Write-Host "No installation scripts found." -ForegroundColor Yellow
} else {
    $jobs = @()
    $logDir = Join-Path $setupDir "logs"
    if (-not (Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir -Force | Out-Null
    }

    foreach ($scriptName in $installScripts) {
        $scriptPath = Join-Path $installerDir $scriptName
        $appName = [System.IO.Path]::GetFileNameWithoutExtension($scriptName) -replace '\.install$', ''
        $logFile = Join-Path $logDir "$appName-install-$(Get-Date -Format 'yyyyMMdd-HHmmss').log"
        
        Write-Host "Starting: $appName (log: logs/$appName-install-*.log)" -ForegroundColor Cyan
        
        # Start each installation in a separate job
        $job = Start-Job -ScriptBlock {
            param($Script, $Cache, $Desktop, $LogPath)
            try {
                & $Script -CacheDir $Cache -DesktopPath $Desktop *>&1 | Tee-Object -FilePath $LogPath
            } catch {
                "ERROR: $_" | Out-File -FilePath $LogPath -Append
                throw
            }
        } -ArgumentList $scriptPath, $cacheDir, $desktopPath, $logFile -Name $appName
        
        $jobs += $job
    }

    # Wait for all installations to complete
    Write-Host ""
    Write-Host "Waiting for $($jobs.Count) installation(s) to complete..." -ForegroundColor Yellow
    Write-Host "(Check logs in .sandbox/logs/ for details)" -ForegroundColor Gray

    Wait-Job -Job $jobs | Out-Null

    foreach ($job in $jobs) {
        if ($job.State -eq "Completed") {
            Write-Host "  [$($job.Name)] Completed successfully" -ForegroundColor Green
        } else {
            Write-Host "  [$($job.Name)] Failed or stopped" -ForegroundColor Red
        }
        Remove-Job -Job $job
    }
}

Write-Host ""
Write-Host "--- Phase 2: Applying Configurations (Sequential) ---" -ForegroundColor Yellow

# Find all *.config.ps1 scripts and run them sequentially
$configScripts = Get-ChildItem -Path $installerDir -Filter "*.config.ps1" -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty FullName

if ($configScripts.Count -eq 0) {
    Write-Host "No configuration scripts found." -ForegroundColor Gray
} else {
    foreach ($scriptPath in $configScripts) {
        $configName = [System.IO.Path]::GetFileNameWithoutExtension($scriptPath) -replace '\.config$', ''
        
        Write-Host ""
        Write-Host "Configuring: $configName" -ForegroundColor Cyan
        
        try {
            & $scriptPath -DevPath "C:\dev"
        } catch {
            Write-Host "  [$configName] Configuration failed: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "=== Setup Complete ===" -ForegroundColor Green
Write-Host "All tools are installed and configured!"
Write-Host ""

Start-Sleep -Seconds 5
