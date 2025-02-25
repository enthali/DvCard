package de.drachenfels.dvcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.drachenfels.dvcard.data.BusinessCardRepository
import de.drachenfels.dvcard.data.model.BusinessCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel für die Verwaltung der Visitenkarten und UI-Zustände
 */
class BusinessCardViewModel(private val repository: BusinessCardRepository) : ViewModel() {
    
    // Status des Bearbeitungsmodus
    private val _cardEditMode = MutableStateFlow<CardEditState>(CardEditState.Closed)
    val cardEditMode: StateFlow<CardEditState> = _cardEditMode.asStateFlow()
    
    // Liste aller Visitenkarten
    private val _cards = MutableStateFlow<List<BusinessCard>>(emptyList())
    val cards: StateFlow<List<BusinessCard>> = _cards.asStateFlow()
    
    // Aktuell ausgewählte Karte für Bearbeitung
    private val _selectedCard = MutableStateFlow<BusinessCard?>(null)
    val selectedCard: StateFlow<BusinessCard?> = _selectedCard.asStateFlow()
    
    // QR-Code Dialog Status
    private val _qrCodeDialogCard = MutableStateFlow<BusinessCard?>(null)
    val qrCodeDialogCard: StateFlow<BusinessCard?> = _qrCodeDialogCard.asStateFlow()
    
    init {
        // Initialisiere den Flow der Karten aus dem Repository
        viewModelScope.launch {
            repository.allCards.collect { cardList ->
                _cards.value = cardList
            }
        }
    }
    
    /**
     * Erstellt eine neue leere Karte und öffnet den Bearbeitungsmodus
     */
    fun createNewCard() {
        _selectedCard.value = BusinessCard()
        _cardEditMode.value = CardEditState.Creating
    }
    
    /**
     * Öffnet eine bestehende Karte zur Bearbeitung
     */
    fun editCard(card: BusinessCard) {
        _selectedCard.value = card
        _cardEditMode.value = CardEditState.Editing(card.id)
    }
    
    /**
     * Schließt den Bearbeitungsmodus
     */
    fun closeEdit() {
        _cardEditMode.value = CardEditState.Closed
        _selectedCard.value = null
    }
    
    /**
     * Speichert eine Karte (neu oder bearbeitet)
     */
    fun saveCard(card: BusinessCard) {
        viewModelScope.launch {
            when (val currentMode = _cardEditMode.value) {
                is CardEditState.Creating -> {
                    repository.insertCard(card)
                }
                is CardEditState.Editing -> {
                    repository.updateCard(card)
                }
                else -> { /* Do nothing */ }
            }
            closeEdit()
        }
    }
    
    /**
     * Löscht eine Karte
     */
    fun deleteCard(card: BusinessCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
            closeEdit()
        }
    }
    
    /**
     * Öffnet den QR-Code Dialog für eine Karte
     */
    fun showQrCode(card: BusinessCard) {
        _qrCodeDialogCard.value = card
    }
    
    /**
     * Schließt den QR-Code Dialog
     */
    fun dismissQrCode() {
        _qrCodeDialogCard.value = null
    }
    
    /**
     * Zustand des Bearbeitungsmodus einer Karte
     */
    sealed class CardEditState {
        object Closed : CardEditState()
        object Creating : CardEditState()
        data class Editing(val cardId: Long) : CardEditState()
    }
}

/**
 * Factory für die Erstellung des ViewModels mit Repository-Abhängigkeit
 */
class BusinessCardViewModelFactory(private val repository: BusinessCardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessCardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}