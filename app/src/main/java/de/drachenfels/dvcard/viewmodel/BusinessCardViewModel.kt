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
    
    init {
        // Initialize the flow of cards from repository
        viewModelScope.launch {
            repository.allCards.collect { cardList ->
                _cards.value = cardList
                
                // Auto-create a card if the list is empty
                if (cardList.isEmpty()) {
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
        var newId: Long? = null
        
        // Use string resource if context is available, otherwise default to "New Card"
        val defaultName = context?.getString(R.string.card_add) ?: "New Card"
        
        viewModelScope.launch {
            val newCard = BusinessCard(name = defaultName, isExpanded = true)
            try {
                newId = repository.insertCard(newCard)
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error saving new card", e)
            }
        }
        
        return newId
    }
    
    /**
     * Saves or updates a card
     * Calls the repository to persist the data
     */
    fun saveCard(card: BusinessCard) {
        viewModelScope.launch {
            try {
                repository.updateCard(card)
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error saving/updating card", e)
            }
        }
    }
    
    /**
     * Deletes a card
     */
    fun deleteCard(card: BusinessCard) {
        viewModelScope.launch {
            try {
                repository.deleteCard(card)
                
                // If this was the last card, create a new one
                if (_cards.value.size <= 1) {
                    createNewCard()
                }
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Error deleting card", e)
            }
        }
    }

    /**
     * Opens the QR code dialog for a card
     */
    fun showQrCode(card: BusinessCard) {
        _qrCodeDialogCard.value = card
    }
    
    /**
     * Closes the QR code dialog
     */
    fun dismissQrCode() {
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