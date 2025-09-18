#!/bin/bash

# Android Emulator Snapshot Save & Shutdown Script
# Save current emulator state to a named snapshot and optionally shutdown

# Colors for better UX
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}📸 Android Emulator Snapshot Saver${NC}"
echo "================================="

# Check if emulator is running
if ! adb devices | grep -q "emulator"; then
    echo -e "${RED}❌ No emulator is currently running${NC}"
    echo -e "${YELLOW}💡 Start an emulator first with: ./start_emulator.sh${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Emulator detected${NC}"

# Get snapshot name from user
echo
read -p "Enter name for new snapshot: " SNAPSHOT_NAME

if [ -z "$SNAPSHOT_NAME" ]; then
    echo -e "${RED}❌ Snapshot name cannot be empty${NC}"
    exit 1
fi

# Validate snapshot name (no spaces, special chars)
if [[ ! "$SNAPSHOT_NAME" =~ ^[a-zA-Z0-9_-]+$ ]]; then
    echo -e "${RED}❌ Snapshot name can only contain letters, numbers, underscore, and dash${NC}"
    exit 1
fi

echo
echo -e "${BLUE}📸 Saving current emulator state as: $SNAPSHOT_NAME${NC}"

# Save snapshot using ADB
if adb emu avd snapshot save "$SNAPSHOT_NAME"; then
    echo -e "${GREEN}✅ Snapshot '$SNAPSHOT_NAME' saved successfully!${NC}"
else
    echo -e "${RED}❌ Failed to save snapshot${NC}"
    exit 1
fi

# Ask if user wants to shutdown emulator
echo
read -p "Shutdown emulator now? (y/N): " SHUTDOWN_CHOICE

if [[ "$SHUTDOWN_CHOICE" =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}🔌 Shutting down emulator...${NC}"
    adb emu kill
    echo -e "${GREEN}👋 Emulator stopped${NC}"
else
    echo -e "${CYAN}💡 Emulator is still running. You can continue working.${NC}"
fi

echo
echo -e "${GREEN}📸 Snapshot '$SNAPSHOT_NAME' is ready for use!${NC}"
echo -e "${CYAN}💡 Use ./start_emulator.sh to boot from this snapshot next time${NC}"