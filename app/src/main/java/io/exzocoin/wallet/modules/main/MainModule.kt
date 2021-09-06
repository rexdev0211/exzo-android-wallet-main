package io.exzocoin.wallet.modules.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.utils.RootUtil

object MainModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = MainService(RootUtil, App.localStorage)
            return MainViewModel(App.pinComponent, App.rateAppManager, App.backupManager, App.termsManager, App.accountManager, App.releaseNotesManager, service) as T
        }
    }

    fun start(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(intent)
    }

    fun startAsNewTask(context: Activity, activeTab: Int? = null) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activeTab?.let {
            intent.putExtra(MainActivity.ACTIVE_TAB_KEY, it)
        }
        context.startActivity(intent)
        context.overridePendingTransition(0, 0)
    }
}
