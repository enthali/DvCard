package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.BuildConfig
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.createMockQrBitmap
import de.drachenfels.dvcard.util.generateUrlQrCode

/**
 * About dialog showing application information
 *
 * @param onDismiss Callback when the dialog is dismissed
 */
@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            AboutDialogTitle() 
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                AboutDialogContent()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_close))
            }
        }
    )
}

/**
 * Title section of the about dialog
 */
@Composable
private fun AboutDialogTitle() {
    Text(
        text = stringResource(R.string.about_title),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Content section of the about dialog
 */
@Composable
private fun AboutDialogContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // App name
        Text(
            text = stringResource(R.string.about_subtitle),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Version information
        VersionInfo()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // App description
        Text(
            text = stringResource(R.string.about_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Copyright information
        CopyrightInfo()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Play Store QR Code
        Divider()
        PlayStoreQrCodeSection()
    }
}

/**
 * Version information section
 */
@Composable
private fun VersionInfo() {
    Text(
        text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    Text(
        text = stringResource(R.string.about_build, BuildConfig.VERSION_CODE.toString()),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Copyright information section
 */
@Composable
private fun CopyrightInfo() {
    val uriHandler = LocalUriHandler.current
    val githubUrl = "https://github.com/drachenfels-de/dvcard"
    
    Text(
        text = stringResource(R.string.about_copyright),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    Text(
        text = stringResource(R.string.about_rights),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    // GitHub repository link
    Text(
        text = stringResource(R.string.about_github),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Start
    )
}

/**
 * Play Store QR code section
 */
@Composable
private fun PlayStoreQrCodeSection() {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=de.drachenfels.dvcard"
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = stringResource(R.string.playstore_qr_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // QR Code
        PlayStoreQrCode(playStoreUrl)
        
        // Description
        Text(
            text = stringResource(R.string.playstore_qr_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * QR code image for Play Store
 */
@Composable
private fun PlayStoreQrCode(playStoreUrl: String) {
    // Generate QR code bitmap (or use mock for preview)
    val isPreview = LocalInspectionMode.current
    val qrBitmap = if (isPreview) {
        // Mock bitmap for preview
        createMockQrBitmap(256)
    } else {
        // Real QR code for the app
        generateUrlQrCode(playStoreUrl, 256)
    }
    
    // Box with a subtle shadow for the QR code
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .size(200.dp),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp,
    ) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.playstore_qr_title),
            modifier = Modifier
                .size(196.dp)
                .padding(2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutDialogPreview() {
    DigtalBusinessCardTheme {
        // Using a dummy callback for the preview
        AboutDialog(onDismiss = {})
    }
}