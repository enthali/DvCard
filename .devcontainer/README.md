# Android Development Container Setup

This dev container provides a complete Android development environment with Android Studio GUI support via **NoVNC** (browser-based).

## Prerequisites (Host System Requirements)

### Required Software

1. **Docker Desktop for Windows**

   ```powershell
   winget install Docker.DockerDesktop
   ```

   - Enable WSL2 backend in Docker Desktop settings
   - Ensure Docker Desktop is running

2. **Visual Studio Code**

   ```powershell
   winget install Microsoft.VisualStudioCode
   ```

3. **VS Code Dev Containers Extension**
   - Install via VS Code Extensions: `ms-vscode-remote.remote-containers`
   - Or via command palette: `Extensions: Install Extensions`

### System Configuration

#### Windows Version
- **Windows 10** (version 2004 or later) or **Windows 11**
- WSL2 should be enabled (usually automatic with Docker Desktop)

## Quick Start

1. **Open project in VS Code**:
   ```powershell
   cd C:\path\to\DvCard
   code .
   ```

2. **Build and start container**:
   - Press `F1` → `Dev Containers: Reopen in Container`
   - Or use Command Palette: `Ctrl+Shift+P` → `Dev Containers: Rebuild Container`

3. **Access Android Studio GUI**:
   - Open browser: `http://localhost:6080`
   - Password: `vscode`
   - Start Android Studio from terminal or desktop icon

## Container Features

### Installed Software
- **Ubuntu 22.04** base system
- **Java 21** (OpenJDK)
- **Android Studio** (latest stable)
- **Android SDK** with build tools, platform tools, emulator
- **Git** and development tools
- **NoVNC** for browser-based GUI access
- **XFCE Desktop** environment

### Performance Optimizations
- **Volume mounts** for Android SDK and Gradle cache
- **Persistent storage** survives container rebuilds
- **Browser-based GUI** - no X11 server setup needed
- **Works everywhere**: Local, Codespaces, any Docker host

### Volume Mounts
- `android-sdk-cache:/android-sdk` - Android SDK persistent storage
- `gradle-cache:/home/vscode/.gradle` - Gradle cache for faster builds
- Project files mounted to `/workspace`

## GUI Access

### NoVNC (Browser-based)
- **URL**: `http://localhost:6080`
- **Password**: `vscode`
- **Resolution**: 1920x1080 (configurable)
- **Advantages**:
  - ✅ Works on any OS (Windows, Mac, Linux)
  - ✅ Works in GitHub Codespaces
  - ✅ No additional software needed
  - ✅ Clipboard sharing built-in
  - ✅ Multi-user friendly

## Troubleshooting

### Cannot access noVNC
1. **Check port forwarding**:
   - In VS Code, check Ports panel (usually bottom)
   - Port 6080 should be listed and forwarded

2. **Verify VNC server** in container:
   ```bash
   vncserver -list
   # Should show :1 running
   ```

3. **Check DISPLAY variable**:
   ```bash
   echo $DISPLAY
   # Should show: :1
   ```

### Android Studio won't start
1. **Test X11 connection** in container:
   ```bash
   xclock  # Should show in noVNC browser window
   ```

2. **Check logs**:
   ```bash
   tail -f ~/.vnc/*.log
   ```

### Container fails to build
1. **Check Docker Desktop is running**
2. **Verify WSL2 backend** is enabled in Docker settings
3. **Rebuild container** from scratch:
   - Command Palette → `Dev Containers: Rebuild Container Without Cache`

### Performance Issues
1. **Increase Docker Desktop memory** allocation (Settings → Resources)
2. **Use SSD storage** for Docker Desktop data
3. **Adjust noVNC quality** in browser settings (compression slider)

## Development Workflow

1. **Open VS Code** in project directory
2. **Reopen in Container** via Command Palette
3. **Wait for container build/start** (first time takes ~5-10 minutes)
4. **Open browser** to `http://localhost:6080`
5. **Start Android Studio** from desktop or terminal: `studio`
6. **Develop normally** - all changes persist on host filesystem

## File Structure

```
.devcontainer/
├── devcontainer.json    # Container configuration (NoVNC feature)
├── Dockerfile          # Container image definition
├── post-create.sh      # Post-creation setup script
├── post-start.sh       # VNC server auto-start script
└── README.md          # This documentation
```

## Notes

- **First build** takes significant time (~5-10 minutes) due to Android Studio download
- **Subsequent starts** are much faster thanks to volume caching
- **NoVNC provides** near-native GUI performance in modern browsers
- **Container is ephemeral** - only mounted volumes and workspace persist
- **Android emulator** requires KVM support (works with local Docker, limited in Codespaces)
- **No Windows-specific tools needed** - works on any OS

## Version Information

- **Container Base**: Ubuntu 22.04 LTS
- **Java**: OpenJDK 21
- **Android Studio**: Latest stable (downloaded during build)
- **Android SDK**: Latest stable APIs
- **GUI**: NoVNC + XFCE Desktop (via desktop-lite feature)
