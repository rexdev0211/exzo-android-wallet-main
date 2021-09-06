package io.exzocoin.xrateskit.rates

import io.exzocoin.coinkit.models.CoinType

interface ILatestRatesCoinTypeDataSource {
    fun getCoinTypes(currencyCode: String): List<CoinType>
}
