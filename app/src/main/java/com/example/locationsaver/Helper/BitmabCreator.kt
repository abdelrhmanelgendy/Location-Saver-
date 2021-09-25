package com.example.locationsaver.Helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File
import java.io.FileOutputStream

object BitmabCreator {
    suspend fun getBitmapFromLink(link: String, context: Context): Bitmap {
        val imageBuilder = ImageRequest.Builder(context)
            .data(link)
            .build()
        val imageLoader = ImageLoader(context)
        val drawable = (imageLoader.execute(imageBuilder) as SuccessResult).drawable
        return (drawable as BitmapDrawable).bitmap
    }

    suspend fun getBitmapFromDevice(uri: Uri, context: Context): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}