package com.example.weather.settings.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weather.MainActivity
import com.example.weather.MapActivity
import com.example.weather.R
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.model.Utils.isNetworkAvailable
import com.example.weather.settings.viewmodel.SettingsViewModel
import com.example.weather.settings.viewmodel.SettingsViewModelFactory
import java.util.*

class SettingsFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var binding: FragmentSettingsBinding
    lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPrefs()

        viewModel =
            ViewModelProvider(
                this,
                SettingsViewModelFactory(Repository.getRepoInstance(requireActivity().application))
            )[SettingsViewModel::class.java]

        val lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en")
        val unit = sharedPreferences.getString(Utils.PREFS_FILE_NAME, "metric")

        when (lang) {
            "en" -> binding.englishRadio.isChecked = true
            "ar" -> binding.arabicRadio.isChecked = true
        }

        when (unit) {
            "metric" -> binding.celsiusRadio.isChecked = true
            "imperial" -> binding.fahrenheitRadio.isChecked = true
            "standard" -> binding.kelvinRadio.isChecked = true
        }

        binding.mapRadio.isChecked = true

        binding.arabicRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                changeLang("ar")
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        binding.englishRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                changeLang("en")
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.celsiusRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                changeUnite("metric")
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        binding.kelvinRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                changeUnite("standard")
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        binding.fahrenheitRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                changeUnite("imperial")
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.mapRadio.setOnClickListener(View.OnClickListener {
            if (isNetworkAvailable(requireContext())) {
                val refresh = Intent(requireContext(), MapActivity::class.java)
                activity?.finish()
                startActivity(refresh)
            } else {
                Toast.makeText(
                    requireContext(),
                    "no internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun changeUnite(unit: String) {
        editor.putString(Utils.UNIT_SETTING, unit)
        viewModel.refreshData()
        editor.apply()
    }


    private fun changeLang(lang: String) {
        editor.putString(Utils.LANGUAGE_SETTING, lang)
        editor.apply()
        setLocale(lang)
        viewModel.refreshData()

    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources: Resources = requireActivity().resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        Locale.setDefault(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val refresh = Intent(requireContext(), MainActivity::class.java)
        activity?.finish()
        startActivity(refresh)
    }

    private fun initPrefs() {
        sharedPreferences = activity?.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
    }

}