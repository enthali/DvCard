# Quick Connect to Host Emulator
# One-click ADB connection from Sandbox

param($DevPath = "C:\dev")

Write-Host "Connecting to Host Emulator..." -ForegroundColor Cyan

# Locate ADB
$adbPath = Join-Path $DevPath ".sandbox\android-sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    Write-Host "ERROR: ADB not found at: $adbPath" -ForegroundColor Red
    Write-Host "Please install Android SDK first (start Android Studio once)" -ForegroundColor Yellow
    pause
    exit 1
}

# Get Host IP - try multiple methods
$gateway = $null

# Method 1: Default Gateway from any interface
$defaultRoute = Get-NetRoute -DestinationPrefix "0.0.0.0/0" -ErrorAction SilentlyContinue | Select-Object -First 1
if ($defaultRoute) {
    $gateway = $defaultRoute.NextHop
    Write-Host "Found gateway via default route: $gateway" -ForegroundColor Gray
}

# Method 2: DNS Server (in Sandbox, DNS = Host)
if ($null -eq $gateway) {
    $dnsServer = (Get-DnsClientServerAddress -AddressFamily IPv4 | Where-Object { $_.ServerAddresses.Count -gt 0 }).ServerAddresses | Select-Object -First 1
    if ($dnsServer -and $dnsServer -like "172.*") {
        $gateway = $dnsServer
        Write-Host "Found gateway via DNS: $gateway" -ForegroundColor Gray
    }
}

if ($null -eq $gateway) {
    Write-Host "ERROR: Could not find host IP!" -ForegroundColor Red
    Write-Host "Are you running in Windows Sandbox?" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Debug Info:" -ForegroundColor Cyan
    Get-NetIPConfiguration | Format-List
    pause
    exit 1
}

Write-Host "Host IP: $gateway" -ForegroundColor Green
Write-Host ""

# Check if emulator port is reachable
Write-Host "Checking if emulator is running on host..." -ForegroundColor Yellow
$testConnection = Test-NetConnection -ComputerName $gateway -Port 5555 -WarningAction SilentlyContinue

if (-not $testConnection.TcpTestSucceeded) {
    Write-Host ""
    Write-Host "Emulator not reachable!" -ForegroundColor Red
    Write-Host "Please start the emulator on the host first:" -ForegroundColor Yellow
    Write-Host "  .sandbox\host-scripts\start_emulator.bat" -ForegroundColor White
    Write-Host ""
    Write-Host "Waiting for emulator..." -ForegroundColor Cyan
    
    # Wait up to 60 seconds
    $waited = 0
    while ($waited -lt 60) {
        Start-Sleep -Seconds 5
        $waited += 5
        Write-Host "  Checking... ($waited/60s)" -ForegroundColor Gray
        $testConnection = Test-NetConnection -ComputerName $gateway -Port 5555 -WarningAction SilentlyContinue
        if ($testConnection.TcpTestSucceeded) {
            Write-Host "  Emulator is ready!" -ForegroundColor Green
            break
        }
    }
    
    if (-not $testConnection.TcpTestSucceeded) {
        Write-Host ""
        Write-Host "Timeout! Emulator still not reachable." -ForegroundColor Red
        Write-Host "Start it manually and run this script again." -ForegroundColor Yellow
        Write-Host ""
        pause
        exit 1
    }
}

Write-Host "Emulator reachable!" -ForegroundColor Green
Write-Host ""

# Connect to emulator
Write-Host "Connecting to ${gateway}:5555..." -ForegroundColor Cyan
& $adbPath connect "${gateway}:5555"

Write-Host ""
Write-Host "Devices:" -ForegroundColor Yellow
& $adbPath devices

Write-Host ""
Write-Host "Ready to build and install!" -ForegroundColor Green
Write-Host ""
pause
