package de.drachenfels.dvcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.drachenfels.dvcard.data.model.BusinessCard

/**
 * Room-Datenbank für die App
 *
 * Zentrale Datenbank, die alle Visitenkarten speichert.
 */
@Database(entities = [BusinessCard::class], version = 1, exportSchema = false)
abstract class BusinessCardDatabase : RoomDatabase() {
    /**
     * Liefert das DAO für den Zugriff auf die Visitenkarten.
     */
    abstract fun businessCardDao(): BusinessCardDao
    
    companion object {
        // Singleton-Instanz der Datenbank
        @Volatile
        private var INSTANCE: BusinessCardDatabase? = null
        
        /**
         * Erstellt eine neue Instanz der Datenbank oder gibt die
         * bestehende Instanz zurück.
         */
        fun getDatabase(context: Context): BusinessCardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BusinessCardDatabase::class.java,
                    "business_card_database"
                )
                .fallbackToDestructiveMigration() // Für einfache Schemamigration in der Entwicklung
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}