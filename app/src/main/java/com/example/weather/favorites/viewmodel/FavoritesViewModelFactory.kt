package com.example.weather.favorites.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepositoryInterface

class FavoritesViewModelFactory (private val repo: RepositoryInterface,
    private val context: Context
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            FavoritesViewModel(this.repo, context) as T
        } else {
            throw IllegalArgumentException("View Not Found")
        }    }
}