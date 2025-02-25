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
import de.drachenfels.dvcard.ui.MainScreen
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
    
    // Datenbank, Repository und ViewModel initialisieren
    private val database by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initialisiere Datenbank")
        BusinessCardDatabase.getDatabase(this) 
    }
    private val repository by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initialisiere Repository")
        BusinessCardRepository(database.businessCardDao()) 
    }
    private val viewModelFactory by lazy { 
        Log.d(LogConfig.TAG_MAIN, "Initialisiere ViewModelFactory")
        BusinessCardViewModelFactory(repository) 
    }
    private val viewModel: BusinessCardViewModel by viewModels { 
        Log.d(LogConfig.TAG_MAIN, "Initialisiere ViewModel")
        viewModelFactory 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onCreate aufgerufen")
        
        enableEdgeToEdge()
        
        // Prüfen ob die Datenbank existiert und erreichbar ist
        checkDatabase()
        
        setContent {
            Log.d(LogConfig.TAG_MAIN, "Setze Compose-Content")
            DigtalBusinessCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
        
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onCreate abgeschlossen")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(LogConfig.TAG_MAIN, "MainActivity.onResume aufgerufen")
    }
    
    /**
     * Überprüft, ob die Datenbank korrekt initialisiert wurde und zeigt eine Meldung an,
     * wenn ein Problem auftritt.
     */
    private fun checkDatabase() {
        applicationScope.launch(Dispatchers.IO) {
            try {
                val dbFile = getDatabasePath("business_card_database")
                Log.d(LogConfig.TAG_MAIN, "Datenbankpfad: ${dbFile.absolutePath}")
                Log.d(LogConfig.TAG_MAIN, "Datenbank existiert: ${dbFile.exists()}")
                if (dbFile.exists()) {
                    Log.d(LogConfig.TAG_MAIN, "Datenbank-Größe: ${dbFile.length()} Bytes")
                    Log.d(LogConfig.TAG_MAIN, "Datenbank ist beschreibbar: ${dbFile.canWrite()}")
                    Log.d(LogConfig.TAG_MAIN, "Datenbank ist lesbar: ${dbFile.canRead()}")
                } else {
                    // Datenbank existiert nicht, versuche zu initialisieren
                    Log.w(LogConfig.TAG_MAIN, "Datenbank existiert nicht, versuche zu initialisieren")
                    val dir = dbFile.parentFile
                    if (dir != null && !dir.exists()) {
                        val created = dir.mkdirs()
                        Log.d(LogConfig.TAG_MAIN, "Datenbank-Verzeichnis erstellt: $created")
                    }
                    
                    // Teste Zugriff durch Einfügen einer Test-Abfrage
                    val count = repository.getCardsCount()
                    Log.d(LogConfig.TAG_MAIN, "Anzahl der Karten in der Datenbank: $count")
                }
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_MAIN, "Fehler beim Überprüfen der Datenbank", e)
                // Auf dem UI-Thread eine Fehlermeldung anzeigen
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Fehler beim Zugriff auf die Datenbank: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}