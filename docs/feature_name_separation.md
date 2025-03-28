# Feature: Separating Family Name and Given Name

## Problem Statement
Currently, the app stores a person's full name as a single field. When iOS devices import the vCard, they incorrectly place the entire name into the family name field. This causes poor user experience for iOS users scanning our QR codes.

## Implementation Plan

### Step 1: Analyze Current Implementation
- Examine BusinessCard.kt model to understand the current name storage
- Review CardEditView.kt to see how the name is currently displayed/edited
- Analyze VCardGenerator.kt to see how the name is formatted in vCard output
- Confirm current database schema by checking BusinessCardDao.kt and BusinessCardDatabase.kt

### Step 2: Update Database Entity and Add Migration
- Modify BusinessCard.kt entity to add separate givenName and familyName fields
- Keep the existing name field temporarily for backward compatibility
- Create a database migration strategy in BusinessCardDatabase.kt
- Update the database version number

### Step 3: Update Room DAO
- Update BusinessCardDao.kt to work with the new fields
- Ensure any queries that reference the name field are updated

### Step 4: Implement Migration Helper Utility
- Create a helper function to intelligently split existing name values into givenName and familyName
- Handle edge cases like single names or complex naming patterns
- Test this function with various name formats

### Step 5: Update Card Edit UI
- Modify CardEditView.kt to replace the single name field with separate givenName and familyName fields
- Ensure the UI handles validation and displays appropriate labels/hints
- Connect the new fields to the updated model

### Step 6: Update vCard Generation
- Modify VCardGenerator.kt to use the separate name fields in the vCard format
- Ensure proper formatting for maximum compatibility with iOS and other platforms
- Verify the format follows vCard standards for name components

### Step 7: Update List and Display Components
- Update CardItem.kt and any other components that display the name
- Ensure proper formatting for display purposes (combining the names as needed)

### Step 8: Implement Migration Process
- Ensure existing records are properly migrated when the app updates
- Add code to fill in the new fields from the existing name field
- Make sure this happens seamlessly for users

### Step 9: Update Business Logic in ViewModel
- Update BusinessCardViewModel.kt to handle the new field structure
- Ensure all operations (create, update, delete) work with the separated names

### Step 10: Testing Phase
- Create a test plan that covers various name formats and edge cases
- Test on different Android versions
- Test the migration process with existing data
- Verify vCard format works correctly with iOS and other platforms

### Step 11: Cleanup (After Successful Testing)
- Once everything is working, consider removing the legacy name field
- This would require another database migration
- Update all code that might still reference the old field

### Step 12: Documentation Update
- Update comments and documentation to reflect the new structure
- Add any necessary migration notes for future developers

## Testing Strategy
After each step, we will:
1. Compile the application to ensure there are no syntax or reference errors
2. Conduct a smoke test that includes:
   - Creating a new business card with separate names
   - Editing an existing card to verify migration
   - Generating a QR code and scanning it with an iOS device
   - Verifying the names appear correctly in contacts

## Technical Considerations
- Room database migrations are required
- VCard format standards must be followed
- Backward compatibility must be maintained during the transition
- UI must be intuitive for users to understand the separation of names

## Estimated Timeline
- Analysis: 1 day
- Implementation: 3-4 days
- Testing: 1-2 days
- Total: 5-7 days

## Status
- [ ] Step 1: Analyze Current Implementation
- [ ] Step 2: Update Database Entity and Add Migration
- [ ] Step 3: Update Room DAO
- [ ] Step 4: Implement Migration Helper
- [ ] Step 5: Update Card Edit UI
- [ ] Step 6: Update vCard Generation
- [ ] Step 7: Update List and Display Components
- [ ] Step 8: Implement Migration Process
- [ ] Step 9: Update Business Logic
- [ ] Step 10: Testing Phase
- [ ] Step 11: Cleanup
- [ ] Step 12: Documentation Update
