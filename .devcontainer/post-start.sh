#!/bin/bash
# Auto-start VNC Server for NoVNC
# This script starts the VNC server automatically if not already running

VNC_DISPLAY=":1"
VNC_GEOMETRY="1920x1080"
VNC_DEPTH="24"

echo "🖥️  Starting VNC server for Android Studio GUI..."

# Check if VNC server is already running
if vncserver -list 2>/dev/null | grep -q "^:1"; then
    echo "✅ VNC server already running on display $VNC_DISPLAY"
else
    # Start VNC server (desktop-lite feature handles this automatically)
    echo "🚀 VNC server starting via desktop-lite feature..."
    
    # Set DISPLAY for current session
    export DISPLAY=$VNC_DISPLAY
    echo "export DISPLAY=$VNC_DISPLAY" >> ~/.bashrc
    
    echo "✅ VNC server ready!"
fi

echo "📱 Access Android Studio GUI via NoVNC on port 6080"
echo "🌐 URL: http://localhost:6080"
echo "🔑 Password: vscode"
echo "📺 Local DISPLAY set to: $VNC_DISPLAY"

# Start Android Studio in background
echo "🚀 Starting Android Studio..."
nohup /opt/android-studio/bin/studio.sh > ~/.android-studio.log 2>&1 &
echo "✅ Android Studio started in background"

# Log execution time
echo "post-start.sh executed at $(date)" >> ~/.devcontainer-post-start.log