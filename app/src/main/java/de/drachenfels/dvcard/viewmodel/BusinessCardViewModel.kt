package de.drachenfels.dvcard.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.data.BusinessCardRepository
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing business cards and UI states
 */
class BusinessCardViewModel(
    private val repository: BusinessCardRepository,
    private val context: Context? = null
) : ViewModel() {
    
    // List of all business cards
    private val _cards = MutableStateFlow<List<BusinessCard>>(emptyList())
    val cards: StateFlow<List<BusinessCard>> = _cards.asStateFlow()
    
    // QR code dialog state
    private val _qrCodeDialogCard = MutableStateFlow<BusinessCard?>(null)
    val qrCodeDialogCard: StateFlow<BusinessCard?> = _qrCodeDialogCard.asStateFlow()
    
    // Flag to prevent double card creation when deleting the last card
    private var isCreatingNewCard = false
    
    init {
        Log.d(LogConfig.TAG_VIEWMODEL, "BusinessCardViewModel initialized")
        // Initialize the flow of cards from repository
        viewModelScope.launch {
            Log.d(LogConfig.TAG_VIEWMODEL, "Starting to collect cards from repository")
            repository.allCards.collect { cardList ->
                Log.d(LogConfig.TAG_VIEWMODEL, "Card list updated, count: ${cardList.size}")
                _cards.value = cardList
                
                // Auto-create a card if the list is empty AND we're not already creating a card
                if (cardList.isEmpty() && !isCreatingNewCard) {
                    Log.d(LogConfig.TAG_VIEWMODEL, "Card list is empty, auto-creating a new card")
                    createNewCard()
                }
            }
        }
    }
    
    /**
     * Creates a new card directly in the database
     * @return The ID of the new card if successfully created
     */
    fun createNewCard(): Long? {
        Log.d(LogConfig.TAG_VIEWMODEL, "Creating new card")
        var newId: Long? = null
        
        // Set flag to prevent duplicate card creation
        isCreatingNewCard = true
        
                viewModelScope.launch {
            val newCard = BusinessCard( isExpanded = true)
            try {
                newId = repository.insertCard(newCard)
                Log.d(LogConfig.TAG_VIEWMODEL, "New card successfully created with ID: $newId")
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error saving new card", e)
            } finally {
                // Reset flag after creation attempt completes
                isCreatingNewCard = false
            }
        }
        
        return newId
    }
    
    /**
     * Saves or updates a card
     * Calls the repository to persist the data
     */
    fun saveCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Saving card: ID=${card.id}, Name=${card.familyName}")
        viewModelScope.launch {
            try {
                repository.updateCard(card)
                Log.d(LogConfig.TAG_VIEWMODEL, "Card successfully saved")
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error saving/updating card", e)
            }
        }
    }

    /**
     * Aktualisiere lokale LiveData/StateFlow der Karte
     */
    fun updateCard(card: BusinessCard) {
        val updatedCards = _cards.value.map {
            if (it.id == card.id) card else it
        }
        _cards.value = updatedCards
    }
    
    /**
     * Deletes a card
     */
    fun deleteCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Deleting card: ID=${card.id}, Name=${card.familyName}")
        viewModelScope.launch {
            try {
                // Check if this is the last card
                val isLastCard = _cards.value.size <= 1
                
                // If it's the last card, set the flag to prevent auto-creation
                if (isLastCard) {
                    isCreatingNewCard = true
                }
                
                // Delete the card
                repository.deleteCard(card)
                Log.d(LogConfig.TAG_VIEWMODEL, "Card successfully deleted")
                
                // If this was the last card, create a new one explicitly
                if (isLastCard) {
                    Log.d(LogConfig.TAG_VIEWMODEL, "Last card was deleted, creating a new one")
                    val newCard = BusinessCard(
                        isExpanded = true
                    )
                    repository.insertCard(newCard)
                    // Reset the flag after we've explicitly created a new card
                    isCreatingNewCard = false
                }
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error deleting card", e)
                // Reset the flag in case of error
                isCreatingNewCard = false
            }
        }
    }

    /**
     * Opens the QR code dialog for a card
     */
    fun showQrCode(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Showing QR code for card: ID=${card.id}")
        _qrCodeDialogCard.value = card
    }
    
    /**
     * Closes the QR code dialog
     */
    fun dismissQrCode() {
        Log.d(LogConfig.TAG_VIEWMODEL, "Dismissing QR code dialog")
        _qrCodeDialogCard.value = null
    }
}

/**
 * Factory for creating the ViewModel with repository dependency
 */
class BusinessCardViewModelFactory(
    private val repository: BusinessCardRepository,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessCardViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}