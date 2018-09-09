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
    private val supportedSymbols = mutableListOf<String>()
    fun setCacheFiles(currenciesListFile:File, currenciesRatesFile:File){
        dataCache.setCurrencyCacheFile(currenciesRatesFile)
    }

    fun setSymbols(syms: String){
        symbols = syms
    }

    fun getCurrenciesRatesOfDay(date: String):CurrencyInfoOfDay{
        if(dataCache.cachedDates.contains(date)){
            Log.d(TAG, "Read available currencies from cache")
            val res = dataCache.readCurrencyInfoFromCache(date)
            if(res != null){
                val list = res.list
                val listOfChoosedCurrencies = mutableListOf<CurrencyInfo>()
                for(i in 0 until list.size){
                    if(symbols.contains(list[i].currency)){
                        listOfChoosedCurrencies.add(list[i])
                    }
                }
                return CurrencyInfoOfDay(listOfChoosedCurrencies)
            }
        }

        var allSymbols = ""
        for(i in supportedSymbols){
            allSymbols += i
            if(i != supportedSymbols.last()){
                allSymbols += ","
            }
        }

        val request = Uri.parse("http://data.fixer.io/api/$date").buildUpon()
                .appendQueryParameter("access_key", accessKey)
                .appendQueryParameter("symbols", allSymbols)
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
        val listOfChoosedCurrencies = mutableListOf<CurrencyInfo>()
        for(i in 0 until list.size){
            if(symbols.contains(list[i].currency)){
                listOfChoosedCurrencies.add(list[i])
            }
        }
        return CurrencyInfoOfDay(listOfChoosedCurrencies)
    }


//    fun getSupportedSymbolsfromServer():List<Currency>{
//        val cachedCurrencyList = dataCache.getCurrencyList()
//        if(cachedCurrencyList.isNotEmpty()){
//            Log.d(TAG, "Read available currencies from cache")
//            return cachedCurrencyList
//        }
//        Log.d(TAG, "Read available currencies from server")
//        val request = Uri.parse("http://data.fixer.io/api/symbols").buildUpon()
//                .appendQueryParameter("access_key", accessKey)
//                .build()
//        val response = NetworkUtil.loadStringFromUrl(URL(request.toString()))
//        val document = JSONObject(response)
//        val symbols = document.getJSONObject("symbols")
//        val list = mutableListOf<Currency>()
//        for(i in symbols.keys()){
//            list.add(Currency(i, symbols.getString(i)))
//        }
//
//        dataCache.writeAvailableCurrenciesToCache(list)
//        return list
//    }
    fun setSupportedSymbols(list:Array<String>){
        supportedSymbols.clear()
        for(i in list){
            supportedSymbols.add(i)
        }
    }
}