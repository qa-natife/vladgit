package com.natife.voobrazharium.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.natife.voobrazharium.R
import com.natife.voobrazharium.init_game.InitGameActivity.Companion.start

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            start(this@SplashActivity)
            finish()
        }, 500)
    }


    override fun onBackPressed() {}
}