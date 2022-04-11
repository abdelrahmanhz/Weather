package com.example.weather.model

interface RepositoryInterface {

    suspend fun getWeather(
        lat: Float, lon: Float,
        language: String ,
        units:String,
        city: String
    ):OneCallWeather

    suspend fun insertWeather(weatherModel: OneCallWeather)
    fun getWeatherByCity(city: String): OneCallWeather
    fun deleteByCity(city :String)
    fun getHomeWeather(): OneCallWeather
    fun getFavorites(): List<OneCallWeather>
    fun refrechData()
    suspend fun getFavWeather(
        lat: Float,
        lon: Float,
        language: String,
        units: String,
        city: String
    )

    fun deleteHomeWeather()
}