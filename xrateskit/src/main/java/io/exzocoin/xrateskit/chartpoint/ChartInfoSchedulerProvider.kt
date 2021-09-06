package io.exzocoin.xrateskit.chartpoint

import io.exzocoin.xrateskit.providers.ProviderError
import io.exzocoin.xrateskit.core.IChartInfoProvider
import io.exzocoin.xrateskit.entities.ChartInfoKey
import io.exzocoin.xrateskit.scheduler.ISchedulerProvider
import io.reactivex.Single

class ChartInfoSchedulerProvider(
    override val retryInterval: Long,
    private val key: ChartInfoKey,
    private val provider: IChartInfoProvider,
    private val manager: ChartInfoManager
) : ISchedulerProvider {

    override val id = key.toString()

    override val lastSyncTimestamp: Long?
        get() = manager.getLastSyncTimestamp(key)

    override val expirationInterval: Long
        get() = key.chartType.seconds

    override val syncSingle: Single<Unit>
        get() = provider.getChartPointsAsync(key)
            .doOnSuccess { points ->
                manager.update(points, key)
            }
            .doOnError {
                if (it is ProviderError.NoDataForCoin) {
                    manager.handleNoChartPoints(key)
                }
            }
            .map { Unit }

    override fun notifyExpired() = Unit

}
