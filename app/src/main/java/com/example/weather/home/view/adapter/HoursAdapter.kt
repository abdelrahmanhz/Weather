package com.example.weather.home.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.Hourly
import com.example.weather.model.Utils
import com.example.weather.model.Utils.convertNumberToAR
import com.example.weather.model.Utils.getTime


class HoursAdapter(
    private val hours: List<Hourly>,
    val context: Context
): RecyclerView.Adapter<HoursAdapter.HoursViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return HoursViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        sharedPreferences =
            context.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
        val unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric")

        if (position == 0) {
            holder.hour.text = "Now"
        }

        holder.hour.text = hours[position].dt.toInt().getTime(lang)
        var tempUnit: String? = null
        when(unit){
            "imperial" ->  tempUnit = (hours[position].temp.toInt().convertNumberToAR(lang) + "°f")
            "metric" -> tempUnit = (hours[position].temp.toInt().convertNumberToAR(lang) + "°c")
            "standard" -> tempUnit = (hours[position].temp.toInt().convertNumberToAR(lang) + "°k")
        }
        holder.temperature.text = tempUnit
        holder.imgHourly.setImageResource(Utils.getIconId(hours[position].weather[0].icon))
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    class HoursViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val hour:TextView = itemView.findViewById(R.id.hours)
        val imgHourly: ImageView = itemView.findViewById(R.id.hours_icon)
        val temperature: TextView = itemView.findViewById(R.id.hour_temperature)
    }
}