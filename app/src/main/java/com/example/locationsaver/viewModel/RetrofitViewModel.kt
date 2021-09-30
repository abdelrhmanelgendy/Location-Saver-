package com.example.locationsaver.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.pojo.imageFromWeb.InsightsMetadata
import com.example.locationsaver.pojo.imageFromWeb.Thumbnail
import com.example.locationsaver.pojo.imageFromWeb.Value
import com.example.locationsaver.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RetrofitViewModel(var repository: Repository) : ViewModel() {
    val values = List<Value>(1) {
        Value(
            "-1", "-1", "-1", "-1", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", "-1",
            InsightsMetadata(-1, -1, -1), false, false, "-1",
            Thumbnail(-1, -1), "-1", "-1", 1
        )
    }
    val imageStateFlow: MutableStateFlow<List<Value>> =
        MutableStateFlow<List<Value>>(values)
    lateinit var onFillStateFlowListener: OnFillStateFlowListener

    fun getData(searchQyery: String, country: String, reapiApiHost: String, reapiApiKey: String) {
        viewModelScope.launch {
            val value = repository.imageWebSearchAPI.searchForPhotoByName(
                reapiApiHost,
                reapiApiKey,
                searchQyery,
                country
            ).body()?.value
            imageStateFlow.value = value!!
            onFillStateFlowListener.fillData(value)

        }
    }
}

interface OnFillStateFlowListener {
    fun fillData(values: List<Value>)
}