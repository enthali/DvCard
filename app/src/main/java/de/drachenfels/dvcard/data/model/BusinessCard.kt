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
    val id: Long = 0,
    
    val name: String = "",
    val position: String = "",
    val company: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    
    val isPrivate: Boolean = false,
    val countryCode: String = ""
)