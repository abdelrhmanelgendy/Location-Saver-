package com.example.locationsaver.databases.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.locationsaver.databases.converters.BitmabTypeConverterLowQuality
import com.example.locationsaver.pojo.SavedLocation

@Database(entities = [SavedLocation::class], version = 1)
@TypeConverters(BitmabTypeConverterLowQuality::class)
abstract class LocationRoomBuilderLowQualityImage : RoomDatabase() {
    public abstract fun LocationDao(): LocationDao

    companion object {
        fun buildDataBase(context: Context): LocationRoomBuilderLowQualityImage =
            Room.databaseBuilder(context, LocationRoomBuilderLowQualityImage::class.java, "LocationDatabase")
                .build()
    }
}