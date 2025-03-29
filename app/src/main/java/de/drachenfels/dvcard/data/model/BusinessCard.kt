package de.drachenfels.dvcard.data.model

import androidx.room.Entity
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
    // Renamed from 'name' to 'familyName' and added 'givenName' for iOS compatibility
    var familyName: String = "",
    var givenName: String = "",
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
    
    var isExpanded: Boolean = false
) {
    /**
     * Generiert einen vollständigen Namen aus Vor- und Nachname
     * @return Vollständiger Name im Format "Vorname Nachname" oder nur Vor- oder Nachname, falls einer leer ist
     */
    fun getFullName(): String {
        return when {
            givenName.isNotEmpty() && familyName.isNotEmpty() -> "$givenName $familyName"
            givenName.isNotEmpty() -> givenName
            familyName.isNotEmpty() -> familyName
            else -> ""
        }
    }
}