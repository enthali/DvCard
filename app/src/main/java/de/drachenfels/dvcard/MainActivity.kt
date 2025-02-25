package de.drachenfels.dvcard

import android.os.Bundle
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
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModel
import de.drachenfels.dvcard.viewmodel.BusinessCardViewModelFactory

class MainActivity : ComponentActivity() {
    
    // Datenbank, Repository und ViewModel initialisieren
    private val database by lazy { BusinessCardDatabase.getDatabase(this) }
    private val repository by lazy { BusinessCardRepository(database.businessCardDao()) }
    private val viewModelFactory by lazy { BusinessCardViewModelFactory(repository) }
    private val viewModel: BusinessCardViewModel by viewModels { viewModelFactory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            DigtalBusinessCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}