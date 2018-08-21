package com.example.r205_pc.currency.utils

import android.util.Log
import java.net.URL
import java.util.*

class NetworkUtil{
    companion object {
        val TAG = "NetworkUtil"
        fun loadStringFromUrl(url: URL):String{
            Log.d(TAG, "Reading data from $url")
            val urlConnection = url.openConnection()
            //val outputStream = urlConnection.getOutputStream()
            val scanner = Scanner(urlConnection.getInputStream())
            scanner.useDelimiter("\\A")
            return scanner.next()
        }
    }

}