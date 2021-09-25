package com.example.locationsaver.databases.remot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.pojo.imageFromWeb.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageWebSeachClient() : ViewModel() {
    companion object {
        lateinit var onFillStateFlowListener: OnFillStateFlowListener
    }

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


    val imageStateFlow: MutableStateFlow<ImageWebSearch> =
        MutableStateFlow<ImageWebSearch>(imageWebSearch)

    fun getData(searchQyery: String, country: String, reapiApiHost: String, reapiApiKey: String) {
        val imageWebSearchAPI = Retrofit.Builder()
            .baseUrl("https://bing-image-search1.p.rapidapi.com/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ImageWebSearchAPI::class.java)
        viewModelScope
            .launch(Dispatchers.IO) {
                val imageWebSearch = imageWebSearchAPI.searchForPhotoByName(
                    rapidApiHost = reapiApiHost,
                    rapidApiKey = reapiApiKey,
                    searchQyery,
                    country
                )
                    .body()
                imageStateFlow.emit(imageWebSearch!!)
                onFillStateFlowListener.fillData(imageWebSearch)
                Log.d("WebSearchActivity", "getData: complete")

            }

    }


}

interface OnFillStateFlowListener {
    fun fillData(imageWebSearch: ImageWebSearch)
}