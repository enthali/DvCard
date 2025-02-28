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
    
    // State für neu erstellte Karte, um sie als "isNewCard" zu markieren
    var newCardId by remember { mutableStateOf<Long?>(null) }
    
    // State für den About-Dialog
    var showAboutDialog by remember { mutableStateOf(false) }

    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}")

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
                    val id = viewModel.createNewCard()
                    // Speichern der ID der neuen Karte
                    newCardId = id
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
            if (cards.isEmpty()) {
                // Anzeige, wenn keine Karten vorhanden sind
                Log.d(LogConfig.TAG_UI, "Keine Karten vorhanden, zeige EmptyState")
                EmptyState(
                    onCreateClick = {
                        Log.d(LogConfig.TAG_UI, "EmptyState-Button geklickt - Neue Karte erstellen")
                        scope.launch {
                            val id = viewModel.createNewCard()
                            newCardId = id
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Liste der Karten
                Log.d(LogConfig.TAG_UI, "Zeige Kartenliste mit ${cards.size} Karten")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cards) { card ->
                        // Prüfen ob diese Karte neu ist (anhand der ID)
                        val isNewCard = card.id == newCardId
                        
                        CardItem(
                            card = card,
                            isNewCard = isNewCard,
                            onExpandClick = {
                                Log.d(LogConfig.TAG_UI, "Expand-Button geklickt für Karte ${card.id}")
                                // Nichts zu tun, CardItem verwaltet den Status selbst
                            },
                            onCollapseClick = { updatedCard ->
                                Log.d(LogConfig.TAG_UI, "Collapse-Button geklickt für Karte ${card.id}")
                                // Speichere die Änderungen
                                viewModel.saveCard(updatedCard)
                                
                                // Wenn diese Karte die neue Karte war, zurücksetzen
                                if (card.id == newCardId) {
                                    newCardId = null
                                }
                            },
                            onQrCodeClick = {
                                Log.d(LogConfig.TAG_UI, "QR-Code-Button geklickt für Karte ${card.id}")
                                viewModel.showQrCode(card)
                            },
                            onSaveClick = { updatedCard ->
                                Log.d(LogConfig.TAG_UI, "Save-Button geklickt für Karte ${card.id}")
                                viewModel.saveCard(updatedCard)
                            },
                            onDeleteClick = {
                                Log.d(LogConfig.TAG_UI, "Delete-Button geklickt für Karte ${card.id}")
                                viewModel.deleteCard(card)
                                
                                // Wenn diese Karte die neue Karte war, zurücksetzen
                                if (card.id == newCardId) {
                                    newCardId = null
                                }
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
