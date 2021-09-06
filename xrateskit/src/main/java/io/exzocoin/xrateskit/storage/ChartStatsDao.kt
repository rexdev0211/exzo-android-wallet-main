package io.exzocoin.xrateskit.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.xrateskit.entities.ChartPointEntity
import io.exzocoin.xrateskit.entities.ChartType

@Dao
interface ChartStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stats: List<ChartPointEntity>)

    @Query("DELETE FROM ChartPointEntity WHERE coinType = :coinType AND currency = :currency AND type = :chartType")
    fun delete(coinType: CoinType, currency: String, chartType: ChartType)

    @Query("SELECT * FROM ChartPointEntity WHERE coinType = :coinType AND currency = :currency AND type = :type ORDER BY timestamp DESC LIMIT 1")
    fun getLast(coinType: CoinType, currency: String, type: ChartType): ChartPointEntity?

    @Query("SELECT * FROM ChartPointEntity WHERE coinType = :coinType AND currency = :currency AND type = :type ORDER BY timestamp ASC")
    fun getList(coinType: CoinType, currency: String, type: ChartType): List<ChartPointEntity>

}
