package com.example.weather.data.remote

import android.util.Log
import com.example.weather.model.OneCallWeather
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherClient: RemoteSource{

    private const val baseURL = "https://api.openweathermap.org/data/2.5/"

    fun getInstance(): Retrofit{
        return Retrofit.Builder().baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override suspend fun getWeather(
        lat: Float,
        lon: Float,
        language: String,
        units: String
    ): Response<OneCallWeather> {
        Log.i("TAG", "getWeather remote $lat $lon $language $units")
        Log.i("TAG", "getWeather remote ${getInstance().create(WeatherApiService::class.java).getWeather(lat.toString(), lon.toString(), language, units)}")
        return getInstance().create(WeatherApiService::class.java).getWeather(lat.toString(), lon.toString(), language, units)
    }
}