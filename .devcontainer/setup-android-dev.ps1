#Requires -RunAsAdministrator
<#
.SYNOPSIS
    Automated setup script for Android Development Container with GUI support
.DESCRIPTION
    This script automatically installs and configures all required software for the
    Android Development Container environment with Android Studio GUI support.
.NOTES
    Run as Administrator for full installation
    Compatible with Windows 10 (2004+) and Windows 11
#>

param(
    [switch]$SkipSoftware = $false,
    [switch]$SkipAutostart = $false,
    [switch]$Verbose = $false
)

# Set error handling
$ErrorActionPreference = "Stop"

# Colors for output
$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"
$Cyan = "Cyan"

function Write-Step {
    param([string]$Message)
    Write-Host "🔧 $Message" -ForegroundColor $Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor $Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor $Red
}

function Test-IsAdmin {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Test-SoftwareInstalled {
    param([string]$Name, [string]$Command)
    
    try {
        if ($Command) {
            $result = & $Command 2>$null
            return $true
        } else {
            $app = Get-AppxPackage -Name "*$Name*" -ErrorAction SilentlyContinue
            return $app -ne $null
        }
    } catch {
        return $false
    }
}

function Install-Prerequisites {
    Write-Step "Installing required software..."
    
    # Check if winget is available
    try {
        winget --version | Out-Null
    } catch {
        Write-Error "winget is not available. Please install App Installer from Microsoft Store."
        exit 1
    }

    # Install Docker Desktop
    if (-not (Test-SoftwareInstalled -Name "Docker" -Command "docker")) {
        Write-Step "Installing Docker Desktop..."
        try {
            winget install --id Docker.DockerDesktop --accept-package-agreements --accept-source-agreements
            Write-Success "Docker Desktop installed"
        } catch {
            Write-Warning "Failed to install Docker Desktop via winget. Please install manually."
        }
    } else {
        Write-Success "Docker Desktop already installed"
    }

    # Install VcXsrv


    # Install VS Code
    if (-not (Test-SoftwareInstalled -Command "code")) {
        Write-Step "Installing Visual Studio Code..."
        try {
            winget install --id Microsoft.VisualStudioCode --accept-package-agreements --accept-source-agreements
            Write-Success "VS Code installed"
        } catch {
            Write-Warning "Failed to install VS Code via winget. Please install manually."
        }
    } else {
        Write-Success "VS Code already installed"
    }

    # Install Git (if not present)
    if (-not (Test-SoftwareInstalled -Command "git")) {
        Write-Step "Installing Git..."
        try {
            winget install --id Git.Git --accept-package-agreements --accept-source-agreements
            Write-Success "Git installed"
        } catch {
            Write-Warning "Failed to install Git via winget. Please install manually."
        }
    } else {
        Write-Success "Git already installed"
    }
}

function Install-VSCodeExtensions {
    Write-Step "Installing VS Code extensions..."
    
    $extensions = @(
        "ms-vscode-remote.remote-containers",
        "ms-vscode-remote.remote-wsl",
        "GitHub.copilot"
    )
    
    foreach ($extension in $extensions) {
        try {
            Write-Step "Installing extension: $extension"
            & code --install-extension $extension --force
            Write-Success "Extension $extension installed"
        } catch {
            Write-Warning "Failed to install extension: $extension"
        }
    }
}

function Setup-VcXsrvAutostart {

}

function Setup-WSLKvmSupport {
    Write-Step "Configuring WSL2 for Android Emulator hardware acceleration..."
    
    # .wslconfig Pfad
    $wslConfigPath = "$env:USERPROFILE\.wslconfig"
    
    # .wslconfig Inhalt für KVM Support
    $wslConfigContent = @"
[wsl2]
# KVM Support für Android Emulator Hardware Acceleration
nestedVirtualization=true
kernelCommandLine=intel_iommu=on iommu=pt kvm-intel.nested=1

# Performance Optimierungen für Android Development
memory=8GB
processors=4
swap=2GB

# Netzwerk Optimierungen
networkingMode=mirrored
dnsTunneling=true
firewall=true
autoProxy=true
"@

    try {
        # Backup existing .wslconfig if it exists
        if (Test-Path $wslConfigPath) {
            $backupPath = "$wslConfigPath.backup.$(Get-Date -Format 'yyyyMMdd-HHmmss')"
            Copy-Item $wslConfigPath $backupPath
            Write-Success "Existing .wslconfig backed up to: $backupPath"
        }
        
        # Write new .wslconfig
        $wslConfigContent | Out-File -FilePath $wslConfigPath -Encoding UTF8
        Write-Success ".wslconfig created/updated with KVM support"
        
        # Shutdown and restart WSL2
        Write-Step "Restarting WSL2 to apply KVM settings..."
        wsl --shutdown
        Start-Sleep -Seconds 10
        
        # Test WSL2 restart
        $wslTest = wsl -d docker-desktop echo "WSL2 test" 2>$null
        if ($wslTest -eq "WSL2 test") {
            Write-Success "WSL2 restarted successfully with new configuration"
        } else {
            Write-Warning "WSL2 restart may have failed. Please restart manually: 'wsl --shutdown' and then start Docker Desktop"
        }
        
    } catch {
        Write-Warning "Failed to configure WSL2 KVM support: $($_.Exception.Message)"
        Write-Host "You can manually create .wslconfig at: $wslConfigPath" -ForegroundColor $Yellow
    }
}

function Test-DockerConfiguration {
    Write-Step "Testing Docker configuration..."
    
    try {
        $dockerInfo = docker info 2>$null
        if ($dockerInfo) {
            Write-Success "Docker is running and accessible"
            
            # Check WSL2 backend
            if ($dockerInfo -match "WSL 2") {
                Write-Success "Docker is using WSL2 backend (recommended)"
            } else {
                Write-Warning "Docker is not using WSL2 backend. Consider enabling it for better performance."
            }
        } else {
            Write-Warning "Docker is installed but not running. Please start Docker Desktop."
        }
    } catch {
        Write-Warning "Docker is not accessible. Please ensure Docker Desktop is installed and running."
    }
}

function Show-PostInstallInstructions {
    Write-Host "`n" -NoNewline
    Write-Host "🎉 Setup completed! " -ForegroundColor $Green -NoNewline
    Write-Host "Next steps:" -ForegroundColor $Cyan
    Write-Host ""
    
    Write-Host "1. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Restart your computer to ensure all installations are properly loaded"
    
    Write-Host "2. " -ForegroundColor $Yellow -NoNewline  
    Write-Host "After restart, VcXsrv should start automatically"
    
    Write-Host "3. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Open your Android project in VS Code:"
    Write-Host "   cd C:\path\to\your\android\project" -ForegroundColor Gray
    Write-Host "   code ." -ForegroundColor Gray
    
    Write-Host "4. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Use Command Palette (Ctrl+Shift+P) -> 'Dev Containers: Reopen in Container'"
    
    Write-Host "5. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Rebuild your Dev Container to apply KVM settings:"
    Write-Host "   Ctrl+Shift+P -> 'Dev Containers: Rebuild Container'" -ForegroundColor Gray
    
    Write-Host "6. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Once container is running, start Android Studio:"
    Write-Host "   studio" -ForegroundColor Gray
    
    Write-Host "7. " -ForegroundColor $Yellow -NoNewline
    Write-Host "Test Android Emulator with hardware acceleration:"
    Write-Host "   ls -la /dev/kvm  # Should show KVM device" -ForegroundColor Gray
    Write-Host "   emulator -avd TestPixelHW  # Hardware accelerated" -ForegroundColor Gray
    
    Write-Host "`n" -NoNewline
    Write-Host "📋 Troubleshooting:" -ForegroundColor $Cyan

    
    Write-Host "`n" -NoNewline
    Write-Host "📁 Created files:" -ForegroundColor $Cyan

    
    Write-Host "`nHappy coding! 🚀" -ForegroundColor $Green
}

# Main execution
try {
    Write-Host "🚀 Android Development Container Setup" -ForegroundColor $Green
    Write-Host "=====================================" -ForegroundColor $Green
    Write-Host ""
    
    # Check if running as administrator
    if (-not (Test-IsAdmin)) {
        Write-Error "This script requires Administrator privileges. Please run as Administrator."
        exit 1
    }
    
    Write-Success "Running as Administrator ✓"
    Write-Host ""
    
    # Install software
    if (-not $SkipSoftware) {
        Install-Prerequisites
        Write-Host ""
        
        # Brief pause to let installations settle
        Write-Step "Waiting for installations to complete..."
        Start-Sleep -Seconds 5
        
        Install-VSCodeExtensions
        Write-Host ""
    } else {
        Write-Warning "Skipping software installation (--SkipSoftware specified)"
    }
    
    # Setup autostart
    if (-not $SkipAutostart) {
        Setup-VcXsrvAutostart
        Write-Host ""
    } else {
        Write-Warning "Skipping autostart setup (--SkipAutostart specified)"
    }
    
    # Setup WSL2 KVM Support for Android Emulator
    Setup-WSLKvmSupport
    Write-Host ""
    
    # Test Docker
    Test-DockerConfiguration
    Write-Host ""
    
    # Show final instructions
    Show-PostInstallInstructions
    
} catch {
    Write-Error "Setup failed: $($_.Exception.Message)"
    Write-Host "Please check the error and run the script again, or install components manually." -ForegroundColor $Yellow
    exit 1
}
