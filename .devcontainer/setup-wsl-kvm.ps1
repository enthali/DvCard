# PowerShell Script zum Aktivieren von KVM in WSL2
# Muss als Administrator ausgeführt werden!

Write-Host "🚀 Setting up WSL2 with KVM support for Android Emulator..." -ForegroundColor Green

# Prüfen ob als Administrator ausgeführt
if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "❌ This script must be run as Administrator!" -ForegroundColor Red
    Write-Host "Right-click PowerShell and 'Run as Administrator'" -ForegroundColor Yellow
    pause
    exit 1
}

# .wslconfig Pfad
$wslConfigPath = "$env:USERPROFILE\.wslconfig"

Write-Host "📝 Creating/updating .wslconfig at: $wslConfigPath" -ForegroundColor Yellow

# .wslconfig Inhalt
$wslConfigContent = @"
[wsl2]
# KVM Support für Android Emulator
nestedVirtualization=true
kernelCommandLine=intel_iommu=on iommu=pt kvm-intel.nested=1

# Performance Optimierungen
memory=8GB
processors=4
swap=2GB

# Netzwerk optimieren
networkingMode=mirrored
dnsTunneling=true
firewall=true
autoProxy=true
"@

# Datei schreiben
$wslConfigContent | Out-File -FilePath $wslConfigPath -Encoding UTF8

Write-Host "✅ .wslconfig created/updated!" -ForegroundColor Green

# WSL2 neu starten
Write-Host "🔄 Shutting down WSL2..." -ForegroundColor Yellow
wsl --shutdown

Write-Host "⏳ Waiting for WSL2 to shutdown (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "🚀 Starting WSL2..." -ForegroundColor Yellow
wsl -d docker-desktop echo "WSL2 restarted"

Write-Host ""
Write-Host "✅ WSL2 KVM setup complete!" -ForegroundColor Green
Write-Host "🎯 Next steps:" -ForegroundColor Cyan
Write-Host "  1. Rebuild your Dev Container (Ctrl+Shift+P -> 'Dev Containers: Rebuild Container')" -ForegroundColor White
Write-Host "  2. Check KVM with: ls -la /dev/kvm" -ForegroundColor White
Write-Host "  3. Hardware-accelerated Android Emulator should now work!" -ForegroundColor White
Write-Host ""

pause
