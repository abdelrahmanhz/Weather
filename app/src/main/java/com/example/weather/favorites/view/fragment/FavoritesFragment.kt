package com.example.weather.favorites.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.FragmentFavoritesBinding
import com.example.weather.favorites.view.adapter.FavoritesAdapter
import com.example.weather.favorites.viewmodel.FavoritesViewModel
import com.example.weather.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weather.model.OneCallWeather
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.model.Utils.getCity
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var longitude: Float = 0.0f
    private var latitude: Float = 0.0f
    private lateinit var lang: String
    private lateinit var unit: String
    private lateinit var city: String
    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPrefs()

        binding.fabAddFav.setOnClickListener(View.OnClickListener {
            activity?.let{
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment()
                it.findNavController(view.id).navigate(action)
            }
        })

        binding.rvFav.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvFav.hasFixedSize()

        viewModel =
            ViewModelProvider(
                this,
                FavoritesViewModelFactory(Repository.getRepoInstance(requireContext()),
                    requireContext())
            )[FavoritesViewModel::class.java]

        adapter = FavoritesAdapter(emptyList<OneCallWeather>().toMutableList(),requireContext(),viewModel)
        binding.rvFav.adapter = adapter

        getWeatherForDisplay()

        binding.favSwipe.setOnRefreshListener {
            getWeatherForDisplay()
            binding.favSwipe.isRefreshing = false
        }
    }

    private fun getWeatherForDisplay() {
        if(!Utils.isNetworkAvailable(requireContext())){
            Snackbar.make(binding.favRoot,"No internet connection", Snackbar.LENGTH_LONG)
                .setAction("connect") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(panelIntent)
                    }
                    else{
                        val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(wifiIntent)
                    }
                }
                .show()
        }
        Log.i("TAG", "Fav getWeatherForDisplay")
        if(preferences.getFloat(Utils.LATITUDE_FAV_SETTING, 0f) != 0f
            && preferences.getFloat(Utils.LONGITUDE_FAV_SETTING, 0f) != 0f){

            latitude = preferences.getFloat(Utils.LATITUDE_FAV_SETTING, 0F)
            longitude = preferences.getFloat(Utils.LONGITUDE_FAV_SETTING, 0f)
//            city = preferences.getString(Utils.CITY_FAV_SETTING, "empty").toString()
            lang = preferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
            unit = preferences.getString(Utils.UNIT_SETTING, "metric").toString()
            city = LatLng(latitude.toDouble(), longitude.toDouble()).getCity(this.requireContext())
            viewModel.getFavWeather(latitude, longitude, lang, unit, city)
        }
        runBlocking {
            launch(Dispatchers.IO)
            {
                viewModel.getFavs()
            }

        }

        viewModel.favoritesList.observe(this){
            Log.i("TAG", "favoritesList FavFrag: $it")
            adapter.setFavs(it as MutableList<OneCallWeather>)
            binding.rvFav.adapter?.notifyDataSetChanged()
        }
    }

    private fun initPrefs() {
        preferences = this.activity?.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)!!
        editor = preferences.edit()
    }

}