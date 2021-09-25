package com.example.locationsaver.pojo

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ImageTable")
data class SavedLocation(
    var name: String,
    var note: String,
    var loctionLatitude: String,
    var loctionLongitude: String,
    var loctionAddress: String,
    var image: Bitmap?,
    var date: String,
    var time: String

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
