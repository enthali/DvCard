@echo off
echo.
echo ========================================
echo  Android Development Container Setup
echo ========================================
echo.
echo This script will install and configure:
echo  - Docker Desktop
echo  - VcXsrv X11 Server (for Android Studio GUI)
echo  - Visual Studio Code + Extensions
echo  - Git
echo  - WSL2 with KVM support for Android Emulator
echo.
echo IMPORTANT: This requires Administrator privileges!
echo.
pause

REM Check if running as administrator
net session >nul 2>&1
if %errorlevel% == 0 (
    echo Running as Administrator - continuing...
    goto :run_setup
) else (
    echo Requesting Administrator privileges...
    goto :request_admin
)

:request_admin
REM Request administrator privileges and run PowerShell 7.5.2
pwsh -Command "Start-Process -FilePath 'pwsh.exe' -ArgumentList '-NoProfile -ExecutionPolicy Bypass -File \"%~dp0setup-android-dev.ps1\"' -Verb RunAs"
goto :end

:run_setup
REM If already running as admin, run PowerShell 7.5.2 directly
pwsh -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup-android-dev.ps1"

:end
echo.
echo Setup completed! Please check the output above for any errors.
pause
