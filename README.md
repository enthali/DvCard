# DvCard - Digital Business Cards as QR Codes

## Overview

DvCard (Digital vCard) is an Android app for managing multiple digital business cards. Each card represents personal contact information and generates a QR code in vCard format, allowing others to easily transfer this data into their contacts by simply scanning the code.

## Features

* Management of multiple digital business cards (personal and professional)
* Attractive display of contact information as a business card
* Automatic generation of a vCard QR code for each card
* Easy editing, creation, and deletion of contact information
* Persistent storage of cards on the device

## App Structure

### Main View

* Scrollable list of all saved business cards
* Each card in a compact display with the most important information
* Edit and QR code buttons directly on each card
* Expandable edit view within the list
* Floating Action Button (FAB) to add new cards

### Interactions

* Tap the edit icon: Expands the card with input fields
* Tap the QR code icon: Opens a dialog with the QR code
* Save button: Saves changes to a card
* Delete button: Removes a card from the list

## Technical Details

### Technology Stack

* Kotlin as the programming language
* Jetpack Compose for the UI framework
* Room database for persistent storage
* ZXing for QR code generation
* Kotlin DSL for the build system (build.gradle.kts)

### Data Model

The BusinessCard model stores the following information:

* Name
* Position
* Company
* Phone number
* Email
* Website
* Flag for personal/professional card
* Country code (optional)

### Architecture

The app follows a lightweight MVVM pattern:

* **Model**: Room database with Entity and DAO
* **ViewModel**: Management of data and UI states
* **View**: Compose UI with reactive updating

### Components

* **BusinessCardDatabase**: Room database for persistent storage
* **BusinessCardDao**: Database access (CRUD operations)
* **BusinessCardRepository**: Central data source
* **BusinessCardViewModel**: Business logic and UI state management
* **MainScreen**: List display of all cards
* **CardItem**: UI component for a single card (compact and expanded)
* **QrCodeDialog**: Dialog for displaying the QR code
* **VCardGenerator**: Helper functions for generating vCard and QR codes

## Package Structure
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
│   ├── CardItem.kt
│   ├── CardEditView.kt
│   ├── QrCodeDialog.kt
│   └── theme/
├── util/
│   └── VCardGenerator.kt
├── viewmodel/
│   └── BusinessCardViewModel.kt
└── MainActivity.kt
```

## Setup and Development

### Prerequisites

* Android Studio Hedgehog (2023.1.1) or newer
* Kotlin 1.9.0 or newer
* Gradle 8.0 or newer

### Project Setup

1.  Clone the repository
2.  Open the project in Android Studio
3.  Sync Gradle files
4.  Build and Run

## Planned Code Optimizations 

The following optimizations are planned for future development cycles:

### 1. CardEditView.kt - Simplify Callback Structure

* ✅ Changed parameter for `onDeleteClick` to nullable (`onDeleteClick: (() -> Unit)?`)

### 2. CardItem.kt - Reduce Unnecessary Nesting

* ✅ Reduce nesting of layouts
* ✅ Extract UI components into smaller functions
* ✅ Example: Card Header, Card Content, and Card Footer as separate Composables
* ✅ Implement i18n of strings in the string.xml for english and german.

### 3. BusinessCardViewModel.kt - Simplify Redundant Methods

* ✅ Merge `saveCard()` and `updateCard()`
* ✅ Reduce log outputs for cleaner code
* ✅ Check if local `updateCard` and Repository update can be merged

### 4. MainScreen.kt - Simplification

* ✅ Eliminate code duplication when creating new cards
* ✅ Extract QR code display and About dialog logic into separate Composables
* ✅ Simplify conditional check for empty card list

### 5. VCardGenerator.kt - More Robust Implementation

* Ensure special characters are handled correctly in vCard generation
* Escaping of semicolons, commas, and other special characters according to vCard specifications
* Better error handling for QR code generation

### 6. Data Flow Optimization

* Simplify data flows between ViewModel and UI
* Check if `StateFlow` is necessary everywhere or if simpler solutions suffice
* Consistent strategy for UI updates after data changes

### 7. Logging Optimization

* Introduce log levels (DEBUG, INFO, ERROR)
* Reduce log outputs for production use
* Configurability of logging behavior depending on build type

### 8. Merge Unnecessary Preview Functions

* Extract common code from preview functions
* Parameterized preview functions for different card scenarios

### 9. Redundant Initialization in MainActivity

* Move initialization logic into separate initializer classes
* Application of lazy-loading principles
* Check if Dagger/Hilt would be useful for dependency injection

### 10. Better Error Handling

* Uniform error handling strategy throughout the project
* User-friendly error messages for common error scenarios
* Implementation of recovery mechanisms for non-critical errors

## Revision History
| Version | Name                                       | Description                                                                                   |
| --- |--------------------------------------------|-----------------------------------------------------------------------------------------------|
| 2 | what a new look! | significant changes to UI handling and i18n preperation, geerman and english language support |
| 1 | that's what we call an MVP | initial release MVP internal and extended test                                                |
## License

[MIT License](LICENSE)