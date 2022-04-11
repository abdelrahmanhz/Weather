package com.example.weather.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weather.model.RepositoryInterface

class SettingsViewModel (private val repo: RepositoryInterface) : ViewModel() {
    fun refreshData(){
        repo.refrechData()
    }
}