package io.exzocoin.wallet.modules.auth

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.AppLogger
import io.exzocoin.wallet.core.BaseActivity
import io.exzocoin.wallet.entities.Wallet
import io.exzocoin.wallet.modules.send.SendActivity

class AuthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null) // null prevents fragments restoration on theme switch

        setContentView(R.layout.activity_auth)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navHost.navController.setGraph(R.navigation.auth_graph, intent.extras)
        navHost.navController.addOnDestinationChangedListener(this)
    }

    override fun onTrimMemory(level: Int) {
        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                if (App.backgroundManager.inBackground) {
                    val logger = AppLogger("low memory")
                    logger.info("Kill activity due to low memory, level: $level")
                    finishAffinity()
                }
            }
            else -> {  /*do nothing*/
            }
        }

        super.onTrimMemory(level)
    }

    companion object {
        const val ACTIVE_TAB_KEY = "active_tab"
        const val SETTINGS_TAB_POSITION = 3
    }
}
