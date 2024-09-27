package com.craigdietrich.covid19indigenous.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.craigdietrich.covid19indigenous.R
import com.craigdietrich.covid19indigenous.common.Constant
import com.craigdietrich.covid19indigenous.databinding.ActivitySplashBinding
import com.craigdietrich.covid19indigenous.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.changeStatusBar(true, this, color = R.color.colorPrimary)

        Constant.changeSystemNavBarColor(
            context = this,
            color = ContextCompat.getColor(this, R.color.colorPrimary)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}