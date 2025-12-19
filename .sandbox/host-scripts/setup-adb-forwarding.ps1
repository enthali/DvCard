# Setup ADB Port Forwarding for Sandbox Access
# REQUIRES ADMINISTRATOR RIGHTS
# Run this once, then start the emulator normally

#Requires -RunAsAdministrator

$ErrorActionPreference = "Stop"

Write-Host "=== ADB Port Forwarding Setup ===" -ForegroundColor Cyan
Write-Host ""

# Get Hyper-V vEthernet (Default Switch) IP address
$hyperVAdapter = Get-NetIPAddress -AddressFamily IPv4 | Where-Object {
    $_.InterfaceAlias -like "*vEthernet*" -and $_.IPAddress -like "172.*"
} | Select-Object -First 1

if ($null -eq $hyperVAdapter) {
    Write-Host "ERROR: Hyper-V vEthernet interface not found!" -ForegroundColor Red
    Write-Host "Make sure Hyper-V is enabled and the Default Switch exists." -ForegroundColor Yellow
    exit 1
}

$hostIP = $hyperVAdapter.IPAddress
Write-Host "Host IP (Hyper-V): $hostIP" -ForegroundColor Green
Write-Host ""

Write-Host "Setting up port forwarding..." -ForegroundColor Yellow

# Remove any existing port proxies
Write-Host "  Removing old rules..." -ForegroundColor Gray
netsh interface portproxy delete v4tov4 listenaddress=$hostIP listenport=5555 2>$null

# Add port forwarding rule (only emulator port needed!)
Write-Host "  Adding Emulator port forwarding (5555)..." -ForegroundColor Gray
netsh interface portproxy add v4tov4 listenaddress=$hostIP listenport=5555 connectaddress=127.0.0.1 connectport=5555

Write-Host ""
Write-Host "Port forwarding configured!" -ForegroundColor Green
Write-Host "  ${hostIP}:5555 -> 127.0.0.1:5555 (Emulator)" -ForegroundColor Cyan
Write-Host ""
Write-Host "Note: Each Sandbox will run its own ADB server locally." -ForegroundColor Gray
Write-Host ""
Write-Host "Verification:" -ForegroundColor Yellow
netsh interface portproxy show v4tov4
Write-Host ""

# Configure Firewall Rule for Sandbox access
Write-Host "Configuring Windows Firewall..." -ForegroundColor Yellow

# Remove old/redundant rules
Write-Host "  Cleaning up old firewall rules..." -ForegroundColor Gray
Remove-NetFirewallRule -DisplayName "Android Emulator - Allow from Sandbox" -ErrorAction SilentlyContinue | Out-Null
Remove-NetFirewallRule -DisplayName "Android Emulator - ADB TCP/IP" -ErrorAction SilentlyContinue | Out-Null

# Note: We don't remove adb.exe rules as they might be used by other tools

# Add new rule for all profiles (Public, Private, Domain)
New-NetFirewallRule -DisplayName "Android Emulator - ADB TCP/IP" `
    -Direction Inbound `
    -Protocol TCP `
    -LocalPort 5555 `
    -Action Allow `
    -Profile Any `
    -RemoteAddress LocalSubnet `
    -Description "Allow ADB connections to Android Emulator from Sandbox (172.20.x.x)" | Out-Null

Write-Host "  Firewall rule created for port 5555" -ForegroundColor Green
Write-Host "  Allowed from: LocalSubnet (includes Sandbox)" -ForegroundColor Cyan
Write-Host ""

Write-Host "You can now start the emulator normally." -ForegroundColor Green
Write-Host "From Sandbox, connect with: adb connect ${hostIP}:5555" -ForegroundColor White
Write-Host ""
