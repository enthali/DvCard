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
 * Main screen of the app with list of all business cards
 *
 * @param viewModel ViewModel for data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BusinessCardViewModel) {
    Log.d(LogConfig.TAG_UI, "MainScreen Composable is being executed")

    val cards by viewModel.cards.collectAsState()
    val qrCodeCard by viewModel.qrCodeDialogCard.collectAsState()
    val scope = rememberCoroutineScope()

    // State for the About dialog
    var showAboutDialog by remember { mutableStateOf(false) }

    Log.d(LogConfig.TAG_UI, "MainScreen State: cards=${cards.size}")

    Scaffold(
        topBar = {
            AppTopBar(
                onTitleClick = {
                    Log.d(LogConfig.TAG_UI, "App title clicked - Show About dialog")
                    showAboutDialog = true
                }
            )
        },
        floatingActionButton = {
            AddCardFab(
                onClick = {
                    Log.d(LogConfig.TAG_UI, "FAB clicked - Create new card")
                    scope.launch {
                        viewModel.createNewCard()
                    }
                }
            )
        }
    ) { paddingValues ->
        MainContent(
            cards = cards,
            paddingValues = paddingValues,
            onCreateCardClick = {
                Log.d(LogConfig.TAG_UI, "EmptyState button clicked - Create new card")
                scope.launch {
                    viewModel.createNewCard()
                }
            },
            onQrCodeClick = { card ->
                Log.d(LogConfig.TAG_UI, "QR code button clicked for card ${card.id}")
                viewModel.showQrCode(card)
            },
            onDeleteClick = { card ->
                Log.d(LogConfig.TAG_UI, "Delete button clicked for card ${card.id}")
                viewModel.deleteCard(card)
            },
            onCardChange = { updatedCard ->
                viewModel.saveCard(updatedCard) // Save to DB
                viewModel.updateCard(updatedCard) // Update UI
            }
        )

        // Show QR code dialog if a card is selected
        qrCodeCard?.let { card ->
            Log.d(LogConfig.TAG_UI, "Show QR code dialog for card ${card.id}")
            QrCodeDialog(
                card = card,
                onDismiss = {
                    Log.d(LogConfig.TAG_UI, "QR code dialog closed")
                    viewModel.dismissQrCode()
                }
            )
        }

        // Show About dialog if clicked on app title
        if (showAboutDialog) {
            Log.d(LogConfig.TAG_UI, "Show About dialog")
            AboutDialog(
                onDismiss = {
                    Log.d(LogConfig.TAG_UI, "About dialog closed")
                    showAboutDialog = false
                }
            )
        }
    }
}

/**
 * Top app bar with title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(onTitleClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_title),
                modifier = Modifier.clickable(onClick = onTitleClick)
            )
        }
    )
}

/**
 * Floating action button to add a new card
 */
@Composable
private fun AddCardFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_card)
        )
    }
}

/**
 * Main content of the screen (either empty state or card list)
 */
@Composable
private fun MainContent(
    cards: List<de.drachenfels.dvcard.data.model.BusinessCard>,
    paddingValues: PaddingValues,
    onCreateCardClick: () -> Unit,
    onQrCodeClick: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit,
    onDeleteClick: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit,
    onCardChange: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (cards.isEmpty()) {
            // Show empty state if no cards are available
            Log.d(LogConfig.TAG_UI, "No cards available, show EmptyState")
            EmptyState(
                onCreateClick = onCreateCardClick,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Show list of cards
            Log.d(LogConfig.TAG_UI, "Show card list with ${cards.size} cards")
            CardList(
                cards = cards,
                onQrCodeClick = onQrCodeClick,
                onDeleteClick = onDeleteClick,
                onCardChange = onCardChange
            )
        }
    }
}

/**
 * List of business cards
 */
@Composable
private fun CardList(
    cards: List<de.drachenfels.dvcard.data.model.BusinessCard>,
    onQrCodeClick: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit,
    onDeleteClick: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit,
    onCardChange: (de.drachenfels.dvcard.data.model.BusinessCard) -> Unit
) {
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
                onQrCodeClick = { onQrCodeClick(card) },
                onDeleteClick = { onDeleteClick(card) },
                onChange = onCardChange
            )
        }
    }
}

/**
 * Empty state when no cards are available
 */
@Composable
fun EmptyState(onCreateClick: () -> Unit, modifier: Modifier = Modifier) {
    Log.d(LogConfig.TAG_UI, "EmptyState Composable is being executed")
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

// Preview for the EmptyState
@Preview(showBackground = true, name = "Empty State")
@Composable
fun EmptyStatePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                EmptyState(
                    onCreateClick = { /* Dummy callback */ },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}