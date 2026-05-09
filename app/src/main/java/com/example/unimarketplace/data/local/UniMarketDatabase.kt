package com.example.unimarketplace.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.unimarketplace.data.local.dao.AnnuncioDao
import com.example.unimarketplace.data.local.dao.PreferitiDao
import com.example.unimarketplace.data.local.dao.UtenteDao
import com.example.unimarketplace.data.local.entity.AnnuncioEntity
import com.example.unimarketplace.data.local.entity.PreferitiEntity
import com.example.unimarketplace.data.local.entity.UtenteEntity

@Database(
    entities = [UtenteEntity::class, AnnuncioEntity::class, PreferitiEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UniMarketDatabase : RoomDatabase() {

    abstract fun utenteDao(): UtenteDao
    abstract fun annuncioDao(): AnnuncioDao
    abstract fun preferitiDao(): PreferitiDao

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
