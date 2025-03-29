package de.drachenfels.dvcard.data

import androidx.room.*
import de.drachenfels.dvcard.data.model.BusinessCard
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für BusinessCard-Entitäten
 *
 * Definiert die grundlegenden CRUD-Operationen für Visitenkarten in der Datenbank.
 */
@Dao
interface BusinessCardDao {
    /**
     * Gibt alle gespeicherten Visitenkarten zurück, als Flow
     * für reaktive Updates in der UI.
     */
    @Query("SELECT * FROM business_cards ORDER BY familyName ASC, givenName ASC")
    fun getAllCards(): Flow<List<BusinessCard>>
    
    /**
     * Gibt die Anzahl der Karten in der Datenbank zurück.
     */
    @Query("SELECT COUNT(*) FROM business_cards")
    suspend fun getCardCount(): Int
    
    /**
     * Sucht eine spezifische Karte anhand der ID.
     */
    @Query("SELECT * FROM business_cards WHERE id = :id")
    suspend fun getCardById(id: Long): BusinessCard?
    
    /**
     * Fügt eine neue Visitenkarte in die Datenbank ein und gibt die
     * generierte ID zurück.
     */
    @Insert
    suspend fun insertCard(card: BusinessCard): Long
    
    /**
     * Aktualisiert eine bestehende Visitenkarte.
     */
    @Update
    suspend fun updateCard(card: BusinessCard)
    
    /**
     * Löscht eine Visitenkarte aus der Datenbank.
     */
    @Delete
    suspend fun deleteCard(card: BusinessCard)
}