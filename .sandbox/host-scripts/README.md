# Host Scripts - Android Emulator with Sandbox Support

Scripts to run the Android Emulator on the host and connect from Windows Sandbox.

## Setup (One-time, requires Administrator)

**Run as Administrator:**

```bat
setup-adb-forwarding.bat
```

This configures:

- Port forwarding: `172.20.240.1:5555` → `localhost:5555`
- Firewall rule: Allow port 5555 from local subnet

## Start Emulator

**Run normally (no admin needed):**

```bat
start_emulator.bat
```

This will:

1. Check if port forwarding is configured
2. List available AVDs and let you select one
3. Start ADB server
4. Start the emulator
5. Enable ADB over TCP/IP (port 5555)

## Connect from Sandbox

Inside the Windows Sandbox:

```powershell
adb connect 172.20.240.1:5555
adb devices
```

## Files

- **setup-adb-forwarding.bat/ps1** - One-time setup (requires admin)
- **start_emulator.bat/ps1** - Start emulator (normal user)
- **start_sandbox.bat** - Start Windows Sandbox

## Troubleshooting

**"Port forwarding not configured" error:**

- Run `setup-adb-forwarding.bat` as Administrator

**Timeout when connecting from Sandbox:**

- Check firewall: `Get-NetFirewallRule -DisplayName "Android Emulator - ADB TCP/IP"`
- Check port forwarding: `netsh interface portproxy show v4tov4`

**Wrong Host IP:**

- Check Hyper-V IP: `Get-NetIPAddress | Where-Object { $_.InterfaceAlias -like "*vEthernet*" }`
