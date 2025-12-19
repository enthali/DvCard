$ErrorActionPreference = "Stop"

# 1. Get current absolute path (sandbox directory)
$sandboxDir = (Get-Item $PSScriptRoot).FullName
$projectRoot = Split-Path $sandboxDir -Parent
Write-Host "Starting Sandbox from: $projectRoot"
Write-Host "Sandbox config: $sandboxDir"

# 2. Generate .wsb from template
$wsbPath = Join-Path $sandboxDir "sandbox.wsb"
$templatePath = Join-Path $sandboxDir "sandbox.wsb.template"

if (-not (Test-Path $templatePath)) {
    Write-Error "Template file not found: $templatePath"
    exit 1
}

Write-Host "Generating sandbox.wsb from template..."
$content = Get-Content $templatePath -Raw
$content = $content -replace '\{\{PROJECT_ROOT\}\}', $projectRoot
$content | Set-Content $wsbPath -Encoding UTF8

Write-Host "HostFolder set to: $projectRoot"
Write-Host "Launching Windows Sandbox..."
Start-Process -FilePath $wsbPath -Wait
