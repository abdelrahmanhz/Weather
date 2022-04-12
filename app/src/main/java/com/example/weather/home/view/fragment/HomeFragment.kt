package com.example.weather.home.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.home.view.adapter.DaysAdapter
import com.example.weather.home.view.adapter.HoursAdapter
import com.example.weather.home.viewmodel.HomeFragmentViewModel
import com.example.weather.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weather.model.OneCallWeather
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.model.Utils.convertNumberToAR
import com.example.weather.model.Utils.dateFormat
import com.example.weather.model.Utils.getCity
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import okhttp3.internal.Util


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var longitude: Float = 0.0f
    private var latitude: Float = 0.0f
    private lateinit var lang: String
    private lateinit var unit: String
    private lateinit var city: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        latitude = sharedPreferences.getFloat(Utils.LATITUDE_SETTING, 0F)
        longitude = sharedPreferences.getFloat(Utils.LONGITUDE_SETTING, 0f)
        city = sharedPreferences.getString(Utils.CITY_SETTING, "empty").toString()
        lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
        unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric").toString()
        editor.putInt(Utils.MAP_SETTING, 0)
        binding.rvDays.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvDays.hasFixedSize()
        binding.homeFragmentLayout.isVisible = false
        binding.progressBar.isVisible = true
        binding.rvHours.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rvHours.hasFixedSize()

        viewModel = ViewModelProvider(this,
        HomeFragmentViewModelFactory(Repository.getRepoInstance(requireActivity().application),
        requireContext()))[HomeFragmentViewModel::class.java]
        getWeatherForDisplay()
    }

    private fun getWeatherForDisplay() {
        if(!Utils.isNetworkAvailable(requireContext())){
            Snackbar.make(binding.homeRoot,"No internet connection", Snackbar.LENGTH_LONG)
                .setAction("connect") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(panelIntent)
                    }
                    else{
                        val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(wifiIntent)
                    }
                }
                .show()
        }
        viewModel.getWeather(latitude, longitude, lang, unit, city)
        viewModel.allWeather.observe(viewLifecycleOwner){
            binding.progressBar.isVisible = false
            binding.homeFragmentLayout.isVisible = true

            setData(it)
            Log.i("TAG", "getWeatherForDisplay: $it")
        }
    }

    private fun setData(weather: OneCallWeather?) {

        val cityName = LatLng(weather!!.lat, weather.lon).getCity(requireContext(), lang)
        if(!cityName.isNullOrEmpty())
            binding.cityName.text = cityName
        else
            binding.cityName.text = weather.city
        binding.weatherDate.text = weather.current.dt.dateFormat(lang)
        binding.weatherCondition.text = weather.current.weather[0].description
        binding.temperature.text = weather.current.temp.toInt().convertNumberToAR(lang)
        var windUnitSymbol: String? = null
        when (unit) {
            "metric" -> windUnitSymbol = "°c"
            "imperial" -> windUnitSymbol = "°f"
            "standard" -> windUnitSymbol = "°k"
        }
        binding.tempratureUnit.text = windUnitSymbol

        binding.cloudValue.text = (weather.current.clouds.convertNumberToAR(lang) + " %")
        binding.pressureValue.text = (weather.current.pressure.convertNumberToAR(lang) + " hpa")
        binding.humidityValue.text = (weather.current.humidity.convertNumberToAR(lang) + " %")
        var windUnit: String? = null
        when (unit) {
            "imperial" -> windUnit = " m/h"
            "metric" -> windUnit = " m/s"
            "standard" -> windUnit = " m/s"
        }
        binding.windValue.text =
            (weather.current.wind_speed.toInt().convertNumberToAR(lang) + windUnit)

        binding.txtVisibilityValue.text = (weather.current.visibility.convertNumberToAR(lang) + " m")
        binding.ultraVioletteValue.text = (weather.current.uvi.toInt().convertNumberToAR(lang))

        binding.weatherIcon.setImageResource(Utils.getIconId(weather.current.weather[0].icon))

        val daysAdapter = DaysAdapter(weather.daily, binding.cityName.context)
        binding.rvDays.adapter = daysAdapter

        val hoursAdapter = HoursAdapter(weather.hourly, binding.cityName.context)
        binding.rvHours.adapter = hoursAdapter
    }
}