package io.exzocoin.wallet.modules.main

import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.wallet.core.utils.RootUtil

class MainService(
        private val rootUtil: RootUtil,
        private val localStorage: ILocalStorage
) {

    val isDeviceRooted: Boolean
        get() = rootUtil.isRooted

    val ignoreRootCheck: Boolean
        get() = localStorage.ignoreRootedDeviceWarning

}
