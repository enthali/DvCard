package de.drachenfels.dvcard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.components.AboutDialog
import de.drachenfels.dvcard.ui.components.CardItem
import de.drachenfels.dvcard.ui.components.QrCodeDialog
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModel
import kotlinx.coroutines.launch

/**
 * Hauptbildschirm der App mit Liste aller Visitenkarten
 *
 * @param viewModel ViewModel für die Datenverwaltung
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BusinessCardViewModel) {
    Log.d(LogConfig.TAG_UI, "MainScreen Composable wird ausgeführt")

    val cards by viewModel.cards.collectAsState()
    val qrCodeCard by viewModel.qrCodeDialogCard.collectAsState()
    val scope = rememberCoroutineScope()
    
    // State für lokale UI-Karten mit isExpanded-Status
    var uiCards by remember { mutableStateOf(listOf<BusinessCard>()) }
    
    // Aktualisiere uiCards wenn sich die DB-Karten ändern
    LaunchedEffect(cards) {
        // Behalte isExpanded-Status bei, falls vorhanden
        uiCards = cards.map { dbCard ->
            val existingCard = uiCards.find { it.id == dbCard.id }
            if (existingCard != null) {
                dbCard.withExpanded(existingCard.isExpanded)
            } else {
                dbCard
            }
        }
    }

    // State für den About-Dialog
    var showAboutDialog by remember { mutableStateOf(false) }

    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}, uiCards=${uiCards.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Digitale Visitenkarte",
                        modifier = Modifier.clickable {
                            Log.d(LogConfig.TAG_UI, "App-Titel geklickt - Zeige About-Dialog")
                            showAboutDialog = true
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Log.d(LogConfig.TAG_UI, "FAB geklickt - Neue Karte erstellen")
                
                scope.launch {
                    val newId = viewModel.createNewCard()
                    // Wenn neue Karte erstellt wurde, in UI-Zustand aktualisieren
                    if (newId != null) {
                        // Warten bis die Karte in der Datenbank ist
                        val newCard = viewModel.getCardById(newId)
                        if (newCard != null) {
                            // Neue Karte in UI-Karten hinzufügen und expandieren
                            uiCards = uiCards.map { it.withExpanded(false) } + newCard.withExpanded(true)
                        }
                    }
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Neue Karte hinzufügen")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiCards.isEmpty()) {
                // Anzeige, wenn keine Karten vorhanden sind
                Log.d(LogConfig.TAG_UI, "Keine Karten vorhanden, zeige EmptyState")
                EmptyState(
                    onCreateClick = {
                        Log.d(LogConfig.TAG_UI, "EmptyState-Button geklickt - Neue Karte erstellen")
                        scope.launch {
                            val newId = viewModel.createNewCard()
                            if (newId != null) {
                                // Warten bis die Karte in der Datenbank ist
                                val newCard = viewModel.getCardById(newId)
                                if (newCard != null) {
                                    // Neue Karte in UI-Karten hinzufügen und expandieren
                                    uiCards = listOf(newCard.withExpanded(true))
                                }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Liste der Karten
                Log.d(LogConfig.TAG_UI, "Zeige Kartenliste mit ${uiCards.size} Karten")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiCards) { uiCard ->
                        CardItem(
                            card = uiCard,
                            onExpandClick = {
                                Log.d(LogConfig.TAG_UI, "Expand-Button geklickt für Karte ${uiCard.id}")
                                // Nur diese Karte öffnen, alle anderen schließen
                                uiCards = uiCards.map { card ->
                                    if (card.id == uiCard.id) card.withExpanded(true)
                                    else card.withExpanded(false)
                                }
                            },
                            onCollapseClick = { updatedCard ->
                                Log.d(LogConfig.TAG_UI, "Collapse-Button geklickt für Karte ${uiCard.id}")
                                // Speichere die Änderungen und schließe die Karte
                                viewModel.saveCard(updatedCard)
                                // Aktualisiere UI-Zustand
                                uiCards = uiCards.map { card ->
                                    if (card.id == updatedCard.id) updatedCard.withExpanded(false)
                                    else card
                                }
                            },
                            onQrCodeClick = {
                                Log.d(LogConfig.TAG_UI, "QR-Code-Button geklickt für Karte ${uiCard.id}")
                                viewModel.showQrCode(uiCard)
                            },
                            onSaveClick = { updatedCard ->
                                Log.d(LogConfig.TAG_UI, "Save-Button geklickt für Karte ${uiCard.id}")
                                viewModel.saveCard(updatedCard)
                                // UI-Zustand aktualisieren, expanded-Status beibehalten
                                uiCards = uiCards.map { card ->
                                    if (card.id == updatedCard.id) updatedCard.withExpanded(true)
                                    else card
                                }
                            },
                            onDeleteClick = {
                                Log.d(LogConfig.TAG_UI, "Delete-Button geklickt für Karte ${uiCard.id}")
                                viewModel.deleteCard(uiCard)
                                // Karte aus UI-Zustand entfernen
                                uiCards = uiCards.filter { it.id != uiCard.id }
                            }
                        )
                    }
                }
            }
        }

        // QR-Code Dialog anzeigen, wenn eine Karte ausgewählt ist
        if (qrCodeCard != null) {
            Log.d(LogConfig.TAG_UI, "Zeige QR-Code-Dialog für Karte ${qrCodeCard?.id}")
            QrCodeDialog(
                card = qrCodeCard!!,
                onDismiss = {
                    Log.d(LogConfig.TAG_UI, "QR-Code-Dialog geschlossen")
                    viewModel.dismissQrCode()
                }
            )
        }

        // About-Dialog anzeigen, wenn auf den App-Titel geklickt wurde
        if (showAboutDialog) {
            Log.d(LogConfig.TAG_UI, "Zeige About-Dialog")
            AboutDialog(
                onDismiss = {
                    Log.d(LogConfig.TAG_UI, "About-Dialog geschlossen")
                    showAboutDialog = false
                }
            )
        }
    }
}

/**
 * Anzeige, wenn keine Karten vorhanden sind
 */
@Composable
fun EmptyState(onCreateClick: () -> Unit, modifier: Modifier = Modifier) {
    Log.d(LogConfig.TAG_UI, "EmptyState Composable wird ausgeführt")
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Keine Visitenkarten vorhanden",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Erstellen Sie Ihre erste digitale Visitenkarte, um einen QR-Code zu generieren.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onCreateClick) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Visitenkarte erstellen")
        }
    }
}

// Preview für den EmptyState
@Preview(showBackground = true, name = "Leerer Zustand")
@Composable
fun EmptyStatePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                EmptyState(
                    onCreateClick = { /* Dummy-Callback */ },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
