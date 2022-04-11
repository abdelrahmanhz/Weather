package com.example.weather.home.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Log.INFO
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.Daily
import com.example.weather.model.Utils
import com.example.weather.model.Utils.convertNumberToAR
import com.example.weather.model.Utils.getThatDay
import java.util.logging.Level.INFO

class DaysAdapter(
    private val days: List<Daily>,
    val context: Context
    ): RecyclerView.Adapter<DaysAdapter.DaysViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        sharedPreferences =
            context.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
        val unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric")

        if (position == 0) {
            holder.day.text = context.getString(R.string.today)
        } else {
            holder.day.text = days[position].dt.getThatDay(lang)

        }
        holder.statusDaily.text = days[position].weather[0].description

        val min = days[position].temp.min.toInt()
        val max = days[position].temp.max.toInt()
        var tempUnit: String? = null
        when (unit) {
            "imperial" -> tempUnit =
                (min.convertNumberToAR(lang) + "/" +
                    max.convertNumberToAR(lang)+ "°f")

            "metric" -> tempUnit =
                (min.convertNumberToAR(lang) + "/" +
                        max.convertNumberToAR(lang) + "°c")
            "standard" -> tempUnit =
                (min.convertNumberToAR(lang) + "/" +
                        max.convertNumberToAR(lang) + "°k")
        }
        holder.temperatureProbability.text = tempUnit
        holder.imgDaily.setImageResource(Utils.getIconId(days[position].weather[0].icon))
    }

    override fun getItemCount(): Int {
        return days.size
    }

    class DaysViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val day: TextView = itemView.findViewById(R.id.day)
        val imgDaily: ImageView = itemView.findViewById(R.id.day_icon)
        val statusDaily: TextView = itemView.findViewById(R.id.day_status)
        val temperatureProbability: TextView = itemView.findViewById(R.id.day_temperature_probability)
        val cardView: CardView = itemView.findViewById(R.id.day_card_view)
    }
}