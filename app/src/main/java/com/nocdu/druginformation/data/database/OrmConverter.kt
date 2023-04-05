package com.nocdu.druginformation.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrmConverter {
    @TypeConverter
    fun fromList(value:List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value:String) = Json.decodeFromString<List<String>>(value)

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toIntList(str: String): List<Int> {
        return if (str.isBlank()) {
            emptyList()
        } else {
            str.split(",").map { it.toInt() }
        }
    }
}