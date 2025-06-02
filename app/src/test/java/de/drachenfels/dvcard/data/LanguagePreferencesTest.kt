package de.drachenfels.dvcard.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LanguagePreferencesTest {

    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    
    @Mock
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    }

    @Test
    fun `getCurrentLanguage returns default German when no preference set`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.DEFAULT_LANGUAGE)
            
        val languagePrefs = LanguagePreferences(context)
        assertEquals(LanguagePreferences.LANGUAGE_GERMAN, languagePrefs.getCurrentLanguage())
    }

    @Test
    fun `getCurrentLanguage returns saved language preference`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.LANGUAGE_ENGLISH)
            
        val languagePrefs = LanguagePreferences(context)
        assertEquals(LanguagePreferences.LANGUAGE_ENGLISH, languagePrefs.getCurrentLanguage())
    }

    @Test
    fun `setLanguage saves preference and updates state`() = runTest {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.DEFAULT_LANGUAGE)
            
        val languagePrefs = LanguagePreferences(context)
        
        languagePrefs.setLanguage(LanguagePreferences.LANGUAGE_ENGLISH)
        
        verify(editor).putString("language_code", LanguagePreferences.LANGUAGE_ENGLISH)
        verify(editor).apply()
    }

    @Test
    fun `toggleLanguage switches from German to English`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.LANGUAGE_GERMAN)
            
        val languagePrefs = LanguagePreferences(context)
        
        languagePrefs.toggleLanguage()
        
        verify(editor).putString("language_code", LanguagePreferences.LANGUAGE_ENGLISH)
    }

    @Test
    fun `toggleLanguage switches from English to German`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.LANGUAGE_ENGLISH)
            
        val languagePrefs = LanguagePreferences(context)
        
        languagePrefs.toggleLanguage()
        
        verify(editor).putString("language_code", LanguagePreferences.LANGUAGE_GERMAN)
    }

    @Test
    fun `getLanguageSwitchText returns EN when German is active`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.LANGUAGE_GERMAN)
            
        val languagePrefs = LanguagePreferences(context)
        assertEquals("EN", languagePrefs.getLanguageSwitchText())
    }

    @Test
    fun `getLanguageSwitchText returns DE when English is active`() {
        `when`(sharedPreferences.getString("language_code", LanguagePreferences.DEFAULT_LANGUAGE))
            .thenReturn(LanguagePreferences.LANGUAGE_ENGLISH)
            
        val languagePrefs = LanguagePreferences(context)
        assertEquals("DE", languagePrefs.getLanguageSwitchText())
    }
}