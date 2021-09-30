package com.example.locationsaver.databases.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.locationsaver.databases.converters.BitmabTypeConverter
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation

@Database(
    entities = arrayOf(SavedLocation::class, HistorySearchedLocations::class), version = 2
)
@TypeConverters(BitmabTypeConverter::class)
abstract class LocationRoomBuilder : RoomDatabase() {
    public abstract fun LocationDao(): LocationDao
    public abstract fun historyDao(): HistoryDao


}