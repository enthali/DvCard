package de.drachenfels.dvcard.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Datenmodell für eine Visitenkarte
 *
 * Diese Klasse repräsentiert eine digitale Visitenkarte und wird direkt als 
 * Room-Entity für die Datenbank verwendet.
 */
@Entity(tableName = "business_cards")
data class BusinessCard(
    @PrimaryKey(autoGenerate = true) 
    var id: Long = 0,
    
    var title: String = "", // Neues Feld für den Titel der Karte
    var name: String = "",
    var position: String = "",
    var company: String = "",
    var phone: String = "",
    var email: String = "",
    var website: String = "",

    var street: String = "",
    var postalCode: String = "",
    var city: String = "",
    var country: String = "",
    
    var isPrivate: Boolean = false,
    
    // UI-Zustand, nicht in Datenbank gespeichert
    @Ignore
    var isExpanded: Boolean = false
) {
    /**
     * Erzeugt eine Kopie dieser Karte mit umgeschaltetem isExpanded-Status
     */
    fun toggleExpanded(): BusinessCard = copy(isExpanded = !isExpanded)
    
    /**
     * Erzeugt eine Kopie dieser Karte mit explizitem isExpanded-Status
     */
    fun withExpanded(expanded: Boolean): BusinessCard = copy(isExpanded = expanded)
}