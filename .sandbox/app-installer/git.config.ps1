# Git Configuration
# Applies persisted Git user configuration

param($DevPath = "C:\dev")

$configFile = Join-Path $DevPath ".sandbox\config\git-config.json"
$gitExe = "C:\Program Files\Git\bin\git.exe"

# Check if Git is installed
if (-not (Test-Path $gitExe)) {
    Write-Host "Git: Not installed, skipping configuration" -ForegroundColor Gray
    return
}

# Check if config exists
if (Test-Path $configFile) {
    # Load existing config
    Write-Host "Loading Git configuration from config/git-config.json..." -ForegroundColor Yellow
    $config = Get-Content $configFile | ConvertFrom-Json
    
    & $gitExe config --global user.name $config.name
    & $gitExe config --global user.email $config.email
    
    Write-Host "  Git configured: $($config.name) <$($config.email)>" -ForegroundColor Green
} else {
    # Interactive setup if no config exists
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Yellow
    Write-Host "Git Configuration Required" -ForegroundColor Yellow
    Write-Host "=====================================" -ForegroundColor Yellow
    Write-Host "Diese Daten werden lokal gespeichert (nicht in Git)." -ForegroundColor Cyan
    Write-Host ""
    
    $name = Read-Host "Git User Name"
    $email = Read-Host "Git User Email"
    
    if (-not $name -or -not $email) {
        Write-Host "Git: Konfiguration übersprungen (keine Eingabe)" -ForegroundColor Yellow
        return
    }
    
    # Create config directory if needed
    $configDir = Split-Path $configFile -Parent
    if (-not (Test-Path $configDir)) {
        New-Item -ItemType Directory -Path $configDir -Force | Out-Null
    }
    
    # Save config
    $config = @{
        name = $name
        email = $email
    }
    $config | ConvertTo-Json | Set-Content $configFile
    
    # Apply config
    & $gitExe config --global user.name $name
    & $gitExe config --global user.email $email
    
    Write-Host ""
    Write-Host "  Git konfiguriert und gespeichert!" -ForegroundColor Green
    Write-Host "  Bei jedem Sandbox-Start wird diese Config automatisch geladen." -ForegroundColor Cyan
}
