# Contributing to DvCard

Thank you for your interest in contributing to DvCard! This document provides information about the project structure and development process.

## Development Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Kotlin 1.9.0 or newer
- Gradle 8.0 or newer

### Project Setup
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run

## Project Structure

```
de.drachenfels.dvcard/
├── data/
│   ├── BusinessCardDao.kt
│   ├── BusinessCardDatabase.kt
│   ├── BusinessCardRepository.kt
│   └── model/
│       └── BusinessCard.kt
├── ui/
│   ├── MainScreen.kt
│   ├── components/
│   │   ├── AboutDialog.kt
│   │   ├── CardEditView.kt
│   │   ├── CardItem.kt
│   │   └── QrCodeDialog.kt
│   └── theme/
├── util/
│   ├── QrCodeUtils.kt
│   ├── VCardGenerator.kt
│   └── logger/
│       └── Logger.kt
├── viewmodel/
│   └── BusinessCardViewModel.kt
└── MainActivity.kt
```

## App Architecture

The app follows a lightweight MVVM pattern:

### Components
- **BusinessCardDatabase**: Room database for persistent storage
- **BusinessCardDao**: Database access (CRUD operations)
- **BusinessCardRepository**: Central data source
- **BusinessCardViewModel**: Business logic and UI state management
- **MainScreen**: List view of all cards
- **CardItem**: UI component for a single card (compact and expanded)
- **QrCodeDialog**: Dialog for displaying the QR code
- **AboutDialog**: Dialog for app information, with Play Store QR code and GitHub link
- **VCardGenerator**: Helper functions for vCard and QR code generation
- **QrCodeUtils**: Helper functions for QR code generation and display

### User Interface
- Scrollable list of all saved business cards
- Each card in compact display with essential information
- Edit and QR code buttons directly on each card
- Expandable edit view within the list
- Floating Action Button (FAB) to add new cards

### Interactions
- Tap on Edit icon: Expands the card with input fields
- Tap on QR code icon: Opens a dialog with the QR code
- Save button: Saves changes to a card
- Delete button: Removes a card from the list

## Data Model
The BusinessCard model stores the following information:
- Name
- Position
- Company
- Phone number
- Email
- Website
- Flag for private/business card
- Country code (optional)

## Planned Optimizations (KISS Principle)

The following optimizations are planned for future development cycles:

### 1. CardEditView.kt - Simplify callback structure
- ✅ Changed parameter for `onDeleteClick` to nullable (`onDeleteClick: (() -> Unit)?`)

### 2. CardItem.kt - Reduce unnecessary nesting
- ✅ Reduced nesting of layouts
- ✅ Extracted UI components into smaller functions
- ✅ Example: Card-Header, Card-Content and Card-Footer as separate Composables
- ✅ Introduced i18n with strings in string.xml for English and German

### 3. BusinessCardViewModel.kt - Simplify redundant methods
- ✅ Merged `saveCard()` and `updateCard()` 
- ✅ Reduced log outputs for cleaner code
- ✅ Verified if local `updateCard` and repository update could be merged

### 4. MainScreen.kt - Simplification
- ✅ Eliminated code duplication when creating new cards
- ✅ Extracted QR code display and About dialog logic into separate Composables
- ✅ Simplified condition check for empty card list

### 5. VCardGenerator.kt - More robust implementation
- ✅ Added extended QR code functionality for URLs
- Ensure special characters in vCard generation are handled correctly
- Escape semicolons, commas, and other special characters according to vCard specification
- Better error handling for QR code generation

### 6. Data flow optimization
- Simplify data flows between ViewModel and UI
- Check if `StateFlow` is needed everywhere or if simpler solutions are sufficient
- Consistent strategy for UI updates after data changes

### 7. Logging optimization
- ✅ Introduction of a logger system with different output channels
- Introduction of log levels (DEBUG, INFO, ERROR) 
- Reduction of log outputs for production use
- Configurability of logging behavior depending on build type

### 8. Merge unnecessary preview functions
- ✅ Extracted common mock functions for previews (e.g., QrCodeUtils)
- Parameterized preview functions for different card scenarios

### 9. Redundant initialization in MainActivity
- Move initialization logic to separate initializer classes
- Apply lazy loading principles
- Check if Dagger/Hilt for dependency injection would be beneficial

### 10. Better error handling
- Unified strategy for error handling throughout the project
- User-friendly error messages for common error scenarios
- Implementation of recovery mechanisms for non-critical errors

## Code Style and Guidelines

We follow the standard Kotlin coding conventions and adhere to the KISS principle (Keep It Simple, Stupid). When contributing, please:

1. Write clean, readable, and well-documented code
2. Follow existing code style patterns
3. Add proper documentation for public functions
4. Write unit tests for new functionality
5. Keep changes focused and minimal

## Pull Request Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
By contributing to this project, you agree that your contributions will be licensed under the same [MIT License](LICENSE) that covers the project.
