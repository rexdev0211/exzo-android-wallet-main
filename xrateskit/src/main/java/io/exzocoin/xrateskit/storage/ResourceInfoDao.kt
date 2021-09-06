package io.exzocoin.xrateskit.storage

import androidx.room.*
import io.exzocoin.xrateskit.entities.ResourceInfo

@Dao
interface ResourceInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResouceInfo(info: ResourceInfo)

    @Query("SELECT * FROM ResourceInfo WHERE id=:resourceName")
    fun getResourceInfo(resourceName: String): ResourceInfo?

}
