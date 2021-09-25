package com.example.locationsaver.Helper

import android.content.Context
import android.net.Uri
import java.io.FileDescriptor

class FileSizes {
companion object{
    fun getFileSize(uri: Uri, context: Context): Long? {
        val openAssetFileDescriptor = context.contentResolver.openAssetFileDescriptor(uri, "r")
        return ((openAssetFileDescriptor?.length)?.div(1024))
    }
}
}