package de.drachenfels.dvcard.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QR-Code für ${card.name}",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // QR-Code-Bild generieren und anzeigen
                // Für Previews verwenden wir ein simuliertes Bitmap
                val isPreview = LocalInspectionMode.current
                val qrBitmap = if (isPreview) {
                    // Mock-Bitmap für Preview
                    createMockQrBitmap(256)
                } else {
                    // Echten QR-Code für die App
                    generateVCardQrCode(card)
                }
                
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR-Code für ${card.name}",
                    modifier = Modifier.size(256.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Hinweistext
                Text(
                    text = "Scannen Sie diesen Code, um die Kontaktdaten zu importieren.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Schließen")
                }
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
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "QR-Code für ${sampleCard.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Mock-QR-Code für Preview
                    Image(
                        bitmap = createMockQrBitmap(256).asImageBitmap(),
                        contentDescription = "QR-Code für ${sampleCard.name}",
                        modifier = Modifier.size(256.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Scannen Sie diesen Code, um die Kontaktdaten zu importieren.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { /* Dummy-Callback */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Schließen")
                    }
                }
            }
        }
    }
}