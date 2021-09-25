package com.example.locationsaver.databases.local

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.pojo.HistorySearchedLocations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {



    val historyMutableStateFlow: MutableStateFlow<List<HistorySearchedLocations>> =
        MutableStateFlow(listOf(HistorySearchedLocations("", "", "", "")))

    fun getData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val historyList = LocationRoomBuilder.buildDataBase(context)
                .historyDao().getHistory()
            historyMutableStateFlow.value = historyList

        }

    }


}
