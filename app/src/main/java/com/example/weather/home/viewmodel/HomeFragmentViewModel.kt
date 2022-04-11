package com.example.weather.home.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.OneCallWeather
import com.example.weather.model.RepositoryInterface
import com.example.weather.model.Utils
import com.example.weather.model.Utils.getCity
import com.example.weather.model.Weather
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repo: RepositoryInterface, private val context: Context):
    ViewModel(){

    private var weatherMLiveData = MutableLiveData<OneCallWeather>()
    val allWeather: LiveData<OneCallWeather> = weatherMLiveData
//    lateinit var sharedPref: SharedPreferences
//    lateinit var timezone: String

    fun getWeather(
        lat: Float,
        lon: Float,
        language: String,
        unit: String,
        city: String
    ){
//        sharedPref =
//            context.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
        if(Utils.isNetworkAvailable(context)){
            val job = viewModelScope.launch(Dispatchers.IO) {
                val weather = repo.getWeather(lat, lon, language, unit, city)
                weatherMLiveData.postValue(weather)
            }
        }
        else{
            if (city != "null"){
                viewModelScope.launch(Dispatchers.IO){
                    getWeatherFromDB(city)
                }
            }
        }

    }

    private fun getWeatherFromDB(city: String) {
        val weather = repo.getWeatherByCity(city)
        weatherMLiveData.postValue(weather)
    }
}