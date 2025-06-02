package de.drachenfels.dvcard.ui.components

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import de.drachenfels.dvcard.data.LanguagePreferences
import java.util.Locale

/**
 * Provides localized context based on user language preference
 */
@Composable
fun LocaleProvider(
    languagePreferences: LanguagePreferences,
    content: @Composable () -> Unit
) {
    val currentLanguage by languagePreferences.currentLanguage.collectAsState()
    val context = LocalContext.current
    
    val localizedContext = remember(currentLanguage) {
        createLocalizedContext(context, currentLanguage)
    }
    
    val localizedConfiguration = remember(currentLanguage) {
        val locale = Locale(currentLanguage)
        Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
    }
    
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration
    ) {
        content()
    }
}

/**
 * Creates a context with the specified locale
 */
private fun createLocalizedContext(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    return context.createConfigurationContext(configuration)
}