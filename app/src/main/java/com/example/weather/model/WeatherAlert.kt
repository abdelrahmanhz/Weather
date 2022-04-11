package com.example.weather.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class WeatherAlert (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int,
    @ColumnInfo(name = "startDate")
    var startDate: Long,
    @ColumnInfo(name = "endDate")
    var endDate: Long,
    @ColumnInfo(name = "time")
    var time: String,
    @ColumnInfo(name = "timeInMills")
    var timeInMills: Long,
)