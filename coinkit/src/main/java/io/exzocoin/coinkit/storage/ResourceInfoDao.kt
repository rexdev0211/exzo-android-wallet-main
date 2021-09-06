package io.exzocoin.coinkit.storage

import androidx.room.*
import io.exzocoin.coinkit.models.Coin
import io.exzocoin.coinkit.models.ResourceInfo

@Dao
interface ResourceInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResouceInfo(info: ResourceInfo)

    @Query("SELECT * FROM ResourceInfo WHERE id=:resourceName")
    fun getResourceInfo(resourceName: String): ResourceInfo?

}
