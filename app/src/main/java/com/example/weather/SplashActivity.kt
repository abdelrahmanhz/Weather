package com.example.weather

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.weather.model.Utils
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preferences = getSharedPreferences(Utils.PREFS_FILE_NAME, MODE_PRIVATE)
        val lang = preferences.getString(Utils.LANGUAGE_SETTING, "en")!!
        setLocale(lang)
        
        Handler().postDelayed({
            if(preferences.getBoolean(Utils.IS_FIRST_SETTING, true))
                startActivity(Intent(this,MapActivity::class.java))
            else
                startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, 3000L)
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        Locale.setDefault(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}