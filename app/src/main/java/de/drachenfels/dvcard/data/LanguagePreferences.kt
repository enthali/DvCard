package de.drachenfels.dvcard.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

/**
 * Manages language preferences for the application
 */
class LanguagePreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "language_prefs", 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_LANGUAGE_CODE = "language_code"
        const val LANGUAGE_GERMAN = "de"
        const val LANGUAGE_ENGLISH = "en"
        
        // Default language is German as specified in requirements
        const val DEFAULT_LANGUAGE = LANGUAGE_GERMAN
    }
    
    /**
     * Get current language code
     */
    fun getCurrentLanguage(): String {
        return prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
    
    /**
     * Set current language and apply it to the app
     */
    fun setLanguage(languageCode: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE_CODE, languageCode)
            .apply()
        
        // Apply language change immediately
        applyLanguage(languageCode)
    }
    
    /**
     * Apply language change to the app
     */
    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    
    /**
     * Initialize language on app start
     */
    fun initializeLanguage() {
        val currentLanguage = getCurrentLanguage()
        applyLanguage(currentLanguage)
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
}