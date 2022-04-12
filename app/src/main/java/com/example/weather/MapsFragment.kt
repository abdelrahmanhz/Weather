package com.example.weather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.weather.databinding.FragmentMapsBinding
import com.example.weather.favorites.view.fragment.FavoritesFragmentDirections
import com.example.weather.favorites.viewmodel.FavoritesViewModel
import com.example.weather.favorites.viewmodel.FavoritesViewModelFactory
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.model.Utils.getCity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MapsFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMapClickListener{

    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: FragmentMapsBinding
    private var latitude: Float = 0f
    private var longitude: Float = 0f
    lateinit var lang: String
    lateinit var unit: String
    var currentMarker: Marker? = null
    private lateinit var map: GoogleMap
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initPrefs()
        binding = FragmentMapsBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    private fun initPrefs() {
        sharedPreferences = activity?.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)!!
        lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en")!!
        unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        viewModel =
            ViewModelProvider(
                this,
                FavoritesViewModelFactory(
                    Repository.getRepoInstance(requireContext()),
                    requireContext())
            )[FavoritesViewModel::class.java]
        binding.btnSelectLoc.setOnClickListener {
            if(currentMarker != null && latitude != 0f && longitude != 0f){
                val city = LatLng(latitude.toDouble(), longitude.toDouble()).getCity(this.requireContext(), lang)
                Log.i("TAG", "city: $city")
                CoroutineScope(Dispatchers.IO).run{
                    viewModel.getFavWeather(latitude, longitude, lang, unit, city)
                }

                val action = MapsFragmentDirections.actionMapsFragmentToFavoritesFragment()
                it.findNavController().navigate(action)
            }
            else{
                Toast.makeText(this.requireContext(), "please, mark a location", Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0

        if(longitude != 0f  && longitude != 0f){
            val latLong = LatLng(longitude.toDouble(), latitude.toDouble())
            drawMarker(latLong)
        }

        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null){
                    currentMarker?.remove()
                }

                val newLatLng = LatLng(p0.position.latitude, p0.position.longitude)
                Log.i("Map", "onMarkerDragEnd: ${newLatLng.longitude} , ${newLatLng.latitude}")
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {

            }

        })

        map.setOnMapLongClickListener { p0 ->
            if (currentMarker == null) {
                val newLatLng = LatLng(p0.latitude, p0.longitude)
                Log.i("Map", "onMarkerDragEnd: ${newLatLng.longitude} , ${newLatLng.latitude}")
                longitude = p0.longitude.toFloat()
                latitude = p0.latitude.toFloat()
                drawMarker(newLatLng)
            }
        }
    }

    private fun drawMarker(latLong: LatLng) {
        Log.i("Map", "drawMarker: ${latLong.longitude} , ${latLong.latitude}")
        val markerOptions = MarkerOptions().position(latLong).title("location")
            .snippet("(${latLong.latitude}, ${latLong.longitude})").draggable(true)
        map.animateCamera(CameraUpdateFactory.newLatLng(latLong))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f))
        currentMarker = map.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    override fun onMapClick(p0: LatLng) {
        if (currentMarker != null){
            currentMarker?.remove()
        }

        val newLatLng = LatLng(p0.latitude, p0.longitude)
        Log.i("Map", "onMapClick: ${newLatLng.longitude} , ${newLatLng.latitude}")
        drawMarker(newLatLng)
    }
}