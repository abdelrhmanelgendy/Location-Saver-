package com.example.locationsaver.helper

import android.content.Context
import android.location.Geocoder

import com.example.locationsaver.util.OnSearchForLocationListener
import java.lang.Exception

class LocationByNameSearch(
    val context: Context,
    val onSearchForLocationListener: OnSearchForLocationListener
) {

    fun getLocationAddressFromName(locationName: String) {
        onSearchForLocationListener.onStart()
        val geocoder = Geocoder(context)
        try {
            val listOfSearchedLocation = geocoder.getFromLocationName(locationName, 1)
            if (listOfSearchedLocation.size > 0) {
                onSearchForLocationListener.onSuccess(listOfSearchedLocation.get(0))
            } else {
                onSearchForLocationListener.onNothingFound(locationName)
            }
        } catch (e: Exception) {
            onSearchForLocationListener.onError(e.message.toString())
        }

    }
}