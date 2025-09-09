package de.drachenfels.dvcard.ui.components

import android.animation.ValueAnimator
import android.app.Activity
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.createMockQrBitmap
import de.drachenfels.dvcard.util.generateVCardQrCode
import kotlin.math.min

/**
 * Dialog to display a QR code for a business card
 * 
 * @param card The business card to generate QR code for
 * @param onDismiss Callback when the dialog is dismissed
 */
@Composable
fun QrCodeDialog(card: BusinessCard, onDismiss: () -> Unit) {
    // Determine display title (use title if available, otherwise use full name)
    val displayTitle = if (card.title.isNotEmpty()) card.title else card.getFullName()
    
    // Implementierung der automatischen Helligkeitsanpassung
    val context = LocalContext.current
    val activity = context as? Activity

    // DisposableEffect für Helligkeitsanpassung
    DisposableEffect(Unit) {
        val window = activity?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        
        // Aktuelle Helligkeit bestimmen
        val actualBrightness = if (originalBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            try {
                val systemBrightness = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                )
                systemBrightness / 255f // 0-255 to 0-1 scale
            } catch (_: Exception) {
                0.5f // Default fallback
            }
        } else {
            originalBrightness
        }
        
        // Helligkeit um 50% erhöhen (maximal 1.0)
        val targetBrightness = min(actualBrightness * 1.5f, 1.0f)
        
        // Animation der Helligkeit
        val valueAnimator = ValueAnimator.ofFloat(actualBrightness, targetBrightness)
        valueAnimator.addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            window?.attributes = window.attributes?.apply {
                screenBrightness = value
            }
        }
        valueAnimator.duration = 300
        valueAnimator.start()
        
        onDispose {
            // Animation zum Originalwert zurück
            val restoreAnimator = ValueAnimator.ofFloat(
                window?.attributes?.screenBrightness ?: targetBrightness, 
                originalBrightness
            )
            restoreAnimator.addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                window?.attributes = window.attributes?.apply {
                    screenBrightness = value
                }
            }
            restoreAnimator.duration = 300
            restoreAnimator.start()
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        QrCodeDialogContent(
            displayTitle = displayTitle,
            card = card,
            onDismiss = onDismiss
        )
    }
}

/**
 * Content of the QR code dialog
 */
@Composable
private fun QrCodeDialogContent(
    displayTitle: String,
    card: BusinessCard,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            // Make the entire dialog clickable to dismiss
            .clickable { onDismiss() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dialog title and card name
            DialogHeader(displayTitle)
            
            // QR Code image
            QrCodeImage(card, displayTitle)
            
            // Instructions
            DialogFooter()
        }
    }
}

/**
 * Header section of the QR code dialog
 */
@Composable
private fun DialogHeader(displayTitle: String) {
    // Dialog title
    Text(
        text = stringResource(R.string.qr_code_dialog_title),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
    
    // Card name or title
    Text(
        text = displayTitle,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )
}

/**
 * QR code image section
 */
@Composable
private fun QrCodeImage(card: BusinessCard, displayTitle: String) {
    // Generate QR code bitmap (or use mock for preview)
    val isPreview = LocalInspectionMode.current
    val qrBitmap = if (isPreview) {
        // Mock bitmap for preview
        createMockQrBitmap(256)
    } else {
        // Real QR code for the app
        generateVCardQrCode(card)
    }
    
    // Box with a subtle shadow for the QR code
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .size(260.dp),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp,
    ) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.qr_code_description, displayTitle),
            modifier = Modifier
                .size(256.dp)
                .padding(2.dp)
        )
    }
    
    Spacer(modifier = Modifier.height(12.dp))
}

/**
 * Footer section with instructions
 */
@Composable
private fun DialogFooter() {
    // Instruction to scan the QR code
    Text(
        text = stringResource(R.string.qr_code_instruction),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    
    // Hint to close the dialog
    Text(
        text = stringResource(R.string.qr_code_tap_to_close),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 16.dp)
    )
}

// Sample data for previews
private val sampleCard = BusinessCard(
    id = 1,
    title = "Geschäftskarte",
    givenName = "Max",
    familyName = "Mustermann",
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
    givenName = "Max",
    familyName = "Mustermann",
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
            // Since the dialog is displayed as an overlay and we can't have a dialog in a preview,
            // we display the content of the dialog directly
            QrCodeDialogContent(
                displayTitle = if (sampleCard.title.isNotEmpty()) sampleCard.title else sampleCard.getFullName(),
                card = sampleCard,
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "QR Code without title")
@Composable
fun QrCodeDialogNoTitlePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            QrCodeDialogContent(
                displayTitle = sampleCardNoTitle.getFullName(),
                card = sampleCardNoTitle,
                onDismiss = {}
            )
        }
    }
}