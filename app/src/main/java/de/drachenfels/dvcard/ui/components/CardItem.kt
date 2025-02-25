package de.drachenfels.dvcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard

/**
 * Composable für ein einzelnes Visitenkarten-Item in der Liste
 * 
 * @param card Die anzuzeigende Visitenkarte
 * @param isExpanded Ob die Bearbeitungsansicht sichtbar sein soll
 * @param onEditClick Callback wenn der Bearbeiten-Button geklickt wird
 * @param onQrCodeClick Callback wenn der QR-Code-Button geklickt wird
 * @param onSaveClick Callback wenn die Karte gespeichert wird
 * @param onDeleteClick Callback wenn die Karte gelöscht wird
 * @param onCancel Callback wenn die Bearbeitung abgebrochen wird
 */
@Composable
fun CardItem(
    card: BusinessCard,
    isExpanded: Boolean,
    onEditClick: () -> Unit,
    onQrCodeClick: () -> Unit,
    onSaveClick: (BusinessCard) -> Unit,
    onDeleteClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Kompakte Kartenansicht
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (card.position.isNotEmpty()) {
                        Text(
                            text = card.position,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (card.company.isNotEmpty()) {
                        Text(
                            text = card.company,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // Zusätzliche Info für private Karten oder Ländercodes
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (card.isPrivate) {
                            Text(
                                text = "Privat",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        if (card.countryCode.isNotEmpty()) {
                            if (card.isPrivate) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "Region: ${card.countryCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                
                // Aktions-Buttons
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Karte bearbeiten"
                        )
                    }
                    IconButton(onClick = onQrCodeClick) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR-Code anzeigen"
                        )
                    }
                }
            }
            
            // Erweiterter Bearbeitungsbereich
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                CardEditView(
                    card = card,
                    onSaveClick = onSaveClick,
                    onDeleteClick = onDeleteClick,
                    onCancel = onCancel
                )
            }
        }
    }
}