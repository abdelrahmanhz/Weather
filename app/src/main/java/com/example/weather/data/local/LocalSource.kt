package com.example.weather.data.local

import com.example.weather.model.OneCallWeather

interface LocalSource {

    suspend fun insert(oneCallWeather: OneCallWeather)
    fun getWeatherByCity(city: String): OneCallWeather
    fun deleteByCity(city :String)
    fun getHomeWeather(): OneCallWeather
    fun getFavorites(): List<OneCallWeather>
    fun deleteHomeWeather()
}