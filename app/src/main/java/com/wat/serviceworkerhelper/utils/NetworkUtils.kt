package com.wat.serviceworkerhelper.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtils {

    companion object {
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val networkCapabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val networks = connectivityManager.allNetworkInfo
                for (info in networks) {
                    if (info != null && info.isConnected) return true
                }
            }

            return false
        }
    }
}