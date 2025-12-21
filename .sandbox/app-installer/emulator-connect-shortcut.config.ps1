# Desktop Shortcut Configuration
# Creates a desktop shortcut to connect to the host emulator

param($DevPath = "C:\dev")

Write-Host "Creating Desktop Shortcut..." -ForegroundColor Yellow

$desktopPath = [Environment]::GetFolderPath("Desktop")
$shortcutPath = Join-Path $desktopPath "Connect Emulator.lnk"
$targetPath = Join-Path $DevPath ".sandbox\connect-emulator.bat"

if (-not (Test-Path $targetPath)) {
    Write-Host "  Warning: Target script not found yet: $targetPath" -ForegroundColor Yellow
    Write-Host "  Shortcut will be created anyway for later use" -ForegroundColor Gray
}

# Create shortcut using WScript.Shell
$wshell = New-Object -ComObject WScript.Shell
$shortcut = $wshell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $targetPath
$shortcut.WorkingDirectory = $DevPath
$shortcut.Description = "Connect to Android Emulator on Host"
$shortcut.IconLocation = "shell32.dll,194"  # Mobile/Phone icon
$shortcut.Save()

Write-Host "  Desktop shortcut created: Connect Emulator" -ForegroundColor Green
Write-Host "  Target: $targetPath" -ForegroundColor Cyan
