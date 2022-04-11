package com.example.weather.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.OneCallWeather

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: OneCallWeather)

    @Query("SELECT * FROM weather WHERE city = :city")
    fun getWeatherByCity(city: String): OneCallWeather

    @Query("DELETE FROM weather WHERE city = :city")
    fun deleteByCity(city :String)

    @Query("SELECT * FROM weather WHERE isFav = 0")
    fun getHomeWeather(): OneCallWeather

    @Query("SELECT * FROM weather WHERE isFav = 1")
    fun getFavorites(): List<OneCallWeather>
}