package com.example.weather.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weather.R
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

object Utils {


    const val CITY_FAV_SETTING: String= "FAV_CITY"
    const val LONGITUDE_FAV_SETTING: String = "FAV_LONGITUDE"
    const val LATITUDE_FAV_SETTING: String= "FAV_LATITUDE"
    const val MAP_SETTING: String = "MAP"
    const val PREFS_FILE_NAME: String = "settings"
    const val IS_FIRST_SETTING: String = "IS_FIRST"
    const val LONGITUDE_SETTING: String = "LONGITUDE"
    const val LATITUDE_SETTING: String = "LATITUDE"
    const val CITY_SETTING: String= "CITY"
    const val UNIT_SETTING: String = "UNIT"
    const val LANGUAGE_SETTING: String = "LANGUAGE"

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo?.isConnected
    }

    fun LatLng.getAddress(context: Context): String {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(this.latitude, this.longitude, 1)
        return addresses[0].getAddressLine(0).toString()
    }

    fun LatLng.getCity(context: Context, lang: String): String {

//        val geocoder = Geocoder(context)
//        val addresses: List<Address>?
//        val address: Address?
//        var addressText = ""
//        val doublelat: Double = this.latitude!!.toDouble()
//        val doublelong: Double = this.longitude!!.toDouble()
//
//        try {
//
//            addresses = geocoder.getFromLocation(doublelat, doublelong, 1)
//
//            if (null != addresses && !addresses.isEmpty()) {
//                address = addresses[0]
//
//                addressText = address.getAddressLine(0)
//            }
//        } catch (e: IOException) {
//            Log.e("MapsActivity", e.localizedMessage)
//        }
//
//        return addressText
        var city = ""
        val geocoder = Geocoder(context, Locale(lang))
        val addresses= geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            city = state
        }
        return city
    }

    fun LatLng.getCity(context: Context): String {

//        val geocoder = Geocoder(context)
//        val addresses: List<Address>?
//        val address: Address?
//        var addressText = ""
//        val doublelat: Double = this.latitude!!.toDouble()
//        val doublelong: Double = this.longitude!!.toDouble()
//
//        try {
//
//            addresses = geocoder.getFromLocation(doublelat, doublelong, 1)
//
//            if (null != addresses && !addresses.isEmpty()) {
//                address = addresses[0]
//
//                addressText = address.getAddressLine(0)
//            }
//        } catch (e: IOException) {
//            Log.e("MapsActivity", e.localizedMessage)
//        }
//
//        return addressText
        var city = ""
        val geocoder = Geocoder(context)
        val addresses= geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            city = state
        }
        return city
    }

    fun Int.dateFormat(lang: String): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = this.toLong() * 1000
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale(lang))
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        return "$day $month $year"
    }

    fun Int.convertNumberToAR(lang: String): String {

        if (lang == "ar") {
            var result = ""
            var en = '0'
            for (ch in this.toString()) {
                en = ch
                when (ch) {
                    '0' -> en = '۰'
                    '1' -> en = '۱'
                    '2' -> en = '۲'
                    '3' -> en = '۳'
                    '4' -> en = '٤'
                    '5' -> en = '۵'
                    '6' -> en = '٦'
                    '7' -> en = '۷'
                    '8' -> en = '۸'
                    '9' -> en = '۹'
                }
                result = "${result}$en"
            }
            return result
        } else {
            return this.toString()
        }
    }

    fun getIconId(icon: String): Int {
        var iconID: Int? = null
        when (icon) {
            "01d" -> iconID = R.drawable.ic_sun_svgrepo_com
            "01n" -> iconID = R.drawable.ic_moon_svgrepo_com
            "02d" -> iconID = R.drawable.twod
            "02n" -> iconID = R.drawable.twon
            "03d" -> iconID = R.drawable.threed
            "03n" -> iconID = R.drawable.threen
            "04d" -> iconID = R.drawable.fourd
            "04n" -> iconID = R.drawable.fourn
            "09d" -> iconID = R.drawable.nined
            "09n" -> iconID = R.drawable.ninen
            "10d" -> iconID = R.drawable.tend
            "10n" -> iconID = R.drawable.tenn
            "11d" -> iconID = R.drawable.eleven_d
            "11n" -> iconID = R.drawable.eleven_n
            "13d" -> iconID = R.drawable.ic_snow_svgrepo_com
            "13n" -> iconID = R.drawable.ic_snow_svgrepo_com
            "50d" -> iconID = R.drawable.fifty_d
            "50n" -> iconID = R.drawable.fifty_n
        }
        return iconID!!
    }

    fun Int.getThatDay(lang: String): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = this.toLong() * 1000
        return calendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.LONG,
            Locale(lang))
    }

    fun Int.getTime(lang: String): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm aaa", Locale(lang))
        val dateString = simpleDateFormat.format(this * 1000L)
        return String.format(dateString)
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = (this * 1000).toLong()
//        val format = SimpleDateFormat("hh:00 aaa", Locale(lang))
//        return format.format(calendar.time)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.convertTimeToLong():Long{

        //type of med , long time user insert from calender ang compare it with get days
        val localDate = LocalDate.now()

        val timeAndDate="${localDate.dayOfMonth}-"+ localDate.getMonthValue() + "-" + "${localDate.getYear()}" + " " + this
        val timeInMilliseconds: Long
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        var mDate: Date? = null
        try {
            mDate = sdf.parse(timeAndDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.i("TAG", "Date m datee: $mDate")
        timeInMilliseconds = mDate!!.time
        return timeInMilliseconds
    }

    fun String.convertDateToMillis(): Long {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val calendar = Calendar.getInstance()
        try {
            val date = sdf.parse(this)
            calendar.time = date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar.timeInMillis
    }
}
