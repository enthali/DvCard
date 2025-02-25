package de.drachenfels.dvcard.util.logger

import de.drachenfels.dvcard.BuildConfig

/**
 * Zentrale Konfiguration für das Logging-System.
 * Alle Debug-Flags und Komponenten-Tags werden hier verwaltet.
 */
object LogConfig {
    // Debug Flags
    var ENABLE_DEBUG = BuildConfig.DEBUG      // Master-Schalter für Debug
    var ENABLE_DEBUG_MAIN = true              // App-Logging
    var ENABLE_DEBUG_DATABASE = true          // Datenbank-Logging
    var ENABLE_DEBUG_UI = true                // UI-Logging
    var ENABLE_DEBUG_VIEWMODEL = true         // ViewModel-Logging
    var ENABLE_DEBUG_UTILS = true             // Utilities-Logging

    // Standard-Tags für Komponenten
    const val TAG_MAIN = "DVCard: MainActivity"      // Allgemeiner App-Tag
    const val TAG_DATABASE = "DVCard: Database"      // Datenbank-spezifisch
    const val TAG_UI = "DVCard: UI"                  // UI-bezogen
    const val TAG_VIEWMODEL = "DVCard: ViewModel"    // ViewModel-bezogen
    const val TAG_UTILS = "DVCard: Utils"            // Hilfsfunktionen

    // Berechnete Eigenschaft für Debug-Status
    val isDebuggingEnabled: Boolean
        get() = ENABLE_DEBUG && (
                ENABLE_DEBUG_MAIN ||
                ENABLE_DEBUG_DATABASE || 
                ENABLE_DEBUG_UI ||
                ENABLE_DEBUG_VIEWMODEL ||
                ENABLE_DEBUG_UTILS)
}

/**
 * Interface für das Logging-System.
 * Ermöglicht verschiedene Implementierungen für Android und Tests.
 */
interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
}

/**
 * Android-spezifische Logger-Implementierung.
 * Verwendet das Android-Log-System und berücksichtigt Debug-Flags.
 */
class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        if (LogConfig.isDebuggingEnabled && shouldLog(tag)) {
            android.util.Log.d(tag, message)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (LogConfig.isDebuggingEnabled && shouldLog(tag)) {
            android.util.Log.e(tag, message, throwable)
        }
    }

    override fun i(tag: String, message: String) {
        if (LogConfig.isDebuggingEnabled && shouldLog(tag)) {
            android.util.Log.i(tag, message)
        }
    }

    override fun w(tag: String, message: String) {
        if (LogConfig.isDebuggingEnabled && shouldLog(tag)) {
            android.util.Log.w(tag, message)
        }
    }

    private fun shouldLog(tag: String): Boolean {
        return when (tag) {
            LogConfig.TAG_DATABASE -> LogConfig.ENABLE_DEBUG_DATABASE
            LogConfig.TAG_UI -> LogConfig.ENABLE_DEBUG_UI
            LogConfig.TAG_VIEWMODEL -> LogConfig.ENABLE_DEBUG_VIEWMODEL
            LogConfig.TAG_UTILS -> LogConfig.ENABLE_DEBUG_UTILS
            else -> LogConfig.ENABLE_DEBUG_MAIN
        }
    }
}

/**
 * Test-spezifische Logger-Implementierung.
 * Gibt Logs auf der Konsole aus und ignoriert Debug-Flags.
 */
class TestLogger : Logger {
    override fun d(tag: String, message: String) {
        println("DEBUG/$tag: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("ERROR/$tag: $message")
        throwable?.printStackTrace()
    }

    override fun i(tag: String, message: String) {
        println("INFO/$tag: $message")
    }

    override fun w(tag: String, message: String) {
        println("WARN/$tag: $message")
    }
}

/**
 * Singleton zur Bereitstellung einer globalen Logger-Instanz.
 */
object Log {
    private val logger: Logger = AndroidLogger()

    fun d(tag: String, message: String) {
        logger.d(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        logger.e(tag, message, throwable)
    }

    fun i(tag: String, message: String) {
        logger.i(tag, message)
    }

    fun w(tag: String, message: String) {
        logger.w(tag, message)
    }
}