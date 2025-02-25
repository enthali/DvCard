package de.drachenfels.dvcard.ui

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
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.components.CardItem
import de.drachenfels.dvcard.ui.components.QrCodeDialog
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModel

/**
 * Hauptbildschirm der App mit Liste aller Visitenkarten
 *
 * @param viewModel ViewModel für die Datenverwaltung
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BusinessCardViewModel) {
    val cards by viewModel.cards.collectAsState()
    val editMode by viewModel.cardEditMode.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    val qrCodeCard by viewModel.qrCodeDialogCard.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DvCard - Digitale Visitenkarten") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.createNewCard() }) {
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
                EmptyState(
                    onCreateClick = { viewModel.createNewCard() },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Liste der Karten
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
                            onEditClick = { viewModel.editCard(card) },
                            onQrCodeClick = { viewModel.showQrCode(card) },
                            onSaveClick = { updatedCard -> viewModel.saveCard(updatedCard) },
                            onDeleteClick = { viewModel.deleteCard(card) },
                            onCancel = { viewModel.closeEdit() }
                        )
                    }
                    
                    // Anzeige des Erstellungsdialogs, wenn der Modus "Creating" ist
                    if (editMode is BusinessCardViewModel.CardEditState.Creating && selectedCard != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Neue Visitenkarte",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    
                                    de.drachenfels.dvcard.ui.components.CardEditView(
                                        card = selectedCard!!,
                                        onSaveClick = { card -> viewModel.saveCard(card) },
                                        onDeleteClick = null,  // Keine Löschoption bei Neuanlage
                                        onCancel = { viewModel.closeEdit() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // QR-Code Dialog anzeigen, wenn eine Karte ausgewählt ist
        if (qrCodeCard != null) {
            QrCodeDialog(
                card = qrCodeCard!!,
                onDismiss = { viewModel.dismissQrCode() }
            )
        }
    }
}

/**
 * Anzeige, wenn keine Karten vorhanden sind
 */
@Composable
fun EmptyState(onCreateClick: () -> Unit, modifier: Modifier = Modifier) {
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