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
                verticalAlignment = Alignment.Top // Oben ausrichten
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Name, Position, Firma (wie bisher)
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
                    
                    // Kontaktdaten hinzufügen
                    if (card.phone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tel: ${card.phone}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (card.email.isNotEmpty()) {
                        Text(
                            text = "E-Mail: ${card.email}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Adressdaten hinzufügen
                    if (card.street.isNotEmpty() || card.city.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (card.street.isNotEmpty()) {
                            Text(
                                text = card.street,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        val locationText = buildString {
                            if (card.postalCode.isNotEmpty()) {
                                append(card.postalCode)
                                append(" ")
                            }
                            if (card.city.isNotEmpty()) {
                                append(card.city)
                            }
                        }
                        
                        if (locationText.isNotEmpty()) {
                            Text(
                                text = locationText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        if (card.country.isNotEmpty()) {
                            Text(
                                text = card.country,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    // Zusätzliche Info für private Karten oder Ländercodes (wie bisher)
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
                
                // Aktions-Buttons (wie bisher)
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
            
            // Erweiterter Bearbeitungsbereich (wie bisher)
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