# VS Code Installation
param($CacheDir, $DesktopPath)

$tool = @{
    Name = "VS Code"
    File = "VSCodeSetup.exe"
    Url = "https://code.visualstudio.com/sha/download?build=stable&os=win32-x64-user"
    Args = "/VERYSILENT /NORESTART /MERGETASKS=!runcode"
    ExePath = "$env:LOCALAPPDATA\Programs\Microsoft VS Code\Code.exe"
}

$installerPath = Join-Path $CacheDir $tool.File

# Download if not cached
if (-not (Test-Path $installerPath)) {
    Write-Host "Downloading $($tool.Name)..."
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $tool.Url -OutFile $installerPath -UseBasicParsing
    Write-Host "Downloaded!"
} else {
    Write-Host "Using cached $($tool.Name) installer"
}

# Install
Write-Host "Installing $($tool.Name)..."
$process = Start-Process -FilePath $installerPath -ArgumentList $tool.Args -PassThru -Wait
if ($process.ExitCode -eq 0) {
    Write-Host "$($tool.Name) installed successfully!" -ForegroundColor Green
    
    # Create shortcut
    if (Test-Path $tool.ExePath) {
        $WshShell = New-Object -ComObject WScript.Shell
        $shortcut = $WshShell.CreateShortcut("$DesktopPath\VS Code.lnk")
        $shortcut.TargetPath = $tool.ExePath
        $shortcut.WorkingDirectory = "C:\dev"
        $shortcut.Save()
        Write-Host "Desktop shortcut created!" -ForegroundColor Green
    }
} else {
    Write-Warning "$($tool.Name) installation exited with code $($process.ExitCode)"
}
