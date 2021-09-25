package com.example.locationsaver.pojo

data class ImageAdapter(var imageId: Int, var imageUrl: String)
{
    override fun toString(): String {
        return "ImageAdapter(imageId=$imageId, imageUrl='$imageUrl')"
    }
}