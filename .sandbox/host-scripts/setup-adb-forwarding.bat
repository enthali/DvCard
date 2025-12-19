@echo off
:: Check for admin rights
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo This script requires Administrator rights!
    echo Please run as Administrator.
    pause
    exit /b 1
)

:: Run PowerShell script with admin rights
powershell.exe -ExecutionPolicy Bypass -File "%~dp0setup-adb-forwarding.ps1"
pause
