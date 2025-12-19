# Create Project Folder Desktop Link
param($CacheDir, $DesktopPath)

Write-Host "Creating desktop link to project folder..."

$WshShell = New-Object -ComObject WScript.Shell
$shortcut = $WshShell.CreateShortcut("$DesktopPath\Project Folder.lnk")
$shortcut.TargetPath = "C:\dev"
$shortcut.IconLocation = "%SystemRoot%\System32\imageres.dll,3"
$shortcut.Description = "Open project folder in Explorer"
$shortcut.Save()

Write-Host "Project folder link created!" -ForegroundColor Green
