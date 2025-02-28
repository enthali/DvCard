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
    
    // Liste aller Visitenkarten
    private val _cards = MutableStateFlow<List<BusinessCard>>(emptyList())
    val cards: StateFlow<List<BusinessCard>> = _cards.asStateFlow()
    
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
     * Erstellt eine neue Karte direkt in der Datenbank
     * @return Die ID der neuen Karte, falls erfolgreich erstellt
     */
    fun createNewCard(): Long? {
        Log.d(LogConfig.TAG_VIEWMODEL, "Erstelle neue Karte")
        var newId: Long? = null
        
        viewModelScope.launch {
            val newCard = BusinessCard(name = "Neue Karte", isExpanded = true)
            try {
                newId = repository.insertCard(newCard)
                Log.d(LogConfig.TAG_VIEWMODEL, "Neue Karte erfolgreich gespeichert, ID: $newId")
            } catch (e: Exception) {
                Log.e(LogConfig.TAG_VIEWMODEL, "Fehler beim Speichern der neuen Karte", e)
            }
        }
        
        return newId
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