@echo off
echo.
echo ========================================
echo  Android Development Container Setup
echo ========================================
echo.
echo This script will install and configure:
echo  - Docker Desktop

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
    goto :check_pwsh
) else (
    echo Requesting Administrator privileges...
    goto :request_admin
)



:check_pwsh
REM Check if pwsh.exe (PowerShell 7) is available
where pwsh >nul 2>&1
if %errorlevel%==0 (
    echo PowerShell 7 (pwsh.exe) found.
    goto :run_setup
) else (
    echo PowerShell 7 (pwsh.exe) not found. Attempting installation with winget...
    where winget >nul 2>&1
    if %errorlevel%==0 (
        winget install --id Microsoft.Powershell --accept-package-agreements --accept-source-agreements
        echo.
        echo If installation was successful, please restart your computer and run this setup again.
        echo Press any key to exit.
        pause
        exit /b
    ) else (
        echo winget is not available. Please install PowerShell 7 manually: https://aka.ms/powershell
        echo Press any key to exit.
        pause
        exit /b
    )
)

:request_admin
REM Request administrator privileges and run PowerShell 7
echo atempting to request Administrator privileges...
where pwsh >nul 2>&1
if %errorlevel% == 0 (
    pwsh -Command "Start-Process -FilePath 'pwsh.exe' -ArgumentList '-NoProfile -ExecutionPolicy Bypass -File \"%~dp0setup-android-dev.ps1\"' -Verb RunAs"
) else (
    echo PowerShell 7 pwsh.exe not found. Attempting installation...
    winget install --id Microsoft.Powershell --accept-package-agreements --accept-source-agreements
    echo Installation finished. 
    goto :request_admin
)
goto :end

:run_setup
REM If already running as admin, run PowerShell 7 directly
pwsh -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup-android-dev.ps1"

:end
echo.
echo Setup completed! Please check the output above for any errors.
echo Press any key to exit.
pause
