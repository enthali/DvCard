# Feature: Implementing Data Extraction Rules

## Problem Statement
The app currently has a TODO comment in `data_extraction_rules.xml` indicating that `<include>` and `<exclude>` tags should be used to control what data is backed up. Without proper configuration, the Android backup system may not correctly back up the app's database, or it might back up unnecessary files.

## Implementation Plan

### Step 1: Identify Database Details
- Determine the exact path and filename of the Room database
- Check BusinessCardDatabase.kt for the database name
- Verify the database location in the app's internal storage

### Step 2: Update Extraction Rules XML
- Modify data_extraction_rules.xml to use proper backup rules
- Add an `<include>` tag for the database file
- Add any necessary `<exclude>` tags for other files/directories
- Follow Android's best practices for backup rules

### Step 3: Test Backup and Restore
- Create test business cards in the app
- Trigger a backup (manually or via ADB)
- Clear app data or reinstall the app
- Restore from backup
- Verify all business cards are restored correctly

### Step 4: Document Changes
- Add comments to the XML file explaining the backup strategy
- Remove the TODO comment
- Update any relevant documentation

## Implementation Details

### XML Structure Example
```xml
<data-extraction-rules>
    <cloud-backup>
        <!-- Include the database file -->
        <include domain="database" path="business_card_database.db" />
        
        <!-- Exclude everything else -->
        <exclude domain="root" path="." />
        <exclude domain="file" path="." />
        <exclude domain="external" path="." />
        <exclude domain="sharedpref" path="." />
    </cloud-backup>
    <device-transfer>
        <!-- Same rules for device-to-device transfers -->
        <include domain="database" path="business_card_database.db" />
        <exclude domain="root" path="." />
        <exclude domain="file" path="." />
        <exclude domain="external" path="." />
        <exclude domain="sharedpref" path="." />
    </device-transfer>
</data-extraction-rules>
```

## Testing Strategy
- Create multiple business cards with different data
- Trigger a backup using ADB:
  ```
  adb shell bmgr backupnow de.drachenfels.dvcard
  ```
- Clear app data:
  ```
  adb shell pm clear de.drachenfels.dvcard
  ```
- Restore from backup:
  ```
  adb shell bmgr restore de.drachenfels.dvcard
  ```
- Verify all business cards are restored correctly

## Technical Considerations
- Ensure we're using the correct database path
- Test on different Android versions
- Consider the behavior if database schema changes in future updates

## Estimated Timeline
- Implementation: 1 hour
- Testing: 1-2 hours
- Total: 2-3 hours

## Status
- [ ] Step 1: Identify Database Details
- [ ] Step 2: Update Extraction Rules XML
- [ ] Step 3: Test Backup and Restore
- [ ] Step 4: Document Changes
