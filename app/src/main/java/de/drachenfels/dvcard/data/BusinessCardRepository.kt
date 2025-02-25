package de.drachenfels.dvcard.data

import de.drachenfels.dvcard.data.model.BusinessCard
import kotlinx.coroutines.flow.Flow

/**
 * Repository für den Zugriff auf BusinessCard-Daten
 *
 * Kapselt den Datenbankzugriff und bietet eine saubere API für das ViewModel.
 */
class BusinessCardRepository(private val businessCardDao: BusinessCardDao) {
    /**
     * Alle Visitenkarten als Flow für reaktives UI-Update.
     */
    val allCards: Flow<List<BusinessCard>> = businessCardDao.getAllCards()
    
    /**
     * Holt eine spezifische Visitenkarte anhand ihrer ID.
     */
    suspend fun getCardById(id: Long): BusinessCard? {
        return businessCardDao.getCardById(id)
    }
    
    /**
     * Fügt eine neue Visitenkarte in die Datenbank ein und gibt die
     * generierte ID zurück.
     */
    suspend fun insertCard(card: BusinessCard): Long {
        return businessCardDao.insertCard(card)
    }
    
    /**
     * Aktualisiert eine bestehende Visitenkarte.
     */
    suspend fun updateCard(card: BusinessCard) {
        businessCardDao.updateCard(card)
    }
    
    /**
     * Löscht eine Visitenkarte aus der Datenbank.
     */
    suspend fun deleteCard(card: BusinessCard) {
        businessCardDao.deleteCard(card)
    }
}