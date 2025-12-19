#!/bin/bash

# Android Emulator Start Script
# Start emulators with full snapshot control

# Colors for better UX
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}🚀 Android Emulator Starter${NC}"
echo "================================"

# Get available AVDs
AVD_LIST=($($ANDROID_HOME/emulator/emulator -list-avds))

if [ ${#AVD_LIST[@]} -eq 0 ]; then
    echo -e "${RED}❌ No AVDs found. Please create an AVD first.${NC}"
    exit 1
fi

# Display available AVDs
echo -e "${BLUE}📱 Available Emulators:${NC}"
for i in "${!AVD_LIST[@]}"; do
    echo "  $((i+1)). ${AVD_LIST[$i]}"
done

# Get user selection for AVD
while true; do
    echo
    read -p "Select emulator (1-${#AVD_LIST[@]}): " AVD_CHOICE
    if [[ "$AVD_CHOICE" =~ ^[0-9]+$ ]] && [ "$AVD_CHOICE" -ge 1 ] && [ "$AVD_CHOICE" -le ${#AVD_LIST[@]} ]; then
        break
    else
        echo -e "${RED}Invalid choice. Please enter a number between 1 and ${#AVD_LIST[@]}.${NC}"
    fi
done

SELECTED_AVD="${AVD_LIST[$((AVD_CHOICE-1))]}"
echo -e "${GREEN}✅ Selected: $SELECTED_AVD${NC}"

# Find AVD directory
AVD_DIR=$(find /root/.android/avd -name "*.avd" -type d | head -1)
SNAPSHOT_DIR="$AVD_DIR/snapshots"

echo
echo -e "${BLUE}📸 Available Snapshots:${NC}"
SNAPSHOT_LIST=()
if [ -d "$SNAPSHOT_DIR" ]; then
    for snapshot in "$SNAPSHOT_DIR"/*; do
        if [ -d "$snapshot" ]; then
            snapshot_name=$(basename "$snapshot")
            # Skip temporary/system snapshots
            if [[ "$snapshot_name" != "tmp"* ]] && [[ "$snapshot_name" != "."* ]]; then
                SNAPSHOT_LIST+=("$snapshot_name")
            fi
        fi
    done
fi

if [ ${#SNAPSHOT_LIST[@]} -eq 0 ]; then
    echo -e "${YELLOW}  No snapshots available - will cold boot${NC}"
    SNAPSHOT_LIST=()
else
    for i in "${!SNAPSHOT_LIST[@]}"; do
        echo "  $((i+1)). ${SNAPSHOT_LIST[$i]}"
    done
fi

# Add cold boot option
echo "  $((${#SNAPSHOT_LIST[@]}+1)). 🥶 Cold boot (fresh start)"

# Get user selection for snapshot
TOTAL_OPTIONS=$((${#SNAPSHOT_LIST[@]}+1))
while true; do
    echo
    read -p "Select boot option (1-${TOTAL_OPTIONS}): " SNAPSHOT_CHOICE
    if [[ "$SNAPSHOT_CHOICE" =~ ^[0-9]+$ ]] && [ "$SNAPSHOT_CHOICE" -ge 1 ] && [ "$SNAPSHOT_CHOICE" -le $TOTAL_OPTIONS ]; then
        break
    else
        echo -e "${RED}Invalid choice. Please enter a number between 1 and ${TOTAL_OPTIONS}.${NC}"
    fi
done

# Configure ADB for network access (Hyper-V/Sandbox)
echo
echo -e "${CYAN}🌐 Configuring ADB Network Access...${NC}"

# Get Hyper-V vEthernet IP (for Sandbox access)
HOST_IP=$(ip addr show | grep -oP '(?<=inet\s)172\.\d+\.\d+\.\d+' | head -1)

if [ -n "$HOST_IP" ]; then
    echo -e "${GREEN}  Host IP (Hyper-V): $HOST_IP${NC}"
    
    # Kill and restart ADB on specific interface
    adb kill-server 2>/dev/null
    sleep 1
    
    # Start ADB bound to Hyper-V interface
    ANDROID_ADB_SERVER_ADDRESS="$HOST_IP" ANDROID_ADB_SERVER_PORT="5037" adb start-server &
    sleep 2
    
    echo -e "${GREEN}✅ ADB accessible on ${HOST_IP}:5037${NC}"
else
    echo -e "${YELLOW}⚠️  Hyper-V interface not found - ADB on localhost only${NC}"
fi

# Start emulator based on choice
if [ "$SNAPSHOT_CHOICE" -eq $((${#SNAPSHOT_LIST[@]}+1)) ]; then
    # Cold boot
    echo -e "${YELLOW}⚡ Starting cold boot...${NC}"
    $ANDROID_HOME/emulator/emulator \
        -avd "$SELECTED_AVD" \
        -gpu swiftshader_indirect \
        -no-snapshot-save &
else
    # Use selected snapshot
    SELECTED_SNAPSHOT="${SNAPSHOT_LIST[$((SNAPSHOT_CHOICE-1))]}"
    echo -e "${GREEN}⚡ Starting with snapshot: $SELECTED_SNAPSHOT${NC}"
    
    $ANDROID_HOME/emulator/emulator \
        -avd "$SELECTED_AVD" \
        -snapshot "$SELECTED_SNAPSHOT" \
        -no-snapshot-save \
        -gpu swiftshader_indirect &
fi

# Wait for emulator to boot
echo -e "${CYAN}⏳ Waiting for emulator to boot...${NC}"
adb wait-for-device
sleep 5

# Enable TCP/IP for ADB (port 5555)
echo -e "${CYAN}🔌 Enabling ADB over TCP/IP...${NC}"
adb tcpip 5555
sleep 2

# Connect via TCP/IP
if [ -n "$HOST_IP" ]; then
    adb connect localhost:5555
    echo
    echo -e "${GREEN}✅ Emulator ready!${NC}"
    echo -e "${YELLOW}📱 Connect from Sandbox with:${NC}"
    echo -e "   ${WHITE}adb connect ${HOST_IP}:5555${NC}"
    echo
fi

# Wait for emulator to be closed
wait

echo -e "${GREEN}👋 Emulator stopped.${NC}"