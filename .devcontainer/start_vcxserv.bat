@echo off
REM VcXsrv X11 Server Autostart Script for Android Development Container
REM This script starts VcXsrv with optimal settings for GUI forwarding

echo Starting VcXsrv X11 Server for Android Studio...
start "" "C:\Program Files\VcXsrv\vcxsrv.exe" :0 -ac -terminate -lesspointer -multiwindow -clipboard -wgl -dpi auto

echo VcXsrv started. You can now use Android Studio in containers.
