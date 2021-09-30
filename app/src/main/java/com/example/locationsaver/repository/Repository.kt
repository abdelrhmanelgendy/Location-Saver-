package com.example.locationsaver.repository

import com.example.locationsaver.databases.local.HistoryDao
import com.example.locationsaver.databases.local.LocationDao
import com.example.locationsaver.databases.remot.ImageWebSearchAPI
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation
import com.example.locationsaver.pojo.imageFromWeb.Value
import kotlinx.coroutines.flow.MutableStateFlow

class Repository(
    var imageWebSearchAPI: ImageWebSearchAPI,
    var locationDao: LocationDao,
    var historyDao: HistoryDao
) {

    suspend fun getAllLocations(): List<SavedLocation> {
        return locationDao.getAllLocations()
    }

    suspend fun insertLocation(savedLocation: SavedLocation) {
        locationDao.insertLocation(savedLocation)
    }

    suspend fun deletLocation(savedLocation: SavedLocation) {
        locationDao.deleteLocation(savedLocation)
    }


    suspend fun getAllHistory(): List<HistorySearchedLocations> {
        return historyDao.getHistory()
    }

    suspend fun insertHistory(historySearchedLocations: HistorySearchedLocations) {
        historyDao.inserHistory(historySearchedLocations)
    }

    suspend fun getPhotos(
        searchQyery: String,
        country: String,
        reapiApiHost: String,
        reapiApiKey: String
    ): List<Value> {
        return imageWebSearchAPI.searchForPhotoByName(
            reapiApiHost,
            reapiApiKey,
            searchQyery,
            country
        )
            .body()?.value!!
    }

}