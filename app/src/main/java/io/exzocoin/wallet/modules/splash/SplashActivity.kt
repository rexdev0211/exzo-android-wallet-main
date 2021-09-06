package io.exzocoin.wallet.modules.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import io.exzocoin.wallet.R
import io.exzocoin.wallet.modules.auth.AuthActivity
import io.exzocoin.wallet.modules.intro.IntroActivity

class SplashActivity : AppCompatActivity() {
    private var TIME_OUT:Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        loadMainScreen()
    }

    private fun loadMainScreen() {
        Handler().postDelayed({
            var intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_OUT)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            context.startActivity(intent)
        }
    }
}