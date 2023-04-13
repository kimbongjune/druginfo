package com.nocdu.druginformation.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 *  Room 라이브러리에서 사용할 OrmConverter 클래스
 *  데이터베이스 기본타입이 아닌 타입을 기본타입으로 변환하기 위해 사용한다.
 */
class OrmConverter {
    //인서트시 List<String> 타입을 String 타입으로 변환한다.
    @TypeConverter
    fun fromList(value:List<String>) = Json.encodeToString(value)

    //셀렉트시 String 타입을 List<String> 타입으로 변환한다.
    @TypeConverter
    fun toList(value:String) = Json.decodeFromString<List<String>>(value)

    //인서트시 List<Int> 타입을 String 타입으로 변환한다.
    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return list.joinToString(",")
    }

    //셀렉트시 String 타입을 List<Int> 타입으로 변환한다.
    @TypeConverter
    fun toIntList(str: String): List<Int> {
        return if (str.isBlank()) {
            emptyList()
        } else {
            str.split(",").map { it.toInt() }
        }
    }
}