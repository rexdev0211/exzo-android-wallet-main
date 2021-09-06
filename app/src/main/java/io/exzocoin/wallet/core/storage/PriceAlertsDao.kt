package io.exzocoin.wallet.core.storage

import androidx.room.*
import io.exzocoin.wallet.entities.PriceAlert
import io.exzocoin.coinkit.models.CoinType

@Dao
interface PriceAlertsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(priceAlert: PriceAlert)

    @Query("SELECT * FROM PriceAlert")
    fun all(): List<PriceAlert>

    @Query("SELECT * FROM PriceAlert WHERE coinType = :coinType")
    fun priceAlert(coinType: CoinType): PriceAlert?

    @Query("SELECT COUNT(*) FROM PriceAlert")
    fun count(): Int

    @Query("DELETE FROM PriceAlert")
    fun deleteAll()

    @Delete()
    fun delete(it: PriceAlert)

}
