package de.drachenfels.dvcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig

/**
 * Migration von Version 1 zu Version 2
 * 
 * Fügt das neue 'title'-Feld zur business_cards-Tabelle hinzu
 */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Fügt die neue Spalte 'title' mit Standardwert '' hinzu
        db.execSQL("ALTER TABLE business_cards ADD COLUMN title TEXT NOT NULL DEFAULT ''")
    }
}

/**
 * Migration von Version 2 zu Version 3
 *
 * Fügt das 'isExpanded'-Feld zur business_cards-Tabelle hinzu
 */
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Fügt die neue Spalte 'isExpanded' mit Standardwert 0 (false) hinzu
        db.execSQL("ALTER TABLE business_cards ADD COLUMN isExpanded INTEGER NOT NULL DEFAULT 0")
    }
}

/**
 * Migration von Version 3 zu Version 4
 *
 * Benennt das 'name'-Feld zu 'familyName' um und fügt das neue 'givenName'-Feld hinzu
 */
private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Schritt 1: Temporäre Tabelle erstellen mit der neuen Struktur
        db.execSQL("""
            CREATE TABLE business_cards_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL DEFAULT '',
                familyName TEXT NOT NULL DEFAULT '',
                givenName TEXT NOT NULL DEFAULT '',
                position TEXT NOT NULL DEFAULT '',
                company TEXT NOT NULL DEFAULT '',
                phone TEXT NOT NULL DEFAULT '',
                email TEXT NOT NULL DEFAULT '',
                website TEXT NOT NULL DEFAULT '',
                street TEXT NOT NULL DEFAULT '',
                postalCode TEXT NOT NULL DEFAULT '',
                city TEXT NOT NULL DEFAULT '',
                country TEXT NOT NULL DEFAULT '',
                isPrivate INTEGER NOT NULL DEFAULT 0,
                isExpanded INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Schritt 2: Daten von der alten Tabelle in die neue Tabelle kopieren,
        // wobei 'name' in 'familyName' umbenannt wird
        db.execSQL("""
            INSERT INTO business_cards_temp (
                id, title, familyName, position, company, 
                phone, email, website, street, postalCode, 
                city, country, isPrivate, isExpanded
            ) 
            SELECT 
                id, title, name, position, company, 
                phone, email, website, street, postalCode, 
                city, country, isPrivate, isExpanded 
            FROM business_cards
        """)
        
        // Schritt 3: Alte Tabelle löschen
        db.execSQL("DROP TABLE business_cards")
        
        // Schritt 4: Neue Tabelle umbenennen
        db.execSQL("ALTER TABLE business_cards_temp RENAME TO business_cards")
    }
}

/**
 * Room-Datenbank für die App
 *
 * Zentrale Datenbank, die alle Visitenkarten speichert.
 */
@Database(entities = [BusinessCard::class], version = 4, exportSchema = false)
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
            Log.d(LogConfig.TAG_DATABASE, "getDatabase aufgerufen")
            
            val dbFile = context.getDatabasePath("business_card_database")
            Log.d(LogConfig.TAG_DATABASE, "Datenbank-Pfad: ${dbFile.absolutePath}")
            
            // Prüfen, ob die Datenbank existiert
            val exists = dbFile.exists()
            Log.d(LogConfig.TAG_DATABASE, "Datenbank existiert: $exists")
            if (exists) {
                Log.d(LogConfig.TAG_DATABASE, "Datenbank-Größe: ${dbFile.length()} Bytes")
            }
            
            return INSTANCE ?: synchronized(this) {
                Log.d(LogConfig.TAG_DATABASE, "Erstelle neue Datenbankinstanz")
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        BusinessCardDatabase::class.java,
                        "business_card_database"
                    )
                    // Migration hinzufügen, um bestehende Daten zu erhalten
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    // Fallback nur als letzte Option
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE) // Sofortiges Committen nach Transaktionen
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d(LogConfig.TAG_DATABASE, "Callback: Datenbank erstellt")
                            // PRAGMA-Befehle entfernt, da sie Probleme verursachen
                        }
                        
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d(LogConfig.TAG_DATABASE, "Callback: Datenbank geöffnet")
                        }
                    })
                    .build()
                    INSTANCE = instance
                    Log.d(LogConfig.TAG_DATABASE, "Datenbank-Instanz erfolgreich erstellt")
                    
                    // Erneut prüfen, ob die Datei existiert
                    val nowExists = dbFile.exists()
                    Log.d(LogConfig.TAG_DATABASE, "Datenbank existiert nach Erstellung: $nowExists")
                    if (nowExists) {
                        Log.d(LogConfig.TAG_DATABASE, "Datenbank-Größe nach Erstellung: ${dbFile.length()} Bytes")
                    }
                    
                    instance
                } catch (e: Exception) {
                    Log.e(LogConfig.TAG_DATABASE, "Fehler beim Erstellen der Datenbank", e)
                    throw e
                }
            }
        }
    }
}