package com.example.weather.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepositoryInterface

class SettingsViewModelFactory (private val repo: RepositoryInterface) :
    ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(this.repo) as T
        } else {
            throw IllegalArgumentException("View Not Found")
        }
    }
}