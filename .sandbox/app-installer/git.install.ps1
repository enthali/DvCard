# Git Installation
param($CacheDir)

$tool = @{
    Name = "Git"
    File = "Git-Setup.exe"
    Url = "https://github.com/git-for-windows/git/releases/download/v2.47.1.windows.1/Git-2.47.1-64-bit.exe"
    Args = "/VERYSILENT /NORESTART"
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
} else {
    Write-Warning "$($tool.Name) installation exited with code $($process.ExitCode)"
}
