package io.exzocoin.coinkit.storage

import androidx.room.TypeConverter
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.coinkit.models.ResourceType

class DatabaseConverters {

   // CoinType
    @TypeConverter
    fun toString(chartType: CoinType): String {
        return chartType.getCoinId()
    }

    @TypeConverter
    fun toCoinType(value: String): CoinType {
        return CoinType.fromString(value)
    }

    @TypeConverter
    fun toString(resourceType: ResourceType): String {
        return resourceType.name
    }

    @TypeConverter
    fun toResourceType(value: String): ResourceType {
        return ResourceType.valueOf(value)
    }
}
