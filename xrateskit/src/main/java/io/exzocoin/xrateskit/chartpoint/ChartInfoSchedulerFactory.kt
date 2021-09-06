package io.exzocoin.xrateskit.chartpoint

import io.exzocoin.xrateskit.core.IChartInfoProvider
import io.exzocoin.xrateskit.entities.ChartInfoKey
import io.exzocoin.xrateskit.scheduler.Scheduler

class ChartInfoSchedulerFactory(
        private val manager: ChartInfoManager,
        private val provider: IChartInfoProvider,
        private val retryInterval: Long) {

    fun getScheduler(key: ChartInfoKey): Scheduler {
        return Scheduler(ChartInfoSchedulerProvider(retryInterval, key, provider, manager))
    }
}
