package com.example.storyin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyin.R
import com.example.storyin.data.preferences.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        userPreference = UserPreference.getInstance(this)

        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, SPLASH_DELAY)
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val token = userPreference.getToken().first()
            val intent = if (token.isNotEmpty()) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, AuthOptionActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val SPLASH_DELAY: Long = 2000
    }
}