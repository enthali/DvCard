package de.drachenfels.dvcard.ui

import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.flow.StateFlow

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

    // State für den About-Dialog
    var showAboutDialog by remember { mutableStateOf(false) }

    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}, editMode=$editMode, selectedCard=${selectedCard?.id}")

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
            if (editMode !is BusinessCardViewModel.CardEditState.Creating) {
                FloatingActionButton(onClick = {
                    Log.d(LogConfig.TAG_UI, "FAB geklickt - Neue Karte erstellen")
                    viewModel.createNewCard()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Neue Karte hinzufügen")
                }
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Neue Visitenkarte",
                                style = MaterialTheme.typography.titleLarge
                            )

                            de.drachenfels.dvcard.ui.components.CardEditView(
                                card = selectedCard!!,
                                onSaveClick = { card ->
                                    Log.d(LogConfig.TAG_UI, "Save-Button für neue Karte geklickt")
                                    viewModel.saveCard(card)
                                },
                                onDeleteClick = null,  // Keine Löschoption bei Neuanlage
                                onCancel = null  // Kein Cancel-Button mehr
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
                            onExpandClick = {
                                Log.d(LogConfig.TAG_UI, "Expand-Button geklickt für Karte ${card.id}")
                                viewModel.editCard(card)
                            },
                            onCollapseClick = { updatedCard ->
                                Log.d(LogConfig.TAG_UI, "Collapse-Button geklickt für Karte ${card.id}")
                                viewModel.saveCard(updatedCard)
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

// Previews mit UI-Zuständen statt ViewModels

@Composable
fun MainScreenPreviewContent(
    cards: List<BusinessCard>,
    editingCardId: Long? = null,
    isCreatingCard: Boolean = false,
    selectedCard: BusinessCard? = null,
    qrCodeCard: BusinessCard? = null
) {
    // UI-Zustandssimulation für Previews
    val previewEditMode = when {
        isCreatingCard -> BusinessCardViewModel.CardEditState.Creating
        editingCardId != null -> BusinessCardViewModel.CardEditState.Editing(editingCardId)
        else -> BusinessCardViewModel.CardEditState.Closed
    }

    val cardsState = remember { mutableStateOf(cards) }
    val editModeState = remember { mutableStateOf(previewEditMode) }
    val selectedCardState = remember { mutableStateOf(selectedCard) }
    val qrCodeCardState = remember { mutableStateOf(qrCodeCard) }

    // Mock des ViewModels für die Preview
    val previewViewModel = remember {
        object {
            val cards = object {
                fun collectAsState() = cardsState
            }
            val cardEditMode = object {
                fun collectAsState() = editModeState
            }
            val selectedCard = object {
                fun collectAsState() = selectedCardState
            }
            val qrCodeDialogCard = object {
                fun collectAsState() = qrCodeCardState
            }

            fun createNewCard() {
                selectedCardState.value = BusinessCard()
                editModeState.value = BusinessCardViewModel.CardEditState.Creating
            }

            fun editCard(card: BusinessCard) {
                selectedCardState.value = card
                editModeState.value = BusinessCardViewModel.CardEditState.Editing(card.id)
            }

            fun closeEdit() {
                editModeState.value = BusinessCardViewModel.CardEditState.Closed
                selectedCardState.value = null
            }

            fun saveCard(card: BusinessCard) {
                // Simuliere Kartenaktualisierung
                val updatedCards = cardsState.value.toMutableList()
                val index = updatedCards.indexOfFirst { it.id == card.id }
                if (index >= 0) {
                    updatedCards[index] = card
                } else {
                    updatedCards.add(card.copy(id = (updatedCards.maxOfOrNull { it.id } ?: 0) + 1))
                }
                cardsState.value = updatedCards
                closeEdit()
            }

            fun deleteCard(card: BusinessCard) {
                val updatedCards = cardsState.value.filter { it.id != card.id }
                cardsState.value = updatedCards
                closeEdit()
            }

            fun showQrCode(card: BusinessCard) {
                qrCodeCardState.value = card
            }

            fun dismissQrCode() {
                qrCodeCardState.value = null
            }
        }
    }

    // Nutze die UI-Komponente mit dem simulierten ViewModel
    MainScreen(viewModel = previewViewModel as BusinessCardViewModel)
}

@Preview(showBackground = true, name = "Kartenliste")
@Composable
fun MainScreenPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Warnhinweis für Preview-Zwecke
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "MainScreen Preview",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Preview nicht verfügbar, da ViewModel nicht erweiterbar ist",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Bitte in der Anwendung testen",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Separate Preview für den EmptyState
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