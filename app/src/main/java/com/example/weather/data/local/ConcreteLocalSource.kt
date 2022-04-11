package com.example.weather.data.local

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weather.model.OneCallWeather

class ConcreteLocalSource(private val weatherDao: WeatherDao): LocalSource {
    override suspend fun insert(weather: OneCallWeather) {
        weatherDao.insert(weather)
    }

    override fun getWeatherByCity(city: String): OneCallWeather {
        return weatherDao.getWeatherByCity(city)
    }

    override fun deleteByCity(city: String) {
        weatherDao.deleteByCity(city)
    }

    override fun getHomeWeather(): OneCallWeather {
        return weatherDao.getHomeWeather()
    }

    override fun getFavorites(): List<OneCallWeather> {
        Log.i("TAG", "ConcreteLocalSource getFavorites()")
        Log.i("TAG", "ConcreteLocalSource ${weatherDao.getFavorites()}")
        return weatherDao.getFavorites()
    }

    override fun deleteHomeWeather() {
        weatherDao.deleteHomeWeather()
    }
}