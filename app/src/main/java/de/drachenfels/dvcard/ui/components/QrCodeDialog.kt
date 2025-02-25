package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.drachenfels.dvcard.data.model.BusinessCard
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
                val qrBitmap = generateVCardQrCode(card)
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