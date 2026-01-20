package com.nocdu.druginformation.data.database

import androidx.room.TypeConverter
import com.nocdu.druginformation.data.model.IngredientInfo
import com.nocdu.druginformation.data.model.PriceInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 *  Room 라이브러리에서 사용할 OrmConverter 클래스
 *  데이터베이스 기본타입이 아닌 타입을 기본타입으로 변환하기 위해 사용한다.
 */
class OrmConverter {
    
    private val json = Json { ignoreUnknownKeys = true }
    
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

    // === PriceInfo 변환 ===
    @TypeConverter
    fun fromPriceInfo(priceInfo: PriceInfo?): String? {
        return priceInfo?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toPriceInfo(value: String?): PriceInfo? {
        return value?.let { json.decodeFromString(it) }
    }

    // === List<IngredientInfo> 변환 ===
    @TypeConverter
    fun fromIngredientInfoList(ingredients: List<IngredientInfo>?): String? {
        return ingredients?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toIngredientInfoList(value: String?): List<IngredientInfo>? {
        return value?.let { json.decodeFromString(it) }
    }
}