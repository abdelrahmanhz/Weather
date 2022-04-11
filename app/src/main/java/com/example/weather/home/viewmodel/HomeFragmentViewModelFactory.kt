package com.example.weather.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepositoryInterface
import java.lang.IllegalArgumentException

class HomeFragmentViewModelFactory(private val repo: RepositoryInterface
, private val context: Context):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
            HomeFragmentViewModel(this.repo, context) as T
        } else {
            throw IllegalArgumentException("view not found")
        }
    }

}