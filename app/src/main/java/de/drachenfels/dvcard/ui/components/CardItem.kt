package de.drachenfels.dvcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme

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
            .clickable(onClick = onQrCodeClick), // Ganze Karte klickbar für QR-Code
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Kartentyp (Privat/Geschäftlich) vor dem Titel
                    Text(
                        text = if (card.isPrivate) "Privat" else "Geschäftlich",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (card.isPrivate) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Kompakte Kartenansicht
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Titel oder Name, je nachdem was gesetzt ist
                            val displayTitle = if (card.title.isNotEmpty()) card.title else card.name
                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            // Wenn ein Titel gesetzt ist, zeigen wir auch den Namen an
                            if (card.title.isNotEmpty()) {
                                Text(
                                    text = card.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
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
                            
                            // Kontaktdaten
                            if (card.phone.isNotEmpty() || card.email.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            if (card.phone.isNotEmpty()) {
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
                            
                            // Adressdaten
                            if (card.street.isNotEmpty() || card.city.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                
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
                        }
                        
                        // Bearbeiten-Button (nur noch dieser oben rechts)
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Karte bearbeiten"
                            )
                        }
                    }
                }
                
                // QR-Code Icon unten rechts
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR-Code anzeigen",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Erweiterter Bearbeitungsbereich mit AnimatedVisibility
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                // Verwenden Sie ein extra Layout, um den CardEditView zu umschließen
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
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
}

// Beispieldaten für Previews
private val sampleCard = BusinessCard(
    id = 1,
    title = "Geschäftskarte",
    name = "Max Mustermann",
    position = "Software Developer",
    company = "Muster GmbH",
    phone = "+49 123 456789",
    email = "max@example.com",
    website = "www.example.com",
    street = "Musterstraße 123",
    postalCode = "12345",
    city = "Musterstadt",
    country = "Deutschland",
    isPrivate = false
)

private val privateCard = BusinessCard(
    id = 2,
    title = "Meine Privatkarte",
    name = "Erika Mustermann",
    position = "",
    company = "",
    phone = "+49 987 654321",
    email = "erika@example.com",
    website = "",
    street = "Privatweg 42",
    postalCode = "54321",
    city = "Beispielstadt",
    country = "Deutschland",
    isPrivate = true
)

private val minimalCard = BusinessCard(
    id = 3,
    title = "",
    name = "John Doe",
    phone = "+49 555 123456",
    email = "john@example.com"
)

@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = sampleCard,
                isExpanded = false,
                onEditClick = {},
                onQrCodeClick = {},
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Card ohne Titel")
@Composable
fun CardItemNoTitlePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = minimalCard,
                isExpanded = false,
                onEditClick = {},
                onQrCodeClick = {},
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Expanded Card")
@Composable
fun ExpandedCardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = sampleCard,
                isExpanded = true,
                onEditClick = {},
                onQrCodeClick = {},
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Private Card")
@Composable
fun PrivateCardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = privateCard,
                isExpanded = false,
                onEditClick = {},
                onQrCodeClick = {},
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}