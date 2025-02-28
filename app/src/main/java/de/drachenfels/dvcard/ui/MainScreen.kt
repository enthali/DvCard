package de.drachenfels.dvcard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.components.AboutDialog
import de.drachenfels.dvcard.ui.components.CardItem
import de.drachenfels.dvcard.ui.components.QrCodeDialog
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
    Log.d(LogConfig.TAG_UI, "MainScreen composable is being executed")
    
    val cards by viewModel.cards.collectAsState()
    val qrCodeCard by viewModel.qrCodeDialogCard.collectAsState()
    val scope = rememberCoroutineScope()

    // State for the About dialog
    var showAboutDialog by remember { mutableStateOf(false) }
    
    Log.d(LogConfig.TAG_UI, "MainScreen state: cards=${cards.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        modifier = Modifier.clickable {
                            Log.d(LogConfig.TAG_UI, "App title clicked - Showing About dialog")
                            showAboutDialog = true
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d(LogConfig.TAG_UI, "FAB clicked - Creating new card")
                    scope.launch {
                        viewModel.createNewCard()
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_card)
                )
            }
        }
    ) { paddingValues ->
        // Card list
        CardList(
            cards = cards,
            paddingValues = paddingValues,
            onQrCodeClick = { card -> 
                Log.d(LogConfig.TAG_UI, "QR code button clicked for card ${card.id}")
                viewModel.showQrCode(card) 
            },
            onDeleteClick = { card -> 
                Log.d(LogConfig.TAG_UI, "Delete button clicked for card ${card.id}")
                viewModel.deleteCard(card) 
            },
            onCardChange = { updatedCard -> 
                Log.d(LogConfig.TAG_UI, "Card changed: ID=${updatedCard.id}, Name=${updatedCard.name}")
                viewModel.saveCard(updatedCard) 
            }
        )

        // Show QR code dialog if a card is selected
        qrCodeCard?.let { card ->
            Log.d(LogConfig.TAG_UI, "Showing QR code dialog for card ${card.id}")
            QrCodeDialog(
                card = card,
                onDismiss = { 
                    Log.d(LogConfig.TAG_UI, "QR code dialog dismissed")
                    viewModel.dismissQrCode() 
                }
            )
        }

        // Show About dialog if clicked on app title
        if (showAboutDialog) {
            Log.d(LogConfig.TAG_UI, "Showing About dialog")
            AboutDialog(
                onDismiss = { 
                    Log.d(LogConfig.TAG_UI, "About dialog dismissed")
                    showAboutDialog = false 
                }
            )
        }
    }
}

/**
 * List of business cards
 */
@Composable
private fun CardList(
    cards: List<BusinessCard>,
    paddingValues: PaddingValues,
    onQrCodeClick: (BusinessCard) -> Unit,
    onDeleteClick: (BusinessCard) -> Unit,
    onCardChange: (BusinessCard) -> Unit
) {
    Log.d(LogConfig.TAG_UI, "CardList composable with ${cards.size} cards")
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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