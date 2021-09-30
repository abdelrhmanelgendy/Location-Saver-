package com.example.locationsaver.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation
import com.example.locationsaver.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DataBaseViewModel(var repository: Repository) : ViewModel() {
    var historyLiveData: MutableStateFlow<List<HistorySearchedLocations>> =
        MutableStateFlow(listOf(HistorySearchedLocations("", "", "", "")))
    var locationsLiveData: MutableStateFlow<List<SavedLocation>> =
        MutableStateFlow(
            listOf(
                SavedLocation(
                    "",
                    "",
                    "",
                    "",
                    "",
                    null, "", ""
                )
            )
        )

   suspend fun getSavedLocations() {
        viewModelScope.launch {
            locationsLiveData.emit(repository.getAllLocations())
        }
    }

    suspend fun insertLocation(savedLocation: SavedLocation) {
        viewModelScope.launch {
            repository.insertLocation(savedLocation)
        }
    }

    suspend fun deleteLocation(savedLocation: SavedLocation) {
        viewModelScope.launch {
            repository.deletLocation(savedLocation)
        }
    }

    suspend fun getAllHistory() {
        historyLiveData.value = repository.getAllHistory()
    }

    suspend fun insertHistory(historySearchedLocations: HistorySearchedLocations) {
        repository.insertHistory(historySearchedLocations)
    }

}