package com.example.weather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.databinding.ActivityMapBinding
import com.example.weather.model.Utils
import com.example.weather.model.Utils.getAddress
import com.example.weather.model.Utils.getCity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var preferences: SharedPreferences
    private var currentLocation: Location? = null
    var currentMarker: Marker? = null
    private lateinit var editor: SharedPreferences.Editor

    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPrefs()
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("TAG", "inside map onCreate")
        //temp
//        if(preferences.getFloat(Utils.LONGITUDE_SETTING, 0f) != 0f && ){
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//        else if(preferences.getString(Utils.MAP_SETTING, "0") == "1"){
//            selectLocation()
//        }
//        else{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fetchLocation()
//        }

        binding.btnSelectLoc.setOnClickListener {
            if(currentMarker != null){
                updateSharedPrefs()
//                if(preferences.getString(Utils.MAP_SETTING, "0") == "0"){
                    startActivity(Intent(this, MainActivity::class.java))
//                }
//                else{
//                    editor.putString(Utils.MAP_SETTING, "0")
//                    editor.commit()
//                }
                finish()
            }
            else{
                Toast.makeText(this, "please, mark a location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
            selectLocation()
            return
        }
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null || (currentLocation != null && !location?.equals(currentLocation)!!)){
                this.currentLocation = location
                val mapsFragment = supportFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
                  mapsFragment?.getMapAsync(this)
            }
        }
    }

    private fun updateSharedPrefs() {
        Log.i("Map", "long: ${currentLocation?.longitude} & lat: ${currentLocation?.latitude}")
//        if(preferences.getString(Utils.MAP_SETTING, "0") == "1"){
            editor.putFloat(Utils.LATITUDE_SETTING, currentMarker!!.position.latitude.toFloat())
            editor.putFloat(Utils.LONGITUDE_SETTING, currentMarker!!.position.longitude.toFloat())
            editor.putString(Utils.CITY_SETTING, currentMarker!!.position.getCity(this))
            editor.putBoolean(Utils.IS_FIRST_SETTING, false)
//        }
//        else{
//            editor.putBoolean(Utils.IS_FIRST_SETTING, false)
//            editor.putFloat(Utils.LATITUDE_SETTING, currentMarker!!.position.latitude.toFloat())
//            editor.putFloat(Utils.LONGITUDE_SETTING, currentMarker!!.position.longitude.toFloat())
//            editor.putString(Utils.CITY_SETTING, currentMarker!!.position.getCity(this))
//        }
        editor.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST_CODE -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestDeviceLocationSettings()
                fetchLocation()
            }
            else{
                Log.i("Map", "onRequestPermissionsResult: false")
                selectLocation()
            }
        }
    }

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            val state = locationSettingsResponse.locationSettingsStates
            if (state != null) {
                if (state.isGpsPresent){
                    Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show()
                    fetchLocation()
                }
            }
            else{
                selectLocation()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MapActivity,
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectLocation() {

        val mapsFragment = supportFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
        mapsFragment?.getMapAsync(this)
    }

    private fun initPrefs() {
        preferences = getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if(currentLocation?.latitude != null  && currentLocation?.longitude != null){
            val latLong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
            drawMarker(latLong)
        }
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDrag(p0: Marker) {
                //Log.i("Map", "onMarkerDrag: ${p0.position.longitude} , ${p0.position.latitude}")
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
                Log.i("Map", "onMarkerDragStart: ${p0.position.longitude} , ${p0.position.latitude}")
            }

        })

        map.setOnMapLongClickListener { p0 ->
            if (currentMarker == null) {
                val newLatLng = LatLng(p0.latitude, p0.longitude)
                Log.i("Map", "onMarkerDragEnd: ${newLatLng.longitude} , ${newLatLng.latitude}")
                drawMarker(newLatLng)
            }
        }
    }

    private fun drawMarker(latLong: LatLng) {
        Log.i("Map", "drawMarker: ${latLong.longitude} , ${latLong.latitude}")
//        val markerOptions = MarkerOptions().position(latLong).title("I'm Here")
//            .snippet(getAddress(latLong.latitude, latLong.longitude)).draggable(true)
        val markerOptions = MarkerOptions().position(latLong).title("location")
            .snippet("(${latLong.latitude}, ${latLong.longitude})").draggable(true)
        map.animateCamera(CameraUpdateFactory.newLatLng(latLong))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f))
        //Log.i("Map", "drawMarker: ${latLong.getAddress(this)}")
//        currentLocation!!.latitude = latLong.latitude
//        currentLocation!!.longitude = latLong.longitude
        currentMarker = map.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

//    private fun getAddress(lat: Double, lon: Double): String {
//        val geoCoder = Geocoder(this, Locale.getDefault())
//        val addresses = geoCoder.getFromLocation(lat, lon, 1)
//        return addresses[0].getAddressLine(0).toString()
//        //addresses[0].thoroughfare + ", " + addresses[0].subAdminArea + ", " + addresses[0].countryName
//    }

    override fun onMapClick(p0: LatLng) {
        if (currentMarker != null){
            currentMarker?.remove()
        }

        val newLatLng = LatLng(p0.latitude, p0.longitude)
        Log.i("Map", "onMapClick: ${newLatLng.longitude} , ${newLatLng.latitude}")
        drawMarker(newLatLng)
    }
}