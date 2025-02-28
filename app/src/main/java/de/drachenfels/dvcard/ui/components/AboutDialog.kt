package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.BuildConfig
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme

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
            AboutDialogContent()
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
}

@Preview(showBackground = true)
@Composable
fun AboutDialogPreview() {
    DigtalBusinessCardTheme {
        // Using a dummy callback for the preview
        AboutDialog(onDismiss = {})
    }
}