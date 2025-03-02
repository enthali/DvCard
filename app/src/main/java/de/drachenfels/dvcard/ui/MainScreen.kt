package de.drachenfels.dvcard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.R
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

    // State für den About-Dialog
    var showAboutDialog by remember { mutableStateOf(false) }

    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
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
                }
            }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_card)
                )
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
                        CardItem(
                            card = card,
                            onQrCodeClick = {
                                Log.d(LogConfig.TAG_UI, "QR-Code-Button geklickt für Karte ${card.id}")
                                viewModel.showQrCode(card)
                            },
                            onDeleteClick = {
                                Log.d(LogConfig.TAG_UI, "Delete-Button geklickt für Karte ${card.id}")
                                viewModel.deleteCard(card)
                            },
                            onChange = { updatedCard ->
                                viewModel.saveCard(updatedCard) // Speichern in DB
                                viewModel.updateCard(updatedCard) // UI aktualisieren
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
            text = stringResource(R.string.empty_state_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.empty_state_description),
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
            Text(stringResource(R.string.create_card))
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