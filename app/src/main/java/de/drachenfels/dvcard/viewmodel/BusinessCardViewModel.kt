package de.drachenfels.dvcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.drachenfels.dvcard.data.BusinessCardRepository
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
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
        Log.d(LogConfig.TAG_VIEWMODEL, "BusinessCardViewModel initialisiert")
        // Initialisiere den Flow der Karten aus dem Repository
        viewModelScope.launch {
            Log.d(LogConfig.TAG_VIEWMODEL, "Starte Sammlung von Karten aus Repository")
            repository.allCards.collect { cardList ->
                Log.d(LogConfig.TAG_VIEWMODEL, "Kartenliste aktualisiert, Anzahl: ${cardList.size}")
                _cards.value = cardList
            }
        }
    }
    
    /**
     * Erstellt eine neue Karte direkt in der Datenbank und öffnet sie im Bearbeitungsmodus
     */
    fun createNewCard() {
        Log.d(LogConfig.TAG_VIEWMODEL, "Erstelle neue Karte direkt in der Datenbank")
        viewModelScope.launch {
            val newCard = BusinessCard(name = "Neue Karte")
            try {
                val newId = repository.insertCard(newCard)
                Log.d(LogConfig.TAG_VIEWMODEL, "Neue Karte erfolgreich gespeichert, ID: $newId")
                
                // Warten bis die Karte in der Liste erscheint
                repository.getCardById(newId)?.let { savedCard ->
                    _selectedCard.value = savedCard
                    _cardEditMode.value = CardEditState.Editing(newId)
                    Log.d(LogConfig.TAG_VIEWMODEL, "Edit-Modus auf EDITING für neue Karte gesetzt")
                }
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Fehler beim Speichern der neuen Karte", e)
            }
        }
    }
    
    /**
     * Öffnet eine bestehende Karte zur Bearbeitung
     */
    fun editCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Öffne Karte zur Bearbeitung: ID=${card.id}, Name=${card.name}")
        _selectedCard.value = card
        _cardEditMode.value = CardEditState.Editing(card.id)
    }
    
    /**
     * Schließt den Bearbeitungsmodus
     */
    fun closeEdit() {
        Log.d(LogConfig.TAG_VIEWMODEL, "Schließe Bearbeitungsmodus")
        _cardEditMode.value = CardEditState.Closed
        _selectedCard.value = null
    }
    
    /**
     * Speichert eine Karte (neu oder bearbeitet)
     */
    fun saveCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "saveCard aufgerufen mit Karte: ID=${card.id}, Name=${card.name}")
        viewModelScope.launch {
            try {
                repository.updateCard(card)
                Log.d(LogConfig.TAG_VIEWMODEL, "Karte erfolgreich aktualisiert")
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Fehler beim Aktualisieren der Karte", e)
            }
            closeEdit()
        }
    }
    
    /**
     * Löscht eine Karte
     */
    fun deleteCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Lösche Karte: ID=${card.id}, Name=${card.name}")
        viewModelScope.launch {
            try {
                repository.deleteCard(card)
                Log.d(LogConfig.TAG_VIEWMODEL, "Karte erfolgreich gelöscht")
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Fehler beim Löschen der Karte", e)
            }
            closeEdit()
        }
    }
    
    /**
     * Öffnet den QR-Code Dialog für eine Karte
     */
    fun showQrCode(card: BusinessCard) {
        Log.d(LogConfig.TAG_VIEWMODEL, "Zeige QR-Code für Karte: ID=${card.id}, Name=${card.name}")
        _qrCodeDialogCard.value = card
    }
    
    /**
     * Schließt den QR-Code Dialog
     */
    fun dismissQrCode() {
        Log.d(LogConfig.TAG_VIEWMODEL, "Schließe QR-Code Dialog")
        _qrCodeDialogCard.value = null
    }
    
    /**
     * Zustand des Bearbeitungsmodus einer Karte
     */
    sealed class CardEditState {
        object Closed : CardEditState()
        object Creating : CardEditState() // Belassen für Abwärtskompatibilität, wird aber nicht mehr verwendet
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