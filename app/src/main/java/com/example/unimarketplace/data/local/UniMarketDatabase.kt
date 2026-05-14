package com.example.unimarketplace.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.unimarketplace.data.local.dao.*
import com.example.unimarketplace.data.local.entity.*

@Database(
    entities = [UtenteEntity::class, AnnuncioEntity::class, PreferitiEntity::class, CarrelloEntity::class, BadgeEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UniMarketDatabase : RoomDatabase() {

    abstract fun utenteDao(): UtenteDao
    abstract fun annuncioDao(): AnnuncioDao
    abstract fun preferitiDao(): PreferitiDao
    abstract fun carrelloDao(): CarrelloDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: UniMarketDatabase? = null

        fun getInstance(context: Context): UniMarketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UniMarketDatabase::class.java,
                    "unimarket_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
