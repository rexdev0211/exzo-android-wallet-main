package io.exzocoin.wallet.modules.launcher

import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import io.exzocoin.wallet.modules.splash.SplashActivity
import io.exzocoin.pin.PinModule
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.modules.intro.IntroActivity
import io.exzocoin.wallet.modules.keystore.KeyStoreActivity
import io.exzocoin.wallet.modules.lockscreen.LockScreenActivity
import io.exzocoin.wallet.modules.main.MainModule
import io.exzocoin.wallet.modules.tor.TorConnectionActivity

class LauncherActivity : AppCompatActivity() {

    private lateinit var viewModel: LaunchViewModel

    private val unlockResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            PinModule.RESULT_OK -> viewModel.delegate.didUnlock()
            PinModule.RESULT_CANCELLED -> viewModel.delegate.didCancelUnlock()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SplashActivity.start(this);

/*
        viewModel = ViewModelProvider(this).get(LaunchViewModel::class.java)
        viewModel.init()

        viewModel.openWelcomeModule.observe(this, Observer {
            IntroActivity.start(this)
            finish()
        })

        viewModel.openMainModule.observe(this, Observer {
            MainModule.start(this)
            if(App.localStorage.torEnabled) {
                val intent = Intent(this, TorConnectionActivity::class.java)
                startActivity(intent)
            }
            finish()
        })

        viewModel.openUnlockModule.observe(this, Observer {
            val intent = Intent(this, LockScreenActivity::class.java)
            unlockResultLauncher.launch(intent)
        })

        viewModel.openNoSystemLockModule.observe(this, Observer {
            KeyStoreActivity.startForNoSystemLock(this)
        })

        viewModel.openKeyInvalidatedModule.observe(this, Observer {
            KeyStoreActivity.startForInvalidKey(this)
        })

        viewModel.openUserAuthenticationModule.observe(this, Observer {
            KeyStoreActivity.startForUserAuthentication(this)
        })

        viewModel.closeApplication.observe(this, Observer {
            finishAffinity()
        })*/
    }

}
