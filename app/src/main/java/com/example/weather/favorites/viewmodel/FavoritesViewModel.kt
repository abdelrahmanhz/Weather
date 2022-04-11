package com.example.weather.favorites.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.OneCallWeather
import com.example.weather.model.RepositoryInterface
import com.example.weather.model.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel (private val repo: RepositoryInterface, private val context: Context) : ViewModel() {
    private var mutableFavoritesList = MutableLiveData<List<OneCallWeather>>()
    val favoritesList: LiveData<List<OneCallWeather>> = mutableFavoritesList

    fun getFavs() {
        Log.i("TAG", "Fav getFavs")
        viewModelScope.launch (Dispatchers.IO) {
            val favs = repo.getFavorites()
            Log.i("TAG", "Fav getFavs ${favs}")
            mutableFavoritesList.postValue(favs)
        }
    }

    fun deleteFav(city: String) {
        viewModelScope.launch (Dispatchers.IO){
            repo.deleteByCity(city)
        }
    }

    fun getFavWeather(
        lat: Float,
        lon: Float,
        language: String,
        unit: String,
        city: String
    ){
        if(Utils.isNetworkAvailable(context)){
            Log.i("TAG", "Fav getFavWeather")
            val job = viewModelScope.launch(Dispatchers.IO) {
                repo.getFavWeather(lat, lon, language, unit, city)
            }
        }
    }

    fun getWeatherByCity(city: String): OneCallWeather{
        return repo.getWeatherByCity(city)
    }
}