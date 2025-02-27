package de.drachenfels.dvcard.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.generateVCardQrCode

/**
 * Dialog zur Anzeige des QR-Codes für eine Visitenkarte
 * 
 * @param card Die Visitenkarte, für die der QR-Code angezeigt werden soll
 * @param onDismiss Callback, wenn der Dialog geschlossen wird
 */
@Composable
fun QrCodeDialog(card: BusinessCard, onDismiss: () -> Unit) {
    // Bestimme den Anzeigetitel (verwende Titel wenn vorhanden, sonst den Namen)
    val displayTitle = if (card.title.isNotEmpty()) card.title else card.name
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                // Gesamter Dialog ist klickbar, um zu schließen
                .clickable { onDismiss() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titel mit verbesserter Textdarstellung
                Text(
                    text = "Digitale Visitenkarte",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                
                // QR-Code-Bild generieren und anzeigen
                val isPreview = LocalInspectionMode.current
                val qrBitmap = if (isPreview) {
                    // Mock-Bitmap für Preview
                    createMockQrBitmap(256)
                } else {
                    // Echten QR-Code für die App
                    generateVCardQrCode(card)
                }
                
                // Box mit einem dezenten Schatten für den QR-Code
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(260.dp),
                    shadowElevation = 2.dp,
                    tonalElevation = 1.dp,
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR-Code für ${displayTitle}",
                        modifier = Modifier.size(256.dp).padding(2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Hinweistext zum QR-Code scannen
                Text(
                    text = "Scannen Sie diesen Code, um die Kontaktdaten zu importieren.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                // Neuer Hinweistext zum Schließen
                Text(
                    text = "Tippen zum Schließen",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

/**
 * Erstellt ein Mock-QR-Code-Bitmap für Previews
 */
private fun createMockQrBitmap(size: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    
    // Einfaches QR-Code-Muster erstellen
    for (x in 0 until size) {
        for (y in 0 until size) {
            // Rahmen
            val isEdge = x < size/10 || y < size/10 || x >= size*9/10 || y >= size*9/10
            
            // Ausrichtungsquadrate (oben links, oben rechts, unten links)
            val isTopLeftSquare = x < size/4 && y < size/4
            val isTopRightSquare = x >= size*3/4 && y < size/4
            val isBottomLeftSquare = x < size/4 && y >= size*3/4
            
            // Innere Quadrate
            val isInnerSquare = (x > size/6 && x < size/3 && y > size/6 && y < size/3) ||
                               (x > size*2/3 && x < size*5/6 && y > size/6 && y < size/3) ||
                               (x > size/6 && x < size/3 && y > size*2/3 && y < size*5/6)
            
            // Musterelemente
            val isPattern = ((x + y) % 8 < 4) && x > size/3 && x < size*2/3 && y > size/3 && y < size*2/3
            
            bitmap.setPixel(
                x, y, 
                when {
                    isEdge || isTopLeftSquare || isTopRightSquare || isBottomLeftSquare -> Color.BLACK
                    isInnerSquare -> Color.WHITE
                    isPattern -> Color.BLACK
                    else -> Color.WHITE
                }
            )
        }
    }
    
    return bitmap
}

// Beispieldaten für Preview
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

private val sampleCardNoTitle = BusinessCard(
    id = 2,
    title = "",
    name = "Max Mustermann",
    position = "Software Developer",
    company = "Muster GmbH",
    phone = "+49 123 456789",
    email = "max@example.com"
)

@Preview(showBackground = true)
@Composable
fun QrCodeDialogPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Da der Dialog als Overlay dargestellt wird und wir keinen Dialog in einem Preview haben können,
            // stellen wir den Inhalt des Dialogs direkt dar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Titel mit verbesserter Textdarstellung
                    Text(
                        text = "Digitale Visitenkarte",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = if (sampleCard.title.isNotEmpty()) sampleCard.title else sampleCard.name,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    
                    // Box mit einem dezenten Schatten für den QR-Code
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(260.dp),
                        shadowElevation = 2.dp,
                        tonalElevation = 1.dp,
                    ) {
                        Image(
                            bitmap = createMockQrBitmap(256).asImageBitmap(),
                            contentDescription = "QR-Code für ${sampleCard.name}",
                            modifier = Modifier.size(256.dp).padding(2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Scannen Sie diesen Code, um die Kontaktdaten zu importieren.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    // Neuer Hinweistext zum Schließen
                    Text(
                        text = "Tippen zum Schließen",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "QR Code ohne Titel")
@Composable
fun QrCodeDialogNoTitlePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Digitale Visitenkarte",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = sampleCardNoTitle.name,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(260.dp),
                        shadowElevation = 2.dp,
                        tonalElevation = 1.dp,
                    ) {
                        Image(
                            bitmap = createMockQrBitmap(256).asImageBitmap(),
                            contentDescription = "QR-Code für ${sampleCardNoTitle.name}",
                            modifier = Modifier.size(256.dp).padding(2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Scannen Sie diesen Code, um die Kontaktdaten zu importieren.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    // Neuer Hinweistext zum Schließen
                    Text(
                        text = "Tippen zum Schließen",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}