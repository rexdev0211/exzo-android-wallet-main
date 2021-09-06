package io.exzocoin.wallet.modules.send.zcash

import io.exzocoin.wallet.core.AppLogger
import io.exzocoin.wallet.core.ISendZcashAdapter
import io.exzocoin.wallet.modules.send.SendModule
import io.reactivex.Single
import java.math.BigDecimal

class SendZcashInteractor(
        private val adapter: ISendZcashAdapter
) : SendModule.ISendZcashInteractor {

    override val availableBalance: BigDecimal
        get() = adapter.availableBalance

    override val fee: BigDecimal
        get() = adapter.fee

    override fun validate(address: String) {
        adapter.validate(address)
    }

    override fun send(amount: BigDecimal, address: String, memo: String?, logger: AppLogger): Single<Unit> {
        return adapter.send(amount, address, memo ?: "", logger)
    }

}
