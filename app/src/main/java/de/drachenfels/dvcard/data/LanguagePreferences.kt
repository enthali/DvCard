package de.drachenfels.dvcard.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Manages language preferences for the application
 */
class LanguagePreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "language_prefs", 
        Context.MODE_PRIVATE
    )
    
    private val _currentLanguage = MutableStateFlow(getCurrentLanguageFromPrefs())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    
    companion object {
        private const val KEY_LANGUAGE_CODE = "language_code"
        const val LANGUAGE_GERMAN = "de"
        const val LANGUAGE_ENGLISH = "en"
        
        // Default language is German as specified in requirements
        const val DEFAULT_LANGUAGE = LANGUAGE_GERMAN
    }
    
    /**
     * Get current language code from preferences
     */
    private fun getCurrentLanguageFromPrefs(): String {
        return prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
    
    /**
     * Get current language code
     */
    fun getCurrentLanguage(): String {
        return _currentLanguage.value
    }
    
    /**
     * Set current language
     */
    fun setLanguage(languageCode: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE_CODE, languageCode)
            .apply()
        
        _currentLanguage.value = languageCode
    }
    
    /**
     * Toggle between German and English
     */
    fun toggleLanguage() {
        val currentLanguage = getCurrentLanguage()
        val newLanguage = when (currentLanguage) {
            LANGUAGE_GERMAN -> LANGUAGE_ENGLISH
            LANGUAGE_ENGLISH -> LANGUAGE_GERMAN
            else -> LANGUAGE_GERMAN
        }
        setLanguage(newLanguage)
    }
    
    /**
     * Get display text for language switcher button
     */
    fun getLanguageSwitchText(): String {
        return when (getCurrentLanguage()) {
            LANGUAGE_GERMAN -> "EN"  // Show "EN" when German is active (to switch to English)
            LANGUAGE_ENGLISH -> "DE" // Show "DE" when English is active (to switch to German)
            else -> "EN"
        }
    }
    
    /**
     * Get locale for current language
     */
    fun getCurrentLocale(): Locale {
        return Locale(getCurrentLanguage())
    }
}