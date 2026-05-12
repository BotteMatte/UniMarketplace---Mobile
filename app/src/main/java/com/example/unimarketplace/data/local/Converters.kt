package com.example.unimarketplace.data.local

import androidx.room.TypeConverter
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni

class Converters {

    @TypeConverter
    fun categoriaToString(categoria: Categoria?): String? {
        return categoria?.name
    }

    @TypeConverter
    fun stringToCategoria(value: String?): Categoria {
        return value?.let {
            try {
                Categoria.valueOf(it)
            } catch (e: IllegalArgumentException) {
                Categoria.ALTRO
            }
        } ?: Categoria.ALTRO
    }

    @TypeConverter
    fun condizioniToString(condizioni: Condizioni?): String? {
        return condizioni?.name
    }

    @TypeConverter
    fun stringToCondizioni(value: String?): Condizioni {
        return value?.let {
            try {
                Condizioni.valueOf(it)
            } catch (e: IllegalArgumentException) {
                Condizioni.USATO
            }
        } ?: Condizioni.USATO
    }

    @TypeConverter
    fun stringToList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
}
