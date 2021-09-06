package io.exzocoin.wallet.core.storage

import androidx.room.*
import io.exzocoin.wallet.entities.SubscriptionJob

@Dao
interface SubscriptionJobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(subscriptionJob: SubscriptionJob)

    @Query("SELECT * FROM SubscriptionJob")
    fun all(): List<SubscriptionJob>

    @Delete()
    fun delete(subscriptionJob: SubscriptionJob)

}
