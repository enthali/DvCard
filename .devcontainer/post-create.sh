#!/bin/bash

echo "🚀 Setting up Android Development Container..."

# KVM Support prüfen und konfigurieren
echo "🔍 Checking KVM support..."
if [ -e /dev/kvm ]; then
    echo "✅ KVM device found - Hardware acceleration available!"
    chmod 666 /dev/kvm
    # Hardware-accelerated AVD erstellen falls nicht vorhanden
    if ! avdmanager list avd | grep -q "TestPixelHW"; then
        echo "📱 Creating hardware-accelerated AVD..."
        echo "no" | avdmanager create avd -n "TestPixelHW" -k "system-images;android-36;google_apis_playstore;x86_64" -d "pixel_4"
        echo "hw.gpu.enabled=yes" >> ~/.android/avd/TestPixelHW.avd/config.ini
        echo "hw.gpu.mode=host" >> ~/.android/avd/TestPixelHW.avd/config.ini
    fi
else
    echo "⚠️  KVM device not found - Using software emulation"
    # Software AVD erstellen
    if ! avdmanager list avd | grep -q "TestPixelSoft"; then
        echo "📱 Creating software-emulated AVD..."
        echo "no" | avdmanager create avd -n "TestPixelSoft" -k "system-images;android-36;google_apis_playstore;x86_64" -d "pixel_4"
        echo "hw.gpu.enabled=yes" >> ~/.android/avd/TestPixelSoft.avd/config.ini
        echo "hw.gpu.mode=swiftshader_indirect" >> ~/.android/avd/TestPixelSoft.avd/config.ini
    fi
fi

# Gradle Wrapper ausführbar machen
chmod +x ./gradlew

# Android SDK Lizenzen akzeptieren
yes | $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --licenses

# Gradle Cache vorbereiten
mkdir -p /root/.gradle

# Android Studio Desktop-Verknüpfung erstellen
cat > /root/Desktop/AndroidStudio.desktop << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=Android Studio
Comment=Android Studio IDE
Exec=/opt/android-studio/bin/studio.sh
Icon=/opt/android-studio/bin/studio.png
Terminal=false
StartupWMClass=jetbrains-studio
Categories=Development;IDE;
EOF

chmod +x /root/Desktop/AndroidStudio.desktop

# ADB Server starten
$ANDROID_SDK_ROOT/platform-tools/adb start-server

# Git configuration check
echo "🔧 Checking Git configuration..."
if ! git config user.name >/dev/null 2>&1 || ! git config user.email >/dev/null 2>&1; then
    echo "⚠️  Git user not configured!"
    echo "Please run these commands to set up Git:"
    echo "  git config --global user.name \"Your Name\""
    echo "  git config --global user.email \"your.email@example.com\""
    echo ""
else
    echo "✅ Git configuration found - you're ready to commit!"
fi

echo "✅ Android Development Container setup complete!"
echo ""
echo "🎯 How to use:"
echo "  • Run 'studio' to start Android Studio (GUI via VcXsrv)"
echo "  • Run './gradlew build' for command line builds"
echo "  • ADB is available for device connections"
if [ -e /dev/kvm ]; then
    echo "  • Run 'emulator -avd TestPixelHW' for hardware-accelerated emulator"
else
    echo "  • Run 'emulator -avd TestPixelSoft -no-accel -gpu swiftshader_indirect' for software emulator"
fi
echo "  • AVDs are pre-configured and ready to use"
echo ""
