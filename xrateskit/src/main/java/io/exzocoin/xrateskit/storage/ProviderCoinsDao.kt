package io.exzocoin.xrateskit.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.xrateskit.entities.ProviderCoinEntity

@Dao
interface ProviderCoinsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(all: List<ProviderCoinEntity>)

    @Query("SELECT * FROM ProviderCoinEntity WHERE coinType  IN (:coinTypes)")
    fun getProviderCoins(coinTypes: List<CoinType>): List<ProviderCoinEntity>

    @Query("SELECT * FROM ProviderCoinEntity WHERE coinType = :coinType")
    fun getProviderCoin(coinType: CoinType): ProviderCoinEntity?

    @Query("SELECT coinType FROM ProviderCoinEntity WHERE cryptocompareId =:providerCoinId")
    fun getCoinTypesForCryptoCompare(providerCoinId: String): List<CoinType>

    @Query("SELECT coinType FROM ProviderCoinEntity WHERE coingeckoId =:providerCoinId")
    fun getCoinTypesForCoinGecko(providerCoinId: String): List<CoinType>

    @Query("""
        SELECT * FROM ProviderCoinEntity 
        WHERE code LIKE '%'||:searchText||'%'  OR name LIKE '%'||:searchText||'%' 
        ORDER BY (code LIKE :searchText ) DESC,
                 (name LIKE :searchText ) DESC,
                  priority ASC 
    """)
    fun searchCoins(searchText: String): List<ProviderCoinEntity>

    @Query("UPDATE ProviderCoinEntity SET priority = :priority")
    fun resetPriorities(priority: Int)

    @Query("UPDATE ProviderCoinEntity SET priority = :priority WHERE coinType = :coinType")
    fun setPriorityForCoin(coinType: CoinType, priority: Int)
}