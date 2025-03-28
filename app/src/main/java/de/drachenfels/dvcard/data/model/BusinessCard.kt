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
    // TODO: seperate familiy name and given name otherwise ios devices will copy the full name into the family name field..
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
    
    var isExpanded: Boolean = false
)