package com.example.locationsaver.databases.remot

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.pojo.imageFromWeb.*
import com.example.locationsaver.repository.ImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageViewModel @ViewModelInject constructor(var repository: ImagesRepository) : ViewModel() {


    val pivotSuggestions = List<PivotSuggestion>(1) { PivotSuggestion("-1", List<Any>(1) {}) }
    val values = List<Value>(1) {
        Value(
            "-1", "-1", "-1", "-1", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", "-1",
            InsightsMetadata(-1, -1, -1), false, false, "-1",
            Thumbnail
                (-1, -1), "-1", "-1", 1
        )
    }
    val imageWebSearch = ImageWebSearch(
        -1,
        instrumentation = Instrumentation("-1"),
        nextOffset = -1,
        pivotSuggestions,
        queryContext = QueryContext("-1", "-1", "-1", "-1", "-1"),
        "-1",
        -1,
        "-1", values, "-1"
    )


    val imageStateFlow: MutableStateFlow<List<Value>> =
        MutableStateFlow<List<Value>>(values)

    suspend fun getData(
        searchQyery: String,
        country: String,
        reapiApiHost: String,
        reapiApiKey: String
    ) {
        val imageResponse = repository.getImages(reapiApiHost, reapiApiKey, searchQyery, country)
        val imagesUrl = imageResponse.body()?.value
        imageStateFlow.value = imagesUrl!!
    }


}
