package de.drachenfels.dvcard.data

import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository für den Zugriff auf BusinessCard-Daten
 *
 * Kapselt den Datenbankzugriff und bietet eine saubere API für das ViewModel.
 */
class BusinessCardRepository(private val businessCardDao: BusinessCardDao) {
    
    init {
        Log.d(LogConfig.TAG_DATABASE, "BusinessCardRepository initialisiert")
    }
    
    /**
     * Alle Visitenkarten als Flow für reaktives UI-Update.
     */
    val allCards: Flow<List<BusinessCard>> = businessCardDao.getAllCards()
    
    /**
     * Gibt die Anzahl der Karten in der Datenbank zurück.
     * Hilfreich zum Testen der Datenbankverbindung.
     */
    suspend fun getCardsCount(): Int {
        return try {
            val cards = businessCardDao.getCardCount()
            Log.d(LogConfig.TAG_DATABASE, "Anzahl der Karten in der Datenbank: $cards")
            cards
        } catch (e: Exception) {
            Log.e(LogConfig.TAG_DATABASE, "Fehler beim Abrufen der Kartenanzahl", e)
            throw e
        }
    }

    /**
     * Fügt eine neue Visitenkarte in die Datenbank ein und gibt die
     * generierte ID zurück.
     */
    suspend fun insertCard(card: BusinessCard): Long {
        Log.d(LogConfig.TAG_DATABASE, "Füge neue Karte ein: ${card.familyName}")
        return try {
            val id = businessCardDao.insertCard(card)
            Log.d(LogConfig.TAG_DATABASE, "Karte eingefügt mit ID: $id")
            id
        } catch (e: Exception) {
            Log.e(LogConfig.TAG_DATABASE, "Fehler beim Einfügen der Karte: ${card.familyName}", e)
            throw e
        }
    }
    
    /**
     * Aktualisiert eine bestehende Visitenkarte.
     */
    suspend fun updateCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_DATABASE, "Aktualisiere Karte: ID=${card.id}, Name=${card.familyName}")
        try {
            businessCardDao.updateCard(card)
            Log.d(LogConfig.TAG_DATABASE, "Karte erfolgreich aktualisiert")
        } catch (e: Exception) {
            Log.e(LogConfig.TAG_DATABASE, "Fehler beim Aktualisieren der Karte", e)
            throw e
        }
    }
    
    /**
     * Löscht eine Visitenkarte aus der Datenbank.
     */
    suspend fun deleteCard(card: BusinessCard) {
        Log.d(LogConfig.TAG_DATABASE, "Lösche Karte: ID=${card.id}, Name=${card.familyName}")
        try {
            businessCardDao.deleteCard(card)
            Log.d(LogConfig.TAG_DATABASE, "Karte erfolgreich gelöscht")
        } catch (e: Exception) {
            Log.e(LogConfig.TAG_DATABASE, "Fehler beim Löschen der Karte", e)
            throw e
        }
    }
}