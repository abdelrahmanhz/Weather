package com.example.weather.favorites.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.favorites.view.fragment.FavoritesFragmentDirections
import com.example.weather.favorites.viewmodel.FavoritesViewModel
import com.example.weather.model.OneCallWeather
import com.example.weather.model.Utils
import com.example.weather.model.Utils.convertNumberToAR

class FavoritesAdapter (
    private var favs: MutableList<OneCallWeather>,
    val context: Context,
    private val viewModel : FavoritesViewModel
)
    : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private lateinit var lang: String
    private lateinit var unit: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        sharedPreferences = context.getSharedPreferences(Utils.PREFS_FILE_NAME, Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
        lang = sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en").toString()
        unit = sharedPreferences.getString(Utils.UNIT_SETTING, "metric").toString()
        val recyclerViewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.fav_item, parent, false)
        return FavoritesViewHolder(recyclerViewItem)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.cityName.text = favs[position].city
        var tempUnitSymbol: String? = null
        when (unit) {
            "metric" -> tempUnitSymbol = "°c"
            "imperial" -> tempUnitSymbol = "°f"
            "standard" -> tempUnitSymbol = "°k"
        }
        holder.cityTemp.text = (favs[position].current.temp.toInt().
        convertNumberToAR(sharedPreferences.getString(Utils.LANGUAGE_SETTING, "en")!!)
               + " " + tempUnitSymbol)
        holder.favImg.setImageResource(Utils.getIconId(favs[position].current.weather[0].icon))
        holder.deleteBtn.setOnClickListener {

            viewModel.deleteFav(favs[position].city)
            favs.removeAt(position)
            notifyItemRemoved(holder.adapterPosition)
            notifyItemRangeChanged(position, favs.size)
            notifyDataSetChanged()

        }

        holder.constrain.setOnClickListener {
            val action =
                FavoritesFragmentDirections.actionFavoritesFragmentToFavDetailsFragment(favs[position].city)
            it.findNavController().navigate(action)

        }


    }
    override fun getItemCount(): Int {
        return favs.size
    }

    fun setFavs(favs: MutableList<OneCallWeather>) {
        this.favs.clear()
        this.favs.addAll(favs)
        notifyDataSetChanged()
    }

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cityName:TextView = itemView.findViewById(R.id.fav_city_name)
        val favImg: ImageView = itemView.findViewById(R.id.fav_image)
        val cityTemp:TextView = itemView.findViewById(R.id.fav_temp)
        val deleteBtn: ImageView = itemView.findViewById(R.id.btn_delete_fav)
        val constrain: CardView = itemView.findViewById(R.id.cvFav)
    }
}