package de.drachenfels.dvcard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.drachenfels.dvcard.data.BusinessCardDatabase
import de.drachenfels.dvcard.data.BusinessCardRepository
import de.drachenfels.dvcard.data.LanguagePreferences
import de.drachenfels.dvcard.ui.MainScreen
import de.drachenfels.dvcard.ui.components.LocaleProvider
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModel
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Initialize database, repository and ViewModel
    private val database by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initializing database")
        BusinessCardDatabase.getDatabase(this) 
    }
    private val repository by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initializing repository")
        BusinessCardRepository(database.businessCardDao()) 
    }
    private val viewModelFactory by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initializing ViewModelFactory")
        BusinessCardViewModelFactory(repository)
    }
    private val viewModel: BusinessCardViewModel by viewModels { 
        Log.d(LogConfig.TAG_MAIN, "Initializing ViewModel")
        viewModelFactory 
    }
    
    // Language preferences
    private val languagePrefs by lazy {
        Log.d(LogConfig.TAG_MAIN, "Initializing LanguagePreferences")
        LanguagePreferences(this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onCreate called")
        
        enableEdgeToEdge()
        
        // Check if the database exists and is accessible
        checkDatabase()
        
        setContent {
            Log.d(LogConfig.TAG_MAIN, "Setting up Compose content")
            DigtalBusinessCardTheme {
                LocaleProvider(languagePreferences = languagePrefs) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(viewModel = viewModel)
                    }
                }
            }
        }
        
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onCreate completed")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onResume called")
    }
    
    /**
     * Checks if the database is correctly initialized and shows a message
     * if a problem occurs.
     */
    private fun checkDatabase() {
        applicationScope.launch(Dispatchers.IO) {
            try {
                val dbFile = getDatabasePath("business_card_database")
                Log.d(LogConfig.TAG_MAIN, "Database path: ${dbFile.absolutePath}")
                Log.d(LogConfig.TAG_MAIN, "Database exists: ${dbFile.exists()}")
                if (dbFile.exists()) {
                    Log.d(LogConfig.TAG_MAIN, "Database size: ${dbFile.length()} bytes")
                    Log.d(LogConfig.TAG_MAIN, "Database is writable: ${dbFile.canWrite()}")
                    Log.d(LogConfig.TAG_MAIN, "Database is readable: ${dbFile.canRead()}")
                } else {
                    // Database doesn't exist, try to initialize
                    Log.w(LogConfig.TAG_MAIN, "Database doesn't exist, trying to initialize")
                    val dir = dbFile.parentFile
                    if (dir != null && !dir.exists()) {
                        val created = dir.mkdirs()
                        Log.d(LogConfig.TAG_MAIN, "Database directory created: $created")
                    }
                    
                    // Test access by inserting a test query
                    val count = repository.getCardsCount()
                    Log.d(LogConfig.TAG_MAIN, "Number of cards in database: $count")
                }
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_MAIN, "Error checking database", e)
                // Show an error message on the UI thread
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error accessing database: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}