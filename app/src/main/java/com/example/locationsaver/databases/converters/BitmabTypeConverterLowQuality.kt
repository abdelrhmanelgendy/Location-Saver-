package com.example.locationsaver.databases.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmabTypeConverterLowQuality {
    @TypeConverter
    fun fromBitmabToArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()

    }

    @TypeConverter
    fun fromArraytoBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}