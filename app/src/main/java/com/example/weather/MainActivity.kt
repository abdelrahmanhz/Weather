package com.example.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPrefs()
        showDialog()
    }

    private fun showDialog() {
        val dialog = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.choose_loc_dialog)

        // set initial preferences
        val isFirst = preferences.getBoolean(Utils.IS_FIRST_SETTING, true)
        if (isFirst){
            val rbGPS = dialog.findViewById<RadioButton>(R.id.rbGPS)
            val rbMap = dialog.findViewById<RadioButton>(R.id.rbMap)
            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
            dialog.show()
            btnOk.setOnClickListener {
                if (rbGPS.isChecked){
                    Toast.makeText(this, "rbGPS is checked!", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, "rbMap is checked!", Toast.LENGTH_LONG).show()
                }
                editor.putBoolean(Utils.IS_FIRST_SETTING, false)
                editor.commit()
                dialog.dismiss()
                initUI()
            }

        }
        else{
            initUI()
        }
    }

    private fun initPrefs() {
        preferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    private fun initUI() {
        setSupportActionBar(binding.toolbar)
        navController = Navigation.findNavController(this, R.id.host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            binding.drawerLayout
        )
    }
}