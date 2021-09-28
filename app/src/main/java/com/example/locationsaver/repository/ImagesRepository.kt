package com.example.locationsaver.repository

import com.example.locationsaver.databases.remot.ImageWebSearchAPI
import com.example.locationsaver.pojo.imageFromWeb.ImageWebSearch
import retrofit2.Response
import javax.inject.Inject


class ImagesRepository @Inject constructor(private var imageWebSearchAPI: ImageWebSearchAPI) {
    suspend fun getImages(
        rapidApiHost: String,
        rapidApiKey: String,
        query: String,
        country: String
    ): Response<ImageWebSearch> {

        return imageWebSearchAPI.searchForPhotoByName(rapidApiHost, rapidApiKey, query, country);
    }
}