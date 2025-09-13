# Quick Setup - Android Development Container

## One-Command Setup

Run this single PowerShell command as **Administrator** to set up everything:

```powershell
# Download and run setup script
iwr -useb https://raw.githubusercontent.com/YOUR_USERNAME/DvCard/main/.devcontainer/setup-android-dev.ps1 | iex
```

Or download and run locally:

```powershell
# Navigate to project folder
cd C:\path\to\DvCard

# Run setup script as Administrator
.\.devcontainer\setup-android-dev.ps1
```

## What the script does

✅ **Installs automatically:**
- Docker Desktop
- Visual Studio Code
- Git (if needed)
- Required VS Code extensions

✅ **Configures automatically:**
- VS Code Dev Container extension
- Docker WSL2 backend check



## Usage after setup

1. **Restart computer** (for all components to load)
2. **Open project**: `code .`
3. **Start container**: `Ctrl+Shift+P` → "Dev Containers: Reopen in Container"  
4. **Launch Android Studio**: `studio` (in container terminal)

## Performance Tips

### Build Time
- **First build**: ~10-15 minutes (downloads Android Studio + SDK)
- **Subsequent builds**: ~2-3 minutes (cached layers)
- **Rebuild after config change**: ~5 minutes (partial rebuild)

### Speed up builds
- Keep Docker images: Don't run `docker system prune` unless necessary
- Use volume caches: Android SDK and Gradle cache persist between rebuilds
- Close other applications during build to free up system resources

```powershell
# Skip software installation (only setup autostart)
.\setup-android-dev.ps1 -SkipSoftware

# Skip autostart configuration
.\setup-android-dev.ps1 -SkipAutostart

# Verbose output
.\setup-android-dev.ps1 -Verbose
```

## Manual fallback

If the script fails, see the full [README.md](README.md) for manual installation steps.

---

**Requires:** Windows 10 (2004+) or Windows 11, Administrator privileges
