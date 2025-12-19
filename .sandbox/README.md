# Android Development Sandbox

Automatisiertes Setup für eine portable Android-Entwicklungsumgebung in Windows Sandbox.

## 🚀 Features

- **Vollautomatische Installation** von Android Studio, VS Code und Git
- **Persistente Downloads** - Installer werden gecacht und nur einmal heruntergeladen
- **Persistentes Android SDK** - SDK bleibt über Sandbox-Neustarts erhalten (via Symlink)
- **Parallele Installation** - Alle Tools installieren gleichzeitig
- **Persistente Git-Config** - Name & Email bleiben über Neustarts erhalten
- **Portable** - Funktioniert von jedem Verzeichnis aus
- **Template-basiert** - Sandbox-Config wird automatisch generiert

## 📦 Was wird installiert?

- **Android Studio** 2025.2.2.7 (Ladybug)
- **Visual Studio Code** (Latest Stable)
- **Git for Windows** (Latest)
- **Android SDK** (wird beim ersten Start von Android Studio heruntergeladen)

## 🎯 Verwendung

### Erste Verwendung

1. Repository in beliebiges Verzeichnis klonen/kopieren
2. **Optional:** Git-Konfiguration setzen:
   ```powershell
   .\.sandbox\edit-git-config.ps1
   ```
3. Sandbox starten:
   ```powershell
   .\.sandbox\host-scripts\start_sandbox.bat
   ```
   oder per PowerShell:
   ```powershell
   .\.sandbox\host-scripts\start_sandbox.ps1
   ```
4. Die Sandbox startet und installiert automatisch alle Tools
5. **Beim ersten Mal:** ~3.6 GB Downloads (Android Studio + SDK + Tools)
6. Danach: Downloads sind gecacht und SDK ist persistent!

### Folgende Starts

Einfach Doppelklick auf `.sandbox\host-scripts\start_sandbox.bat` oder:

```powershell
.\.sandbox\host-scripts\start_sandbox.ps1
```

Alle Downloads sind gecacht, nur noch die Installation läuft - **deutlich schneller!**

## 📁 Verzeichnisstruktur

```
.
├── .sandbox/                        # Komplettes Sandbox-Setup (wie .github, .vscode)
│   ├── host-scripts/                # Scripts die auf dem HOST laufen
│   │   ├── start_sandbox.bat        # Sandbox starten (Doppelklick)
│   │   ├── start_sandbox.ps1        # Sandbox starten (PowerShell)
│   │   ├── start_emulator.bat       # Emulator auf Host starten
│   │   └── start_emulator.ps1       # Emulator Launcher
│   ├── main.ps1                     # Haupt-Setup-Script (läuft in Sandbox)
│   │                                # Phase 1: Installation (parallel)
│   │                                # Phase 2: Konfiguration (sequenziell)
│   ├── edit-git-config.ps1          # Git-Config interaktiv bearbeiten (Host)
│   ├── sandbox.wsb                  # Generierte Sandbox-Konfiguration
│   ├── sandbox.wsb.template         # Template für Sandbox-Config
│   ├── app-installer/               # Installation & Configuration Scripts
│   │   ├── *.install.ps1            # Installation Scripts (Phase 1)
│   │   │   ├── git.install.ps1
│   │   │   ├── android-studio.install.ps1
│   │   │   ├── vscode.install.ps1
│   │   │   └── project-link.install.ps1
│   │   └── *.config.ps1             # Configuration Scripts (Phase 2)
│   │       └── git.config.ps1
│   ├── config/                      # Persistente Konfigurations-Daten (nur JSON)
│   │   ├── git-config.json          # Git User/Email (gitignored)
│   │   └── README.md                # Config-Dokumentation
│   ├── downloads/                   # Gecachte Installer (persistent)
│   ├── android-sdk/                 # Android SDK (persistent via Symlink)
│   ├── logs/                        # Installation Logs
│   └── README.md                    # Diese Datei
└── (deine Projektdateien)
```

## ⚙️ Konfiguration

Die Sandbox ist konfiguriert mit:

- **16 GB RAM**
- **8 CPU-Cores**
- **GPU-Beschleunigung** aktiviert
- **Networking** aktiviert

Anpassungen in [sandbox.wsb.template](.sandbox/sandbox.wsb.template) möglich.

## 🔧 Erweiterungen

### Neue App hinzufügen

1. **Installation Script** erstellen: `app-installer/neues-tool.install.ps1`
2. **Optional: Configuration Script** erstellen: `app-installer/neues-tool.config.ps1`

**Installation Template:**
```powershell
# Tool Installation
param($CacheDir, $DesktopPath)

$tool = @{
    Name = "Tool Name"
    File = "installer.exe"
    Url = "https://download.url/installer.exe"
    Args = "/S"  # Silent install args
}

$installerPath = Join-Path $CacheDir $tool.File

if (-not (Test-Path $installerPath)) {
    Write-Host "Downloading $($tool.Name)..."
    Invoke-WebRequest -Uri $tool.Url -OutFile $installerPath -UseBasicParsing
}

Write-Host "Installing $($tool.Name)..."
$process = Start-Process -FilePath $installerPath -ArgumentList $tool.Args -PassThru -Wait
if ($process.ExitCode -eq 0) {
    Write-Host "$($tool.Name) installed!" -ForegroundColor Green
}
```

**Configuration Template:**
```powershell
# Tool Configuration
param($DevPath = "C:\dev")

$configFile = Join-Path $DevPath ".sandbox\config\tool-config.json"

if (Test-Path $configFile) {
    Write-Host "Applying tool configuration..." -ForegroundColor Yellow
    $config = Get-Content $configFile | ConvertFrom-Json
    
    # Apply your config here
    
    Write-Host "  Tool configured!" -ForegroundColor Green
} else {
    Write-Host "Tool: No config found" -ForegroundColor Gray
}
```

Die Scripts werden automatisch erkannt und ausgeführt!

**Convention:**
- `*.install.ps1` → Phase 1 (parallel Installation)
- `*.config.ps1` → Phase 2 (sequenziell Konfiguration)

### RAM/CPU ändern

In [sandbox.wsb.template](.sandbox/sandbox.wsb.template):

```xml
<MemoryInMB>16384</MemoryInMB>  <!-- 16 GB -->
<vCPU>8</vCPU>                  <!-- 8 Cores -->
```

## � Persistente Konfigurationen

### Git Configuration

Die Git-Konfiguration (Name & Email) kann persistent gespeichert werden:

**Option 1: Interaktiver Editor (empfohlen)**
```powershell
.\.sandbox\edit-git-config.ps1
```

**Option 2: Manuelle JSON-Datei**
Erstelle `.sandbox\config\git-config.json`:
```json
{
  "name": "Dein Name",
  "email": "deine@email.com"
}
```

Die Git-Config wird automatisch beim Sandbox-Start angewendet!

### Weitere Configs hinzufügen

1. **Config-Datei** erstellen: `.sandbox\config\tool-config.json`
2. **Config-Script** erstellen: `.sandbox\app-installer\tool.config.ps1`

Die Konfigurationen werden automatisch in Phase 2 geladen.

## 💡 Hinweise

- Die Sandbox läuft als **Administrator** (Standard in Windows Sandbox)
- Alle Änderungen außerhalb der gemappten Ordner gehen beim Beenden verloren
- Das gemappte Verzeichnis (`C:\dev` in der Sandbox) entspricht dem Projekt-Ordner auf dem Host
- **Git-Konfiguration** ist jetzt persistent! (siehe oben)

## � Android Emulator (außerhalb Sandbox)

**Wichtig:** Der Android Emulator funktioniert **NICHT** innerhalb der Sandbox, da Windows Sandbox kein Nested Virtualization unterstützt.

**Lösung:** Emulator auf dem Host starten, nutzt das gleiche SDK!

### Emulator auf Host starten

1. **AVD in Android Studio erstellen** (einmalig, in der Sandbox):
   - Tools → Device Manager → Create Device
   - Z.B. "Pixel 8 Pro" mit aktuellem Android

2. **Emulator vom Host starten**:
   ```powershell
   .\.sandbox\host-scripts\start_emulator.bat
   ```

Der Emulator nutzt das persistente SDK aus `.sandbox/android-sdk/` und läuft mit voller Hardware-Beschleunigung auf dem Host! 🚀

**Workflow:**
- **Code-Development:** In der Sandbox (saubere Umgebung)
- **Testing:** Emulator auf Host oder echtes Android-Gerät via USB

## 🐛 Troubleshooting

### Sandbox startet nicht
- Windows Sandbox muss aktiviert sein: `Enable-WindowsOptionalFeature -Online -FeatureName Containers-DisposableClientVM -All`
- PowerShell als Administrator ausführen

### Downloads schlagen fehl
- Internetverbindung prüfen
- Firewall/Proxy-Einstellungen überprüfen

### Android SDK wird neu geladen
- Prüfen ob `android-sdk/` Ordner existiert und gefüllt ist
- Symlink-Status in der Sandbox überprüfen: `Get-Item $env:LOCALAPPDATA\Android\Sdk`

### Emulator findet SDK nicht
- Sandbox mindestens einmal gestartet haben (SDK Download)
- AVD in Android Studio erstellt haben

## 📝 Lizenz

Dieses Setup-Framework ist frei verwendbar. Die installierten Tools unterliegen ihren jeweiligen Lizenzen.
