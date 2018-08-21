package com.example.r205_pc.currency.utils

import android.net.Uri
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class FixerApi{
    private val accessKey = "d8114f9f88b65a31b0fab64052cdef12"
    private val dataCache = DataCache()
    private var symbols = ""
    private val TAG = "FixerApi"
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    fun setCacheFiles(currenciesListFile:File, currenciesRatesFile:File){
        dataCache.setCurrencyCacheFile(currenciesRatesFile)
        dataCache.setAvailableCurrenciesFile(currenciesListFile)
    }

    fun setSymbols(syms: String){
        symbols = syms
        dataCache.setCachedSymbols(syms)
    }

    fun getCurrenciesRatesOfDay(date: String):Map<String, CurrencyInfoOfDay>{
        val map = dataCache.getCurrencyCacheMap()
        if(map.keys.contains(date)){
            Log.d(TAG, "Read available currencies from cache")
            return map
        }

        val request = Uri.parse("http://data.fixer.io/api/$date").buildUpon()
                .appendQueryParameter("access_key", accessKey)
                .appendQueryParameter("symbols", symbols)
                .build()
        val response = NetworkUtil.loadStringFromUrl(URL(request.toString()))
        val document = JSONObject(response)
        val base = document.getString("base")
        val rates = document.getJSONObject("rates")
        val list = mutableListOf<CurrencyInfo>()
        for(i in rates.keys()){
            list.add(CurrencyInfo(base, i, rates.getString(i).toFloat()))
        }

        dataCache.addCurrencyInfoToCache(date, CurrencyInfoOfDay(list))
        return dataCache.getCurrencyCacheMap()
    }


    fun getSupportedSymbols():List<Currency>{
        val cachedCurrencyList = dataCache.getCurrencyList()
        if(cachedCurrencyList.isNotEmpty()){
            Log.d(TAG, "Read available currencies from cache")
            return cachedCurrencyList
        }
        Log.d(TAG, "Read available currencies from server")
        val request = Uri.parse("http://data.fixer.io/api/symbols").buildUpon()
                .appendQueryParameter("access_key", accessKey)
                .build()
        val response = NetworkUtil.loadStringFromUrl(URL(request.toString()))
        val document = JSONObject(response)
        val symbols = document.getJSONObject("symbols")
        val list = mutableListOf<Currency>()
        for(i in symbols.keys()){
            list.add(Currency(i, symbols.getString(i)))
        }

        dataCache.writeAvailableCurrenciesToCache(list)
        return list
    }
}