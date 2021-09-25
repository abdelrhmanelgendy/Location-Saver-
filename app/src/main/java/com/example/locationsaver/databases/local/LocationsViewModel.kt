package com.example.locationsaver.databases.local

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationsaver.R
import com.example.locationsaver.pojo.SavedLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationsViewModel : ViewModel() {


    val locationMutableStateFlow: MutableStateFlow<List<SavedLocation>> =
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

    fun getData(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val locationList = LocationRoomBuilder.buildDataBase(context)
                .LocationDao().getAllLocations()
            locationMutableStateFlow.value = locationList

        }

    }


}
