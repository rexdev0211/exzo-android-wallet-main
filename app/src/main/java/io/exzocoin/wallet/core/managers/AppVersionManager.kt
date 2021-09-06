package io.exzocoin.wallet.core.managers

import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.entities.AppVersion
import io.exzocoin.core.ISystemInfoManager
import java.util.*

class AppVersionManager(
        private val systemInfoManager: ISystemInfoManager,
        private val localStorage: ILocalStorage
) {

    fun storeAppVersion() {
        val versions = localStorage.appVersions.toMutableList()
        val lastVersion = versions.lastOrNull()

        if (lastVersion == null || lastVersion.version != systemInfoManager.appVersion) {
            versions.add(AppVersion(systemInfoManager.appVersion, Date().time))
            localStorage.appVersions = versions
        }
    }

}
