package com.example.weather.data.remote

import com.example.weather.model.OneCallWeather
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeather(
        lat: Float,
        lon: Float,
        language: String ="en",
        units:String = "imperial"
    ) : Response<OneCallWeather>
}