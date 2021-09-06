package io.exzocoin.xrateskit.rates

import io.exzocoin.xrateskit.core.ILatestRateProvider
import io.exzocoin.xrateskit.scheduler.Scheduler

class LatestRatesSchedulerFactory(
    private val manager: LatestRatesManager,
    private val provider: ILatestRateProvider,
    private val expirationInterval: Long,
    private val retryInterval: Long) {

    fun getScheduler(currencyCode: String, coinTypeDataSource: ILatestRatesCoinTypeDataSource): Scheduler {
        val schedulerProvider = LatestRatesSchedulerProvider(
            manager,
            provider,
            currencyCode,
            expirationInterval,
            retryInterval
        )
        schedulerProvider.dataSource = coinTypeDataSource

        return Scheduler(schedulerProvider, 5)
    }
}
