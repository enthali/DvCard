package de.drachenfels.dvcard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.components.CardItem
import de.drachenfels.dvcard.ui.components.QrCodeDialog
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModel

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
    val editMode by viewModel.cardEditMode.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    val qrCodeCard by viewModel.qrCodeDialogCard.collectAsState()
    
    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}, editMode=$editMode, selectedCard=${selectedCard?.id}")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DvCard - Digitale Visitenkarten") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                Log.d(LogConfig.TAG_UI, "FAB geklickt - Neue Karte erstellen")
                viewModel.createNewCard() 
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
            // Anzeige des Erstellungsdialogs, wenn der Modus "Creating" ist
            if (editMode is BusinessCardViewModel.CardEditState.Creating && selectedCard != null) {
                Log.d(LogConfig.TAG_UI, "Zeige Creating-Dialog für neue Karte")
                
                val scrollState = rememberScrollState()
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(0.9f) // Begrenzt die Höhe des Cards
                    ) {
                        Text(
                            text = "Neue Visitenkarte",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Box(modifier = Modifier.weight(1f)) {
                            de.drachenfels.dvcard.ui.components.CardEditView(
                                card = selectedCard!!,
                                onSaveClick = { card -> 
                                    Log.d(LogConfig.TAG_UI, "Save-Button für neue Karte geklickt")
                                    viewModel.saveCard(card) 
                                },
                                onDeleteClick = null,  // Keine Löschoption bei Neuanlage
                                onCancel = { 
                                    Log.d(LogConfig.TAG_UI, "Cancel-Button für neue Karte geklickt")
                                    viewModel.closeEdit() 
                                }
                            )
                        }
                    }
                }
            } else if (cards.isEmpty()) {
                // Anzeige, wenn keine Karten vorhanden sind und kein Erstellungsdialog aktiv ist
                Log.d(LogConfig.TAG_UI, "Keine Karten vorhanden, zeige EmptyState")
                EmptyState(
                    onCreateClick = { 
                        Log.d(LogConfig.TAG_UI, "EmptyState-Button geklickt - Neue Karte erstellen")
                        viewModel.createNewCard() 
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
                        val isExpanded = when (val mode = editMode) {
                            is BusinessCardViewModel.CardEditState.Editing -> 
                                mode.cardId == card.id
                            else -> false
                        }
                        
                        CardItem(
                            card = card,
                            isExpanded = isExpanded,
                            onEditClick = { 
                                Log.d(LogConfig.TAG_UI, "Edit-Button geklickt für Karte ${card.id}")
                                viewModel.editCard(card) 
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
                            },
                            onCancel = { 
                                Log.d(LogConfig.TAG_UI, "Cancel-Button geklickt")
                                viewModel.closeEdit() 
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