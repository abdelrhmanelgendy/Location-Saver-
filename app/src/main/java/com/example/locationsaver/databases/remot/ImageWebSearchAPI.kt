package com.example.locationsaver.databases.remot

import com.example.locationsaver.pojo.imageFromWeb.ImageWebSearch
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface ImageWebSearchAPI {
    //    @Headers(
//        "x-rapidapi-host: bing-image-search1.p.rapidapi.com",
//        "x-rapidapi-key: f9cbdffdb3msh01470815c601b1ep1e6f6fjsn6ec55644837d"
//
//    )
    @GET("search")
    suspend fun searchForPhotoByName(
        @Header("x-rapidapi-host")
        rapidApiHost: String,
        @Header("x-rapidapi-key")
        rapidApiKey: String,
        @Query("q") keyWord: String,
        @Query("mkt") country: String
    ): Response<ImageWebSearch>

}