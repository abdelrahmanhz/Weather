package com.example.weather.data.remote

import com.example.weather.model.OneCallWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY ="5e7e27967b9d86bc493628c43a28e1a7"
const val EXCLUDE = "minutely"

interface WeatherApiService {
    @GET("onecall")
    suspend fun getWeather(

        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") language: String ="en",
        @Query("units") units:String = "imperial",
        @Query("exclude") exclude: String = EXCLUDE,
        @Query("appid") appid: String = API_KEY,
    ):Response<OneCallWeather>
}
