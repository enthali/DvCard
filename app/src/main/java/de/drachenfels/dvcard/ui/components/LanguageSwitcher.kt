package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.drachenfels.dvcard.data.LanguagePreferences
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Language switcher button for the top app bar
 * Shows "EN" when German is active (to switch to English)
 * Shows "DE" when English is active (to switch to German)
 */
@Composable
fun LanguageSwitcher(
    onLanguageToggle: () -> Unit,
    currentLanguageFlow: StateFlow<String>,
    modifier: Modifier = Modifier
) {
    val currentLanguage by currentLanguageFlow.collectAsState()
    
    val displayText = when (currentLanguage) {
        LanguagePreferences.LANGUAGE_GERMAN -> "EN"
        LanguagePreferences.LANGUAGE_ENGLISH -> "DE"  
        else -> "EN"
    }
    
    Button(
        onClick = onLanguageToggle,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = null
    ) {
        Text(
            text = displayText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Simplified language switcher for preview
 */
@Composable
private fun LanguageSwitcherPreview(
    displayText: String,
    onLanguageToggle: () -> Unit
) {
    Button(
        onClick = onLanguageToggle,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = null
    ) {
        Text(
            text = displayText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSwitcherPreviewGerman() {
    DigtalBusinessCardTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LanguageSwitcherPreview(
                displayText = "EN",
                onLanguageToggle = { /* Preview - no action */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable  
fun LanguageSwitcherPreviewEnglish() {
    DigtalBusinessCardTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LanguageSwitcherPreview(
                displayText = "DE",
                onLanguageToggle = { /* Preview - no action */ }
            )
        }
    }
}