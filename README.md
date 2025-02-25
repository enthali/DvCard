# DvCard - Digitale Visitenkarten als QR-Codes

## Überblick
DvCard (Digital vCard) ist eine Android-App zur Verwaltung mehrerer digitaler Visitenkarten. Jede Karte stellt persönliche Kontaktdaten dar und generiert einen QR-Code im vCard-Format, sodass andere Personen durch einfaches Scannen diese Daten in ihre Kontakte übernehmen können.

## Funktionen
- Verwaltung mehrerer digitaler Visitenkarten (privat und geschäftlich)
- Ansprechende Darstellung von Kontaktdaten als Visitenkarte
- Automatische Generierung eines vCard-QR-Codes für jede Karte
- Einfache Bearbeitung, Erstellung und Löschung von Kontaktdaten
- Persistente Speicherung der Karten auf dem Gerät

## App-Struktur
### Hauptansicht
- Scrollbare Liste aller gespeicherten Visitenkarten
- Jede Karte in kompakter Darstellung mit dem wichtigsten Informationen
- Bearbeitungs- und QR-Code-Buttons direkt auf jeder Karte
- Expandierbare Bearbeitungsansicht innerhalb der Liste
- Floating Action Button (FAB) zum Hinzufügen neuer Karten

### Interaktionen
- Tippen auf Bearbeitungs-Symbol: Erweitert die Karte mit Eingabefeldern
- Tippen auf QR-Code-Symbol: Öffnet einen Dialog mit dem QR-Code
- Save-Button: Speichert Änderungen an einer Karte
- Delete-Button: Entfernt eine Karte aus der Liste

## Technische Details
### Technologie-Stack
- Kotlin als Programmiersprache
- Jetpack Compose für das UI-Framework
- Room-Datenbank für persistente Speicherung
- ZXing für die QR-Code-Generierung
- Kotlin DSL für das Build-System (build.gradle.kts)

### Datenmodell
Das BusinessCard-Modell speichert folgende Informationen:
- Name
- Position
- Firma
- Telefonnummer
- E-Mail
- Website
- Flag für private/geschäftliche Karte
- Ländercode (optional)

### Architektur
Die App folgt einem leichtgewichtigen MVVM-Muster:
- **Model**: Room-Datenbank mit Entity und DAO
- **ViewModel**: Verwaltung der Daten und UI-Zustände
- **View**: Compose-UI mit reaktiver Aktualisierung

### Komponenten
- **BusinessCardDatabase**: Room-Datenbank für die persistente Speicherung
- **BusinessCardDao**: Datenbankzugriff (CRUD-Operationen)
- **BusinessCardRepository**: Zentrale Datenquelle
- **BusinessCardViewModel**: Geschäftslogik und UI-State-Management
- **MainScreen**: Listendarstellung aller Karten
- **CardItem**: UI-Komponente für eine einzelne Karte (kompakt und expandiert)
- **QrCodeDialog**: Dialog für die Anzeige des QR-Codes
- **VCardGenerator**: Hilfsfunktionen zur Generierung von vCard und QR-Codes

## Paket-Struktur
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

## Einrichtung und Entwicklung
### Voraussetzungen
- Android Studio Hedgehog (2023.1.1) oder neuer
- Kotlin 1.9.0 oder neuer
- Gradle 8.0 oder neuer

### Projekteinrichtung
1. Klone das Repository
2. Öffne das Projekt in Android Studio
3. Sync Gradle files
4. Build und Run

## Lizenz
[MIT License](LICENSE)
