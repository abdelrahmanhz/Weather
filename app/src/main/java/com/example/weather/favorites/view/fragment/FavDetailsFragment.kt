package com.example.weather.favorites.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentFavDetailsBinding
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.favorites.viewmodel.FavoritesViewModel
import com.example.weather.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weather.home.view.adapter.DaysAdapter
import com.example.weather.home.view.adapter.HoursAdapter
import com.example.weather.home.viewmodel.HomeFragmentViewModel
import com.example.weather.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weather.model.OneCallWeather
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.model.Utils.convertNumberToAR
import com.example.weather.model.Utils.dateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavDetailsFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var lang: String
    private lateinit var unit: String
    private val args: FavDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPrefs()

        binding.homeFragmentLayout.isVisible = false
        binding.progressBar.isVisible = true

        binding.rvDays.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvDays.hasFixedSize()

        binding.rvHours.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rvHours.hasFixedSize()

        viewModel =
            ViewModelProvider(
                this,
                FavoritesViewModelFactory(Repository.getRepoInstance(requireContext()),
                    requireContext())
            )[FavoritesViewModel::class.java]
        lifecycleScope.launch(Dispatchers.IO){
            binding.homeFragmentLayout.isVisible = true
            binding.progressBar.isVisible = false
            setData(viewModel.getWeatherByCity(args.cityName))
        }
    }

    private fun setData(weather: OneCallWeather) {
        binding.cityName.text = weather!!.city
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


    private fun initPrefs(){
        sharedPreferences = activity?.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
        unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric").toString()
    }
}