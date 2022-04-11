package com.example.weather.data.local

import android.util.Log
import androidx.room.TypeConverter
import com.example.weather.model.Alert
import com.example.weather.model.Daily
import com.example.weather.model.Hourly
import com.example.weather.model.Weather
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun hoursListToJson(value: List<Hourly>?) = Gson().toJson(value)

    @TypeConverter
    fun daysListToJson(value: List<Daily>) = Gson().toJson(value)

    @TypeConverter
    fun weatherListToJson(value: List<Weather>) = Gson().toJson(value)

    @TypeConverter
    fun alertsListToJson(value: List<Alert>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToHoursList(value: String) =
        Gson().fromJson(value, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun jsonToDaysList(value: String) =
        Gson().fromJson(value, Array<Daily>::class.java).toList()

    @TypeConverter
    fun jsonToWeatherList(value: String) =
        Gson().fromJson(value, Array<Weather>::class.java).toList()

    @TypeConverter
    fun jsonToAlertList(value: String?): List<Alert>? {

        if(!value.equals("null")) {
            return Gson().fromJson(value, Array<Alert>::class.java).toList()
        }
        return emptyList()
    }



}