# Android Development Container Setup

This dev container provides a complete Android development environment with Android Studio GUI support on Windows.

## Prerequisites (Host System Requirements)

### Required Software

1. **Docker Desktop for Windows**

   ```powershell
   winget install Docker.DockerDesktop
   ```

   - Enable WSL2 backend in Docker Desktop settings
   - Ensure Docker Desktop is running

2. **VcXsrv X11 Server** (for GUI applications)

   ```powershell
   winget install marha.VcXsrv
   ```

   - Required for running Android Studio GUI from container
   - Must be started before using the container

3. **Visual Studio Code**

   ```powershell
   winget install Microsoft.VisualStudioCode
   ```

4. **VS Code Dev Containers Extension**
   - Install via VS Code Extensions: `ms-vscode-remote.remote-containers`
   - Or via command palette: `Extensions: Install Extensions`

### System Configuration

#### Windows Version
- **Windows 10** (version 2004 or later) or **Windows 11**
- WSL2 should be enabled (usually automatic with Docker Desktop)

#### VcXsrv Configuration
VcXsrv must be started with specific parameters for optimal performance:

```powershell
Start-Process "C:\Program Files\VcXsrv\vcxsrv.exe" -ArgumentList ":0 -ac -terminate -lesspointer -multiwindow -clipboard -wgl -dpi auto"
```

**Parameter explanation:**
- `:0` - Display number 0
- `-ac` - Disable access control (allows container connections)
- `-terminate` - Terminate when last client disconnects
- `-lesspointer` - Don't show mouse pointer when not over window
- `-multiwindow` - Each X window in separate Windows window
- `-clipboard` - Enable clipboard sharing
- `-wgl` - Use Windows OpenGL
- `-dpi auto` - Automatic DPI detection

#### VcXsrv Autostart (Optional but Recommended)
To automatically start VcXsrv when Windows boots:

1. **Create startup script**:
   ```batch
   @echo off
   "C:\Program Files\VcXsrv\vcxsrv.exe" :0 -ac -terminate -lesspointer -multiwindow -clipboard -wgl -dpi auto
   ```
   Save as `start-vcxsrv.bat` in a folder like `C:\Scripts\`

2. **Add to Windows autostart**:
   ```powershell
   # Open startup folder
   explorer "shell:startup"
   # Copy the batch file to this folder
   ```
   Or press `Win+R`, type `shell:startup`, and copy the batch file there.

3. **Alternative: Task Scheduler** (for more control):
   ```powershell
   schtasks /create /tn "VcXsrv Autostart" /tr "C:\Scripts\start-vcxsrv.bat" /sc onlogon /delay 0000:10
   ```

## Quick Start

1. **Start VcXsrv X11 Server** (manual start or skip if autostart is configured):
   ```powershell
   Start-Process "C:\Program Files\VcXsrv\vcxsrv.exe" -ArgumentList ":0 -ac -terminate -lesspointer -multiwindow -clipboard -wgl -dpi auto"
   ```

2. **Open project in VS Code**:
   ```powershell
   cd C:\path\to\DvCard
   code .
   ```

3. **Build and start container**:
   - Press `F1` → `Dev Containers: Reopen in Container`
   - Or use Command Palette: `Ctrl+Shift+P` → `Dev Containers: Rebuild Container`

4. **Start Android Studio** (once container is running):
   ```bash
   studio
   ```

## Container Features

### Installed Software
- **Ubuntu 22.04** base system
- **Java 21** (OpenJDK)
- **Android Studio** (latest stable)
- **Android SDK** with build tools, platform tools, emulator
- **Git** and development tools
- **GUI libraries** for X11 forwarding

### Performance Optimizations
- **Volume mounts** for Android SDK and Gradle cache
- **Persistent storage** survives container rebuilds
- **Native GUI performance** through X11 forwarding

### Volume Mounts
- `android-sdk-cache:/android-sdk` - Android SDK persistent storage
- `gradle-cache:/root/.gradle` - Gradle cache for faster builds
- Project files mounted to `/workspace`

## Troubleshooting

### Android Studio won't start
1. **Check VcXsrv is running**:
   ```powershell
   Get-Process -Name vcxsrv -ErrorAction SilentlyContinue
   ```

2. **Verify DISPLAY environment variable** in container:
   ```bash
   echo $DISPLAY
   # Should show: host.docker.internal:0
   ```

3. **Test X11 connection** in container:
   ```bash
   xclock  # Should show a clock window on Windows
   ```

### Container fails to build
1. **Check Docker Desktop is running**
2. **Verify WSL2 backend** is enabled in Docker settings
3. **Rebuild container** from scratch:
   - Command Palette → `Dev Containers: Rebuild Container Without Cache`

### Gradle sync issues
1. **Check Java version** in container:
   ```bash
   java -version
   # Should show Java 21
   ```

2. **Clear Gradle cache** if needed:
   ```bash
   ./gradlew clean
   ```

### Performance Issues
1. **Ensure volume mounts** are being used (check container logs)
2. **Increase Docker Desktop memory** allocation (Settings → Resources)
3. **Use SSD storage** for Docker Desktop data

## Development Workflow

1. **Start VcXsrv** (once per Windows session)
2. **Open VS Code** in project directory
3. **Reopen in Container** via Command Palette
4. **Wait for container build/start** (first time takes ~5-10 minutes)
5. **Start Android Studio** with `studio` command
6. **Develop normally** - all changes persist on host filesystem

## File Structure

```
.devcontainer/
├── devcontainer.json    # Container configuration
├── Dockerfile          # Container image definition
├── post-create.sh      # Post-creation setup script
└── README.md          # This documentation
```

## Notes

- **First build** takes significant time (~5-10 minutes) due to Android Studio download
- **Subsequent starts** are much faster thanks to volume caching
- **X11 forwarding** provides near-native GUI performance
- **Container is ephemeral** - only mounted volumes and workspace persist
- **Android emulator** works but may require additional GPU passthrough configuration

## Version Information

- **Container Base**: Ubuntu 22.04 LTS
- **Java**: OpenJDK 21
- **Android Studio**: Latest stable (downloaded during build)
- **Android SDK**: Latest stable APIs
- **X11 Server**: VcXsrv 21.1.16.1+
