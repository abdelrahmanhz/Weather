package com.example.weather.data.local

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.model.OneCallWeather

@Database(entities = [OneCallWeather::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDataBase(application: Application): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val  instance = Room.databaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java,
                    "weather_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun getDataBase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val  instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "weather_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}