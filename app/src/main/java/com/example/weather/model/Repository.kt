package com.example.weather.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import com.example.weather.data.local.AppDatabase
import com.example.weather.data.local.ConcreteLocalSource
import com.example.weather.data.local.LocalSource
import com.example.weather.data.remote.RemoteSource
import com.example.weather.data.remote.WeatherClient
import com.example.weather.model.Utils.getCity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class Repository(
    private val remote: RemoteSource,
    private val local : LocalSource,
): RepositoryInterface {

    companion object{
        private var latitude: Float = 0.0f
        private var longitude: Float = 0.0f
        private var favLatitude: Float = 0.0f
        private var favLongitude: Float = 0.0f
        private lateinit var sharedPref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private lateinit var lang :String
        private lateinit var units :String

        @Volatile
        private var INSTANCE: Repository? = null
        fun getRepoInstance(application: Application): Repository {
            sharedPref = application.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
            editor = sharedPref.edit()
            latitude = sharedPref.getFloat(Utils.LATITUDE_SETTING, 0f)
            longitude = sharedPref.getFloat(Utils.LONGITUDE_SETTING, 0f)
            favLatitude = sharedPref.getFloat(Utils.LATITUDE_FAV_SETTING, 0f)
            favLongitude = sharedPref.getFloat(Utils.LONGITUDE_FAV_SETTING, 0f)
            lang = sharedPref.getString(Utils.LANGUAGE_SETTING, "en").toString()
            units = sharedPref.getString(Utils.UNIT_SETTING, "metric").toString()

            return INSTANCE ?: synchronized(this) {
                Repository(WeatherClient,
                    ConcreteLocalSource(AppDatabase.getDataBase(application).weatherDao())
                ).also {
                    INSTANCE = it
                }
            }
        }

        fun getRepoInstance(context: Context): Repository {
            return INSTANCE ?: synchronized(this) {
                Repository(WeatherClient,
                    ConcreteLocalSource(AppDatabase.getDataBase(context).weatherDao())
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    override suspend fun getWeather(
        lat: Float,
        lon: Float,
        language: String,
        units: String,
        city: String
    ): OneCallWeather {
        val response = remote.getWeather(lat,lon,language,units)
        if (response.isSuccessful){

            if (latitude != 0f && longitude != 0f){
                //deleteNotFav()
                response.body()!!.city = city
                insertWeather(response.body()!!)
            }
            return response.body()!!
        }else{
            throw Exception(response.message())
        }
    }

    override suspend fun insertWeather(oneCallWeather: OneCallWeather) {
        Log.i("TAG", "Fav insertWeather")
        local.insert(oneCallWeather)
    }

    override fun getWeatherByCity(city: String): OneCallWeather {
        return local.getWeatherByCity(city)
    }

    override fun deleteByCity(city: String) {
        local.deleteByCity(city)
    }

    override fun getHomeWeather(): OneCallWeather {
        return local.getHomeWeather()
    }

    override fun getFavorites(): List<OneCallWeather> {
        return local.getFavorites()
    }

    override fun refrechData() {
        CoroutineScope(Dispatchers.IO ).launch {
            val favs = local.getFavorites()
            for (fav in favs) {
                Log.i("TAG", "$units $lang")
                getFavWeather(fav.lat.toFloat(), fav.lon.toFloat(), lang, units, fav.city)
            }
        }
    }

//    override suspend fun getFavWeather(
//        lat: Float,
//        lon: Float,
//        language: String,
//        units: String,
//        city: String
//    ) {
//        val response = remote.getWeather(lat,lon,language,units)
//        if (response.isSuccessful){
//            Log.i("TAG", "response: ${response.body()}")
//            if (sharedPref.getFloat(Utils.LATITUDE_FAV_SETTING, 0f) != 0f && sharedPref.getFloat(Utils.LONGITUDE_FAV_SETTING, 0f) != 0f){
//                response.body()!!.city = city
//                response.body()!!.isFav = 1
//                Log.i("TAG", "getFavWeather: ${response.body()}")
//
//                insertWeather(response.body()!!)
//                editor.remove(Utils.LATITUDE_FAV_SETTING)
//                editor.remove(Utils.LONGITUDE_FAV_SETTING)
//                editor.remove(Utils.CITY_FAV_SETTING)
//                editor.commit()
//            }
//            else{
//                Log.i("TAG", "getFavWeather: NOOOO")
//            }
//        }else{
//            throw Exception(response.message())
//        }
//    }

    override suspend fun getFavWeather(
        lat: Float,
        lon: Float,
        language: String,
        units: String,
        city: String
    ) {
        val response = remote.getWeather(lat,lon,language,units)
        if (response.isSuccessful){
                response.body()!!.city = city
                response.body()!!.isFav = 1
                Log.i("TAG", "getFavWeather: ${response.body()}")
                insertWeather(response.body()!!)
        }else{
            throw Exception(response.message())
        }
    }


}