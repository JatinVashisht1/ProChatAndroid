package com.example.demochatapplication.features.shared.internetconnectivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.Binds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionManager @Inject constructor(@ApplicationContext private val context: Context,) {

    var isInternetAvailable = false

    private val networkRequest = NetworkRequest
        .Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkRequestCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isInternetAvailable = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isInternetAvailable = false

        }

        override fun onUnavailable() {
            super.onUnavailable()
            isInternetAvailable = false
        }
    }

    fun startMonitoring () {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkRequestCallback)
    }

    fun stopMonitoring () {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkRequestCallback)
    }
}