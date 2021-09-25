package com.example.locationsaver.util

import android.location.Address

interface OnSearchForLocationListener {
    fun onSuccess(address: Address)
    fun onError(e: String)
    fun onNothingFound(e: String)
    fun onStart()
}