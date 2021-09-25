package com.example.locationsaver.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object InternerConnection {

    lateinit var connectionListener: ConnectionListener

    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetworkInfo: NetworkInfo? =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val dataNetworkInfo: NetworkInfo? =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiNetworkInfo!!.isConnected || dataNetworkInfo!!.isConnected) {
            return true
        }
        return false

    }

    fun connectionLoop(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(500)
            GlobalScope.launch {
                val connectionResult = isConnected(context)
                connectionListener.isConnected(connectionResult)
                connectionLoop(context)
            }

        }

    }
}

interface ConnectionListener {
    fun isConnected(connected: Boolean)
}