package io.exzocoin.wallet.core.managers

import android.util.Log
import io.exzocoin.wallet.core.INotificationSubscriptionManager
import io.exzocoin.wallet.core.notifications.NotificationNetworkWrapper
import io.exzocoin.wallet.core.storage.AppDatabase
import io.exzocoin.wallet.entities.SubscriptionJob
import kotlinx.coroutines.*

class NotificationSubscriptionManager(
        appDatabase: AppDatabase,
        private val notificationNetworkWrapper: NotificationNetworkWrapper
) : INotificationSubscriptionManager {

    private val dao = appDatabase.subscriptionJobDao()

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    override fun processJobs() {
        ioScope.launch {
            val jobs = dao.all()
            jobs.forEach {
                processJob(it)
            }
        }
    }

    override fun addNewJobs(jobs: List<SubscriptionJob>) {
        ioScope.launch {
            jobs.forEach {
                dao.save(it)
                processJob(it)
            }
        }
    }

    private suspend fun processJob(subscriptionJob: SubscriptionJob) {
        try {
            notificationNetworkWrapper.processSubscription(subscriptionJob.jobType, subscriptionJob.body)
            dao.delete(subscriptionJob)
        } catch (e: Exception){
            Log.e("NotifSubscrManager", "subscribe error", e)
        }
    }

}
