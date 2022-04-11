package com.example.weather

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.weather.model.Utils

class SplashActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preferences = getSharedPreferences(Utils.PREFS_FILE_NAME, MODE_PRIVATE)

        Handler().postDelayed({
            if(preferences.getBoolean(Utils.IS_FIRST_SETTING, true))
                startActivity(Intent(this,MapActivity::class.java))
            else
                startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, 3000L)
    }
}