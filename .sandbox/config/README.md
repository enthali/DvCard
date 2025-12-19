# Config Directory

Dieses Verzeichnis enthält **nur Konfigurations-Daten** (JSON-Dateien).

Die Konfigurations-**Scripts** befinden sich in `app-installer/*.config.ps1`.

## Vorhandene Konfigurationen

### Git Configuration
- **Datei:** `git-config.json`
- **Script:** `../app-installer/git.config.ps1`
- **Format:**
  ```json
  {
    "name": "Dein Name",
    "email": "deine@email.com"
  }
  ```

## Neue Konfiguration hinzufügen

1. **Config-Daten:** JSON-Datei hier ablegen (z.B. `vscode-config.json`)
2. **Config-Script:** `*.config.ps1` in `app-installer/` erstellen
3. Das Script wird automatisch in Phase 2 des Setups ausgeführt

## Workflow

```
main.ps1
  ├── Phase 1: Installation (parallel)
  │   └── app-installer/*.install.ps1
  │
  └── Phase 2: Configuration (sequenziell)
      └── app-installer/*.config.ps1
          └── lesen: config/*.json
```
