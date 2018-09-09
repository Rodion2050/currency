package com.example.r205_pc.currency.utils

import java.io.*

/**
 * Responsible for caching data
 * Created by r205-pc on 17.07.2018.
 */
class DataCache{
    /*Файл, содержащий курс валют по дням*/
    private lateinit var cacheFile:File
    //list contains the dates we have in cache already
    private var _cachedDates = mutableListOf<String>()
    val cachedDates
        get() = this._cachedDates

    fun setCurrencyCacheFile(file: File){
        cacheFile = file
        readAvailableDatesFromCache()
    }
    fun addCurrencyInfoToCache(date: String, currencyInfoOfDay: CurrencyInfoOfDay){
        val writer = PrintWriter(OutputStreamWriter(FileOutputStream(cacheFile, true)))

        writer.print(date)
        for(currency in currencyInfoOfDay.list){
            writer.print(" ${currency.baseCurrency}:${currency.currency}:${currency.rate}")
        }
        writer.println()
        writer.flush()
        writer.close()
    }

    private fun readAvailableDatesFromCache(){
        _cachedDates.clear()
        if(cacheFile.exists()){
            val reader = BufferedReader(InputStreamReader(cacheFile.inputStream()))
            var line = reader.readLine()
            while (line != null){
                val list = line.split(" ")
                val data = list[0]
                _cachedDates.add(data)
                line = reader.readLine()
            }
        }
    }

    fun readCurrencyInfoFromCache(date:String):CurrencyInfoOfDay?{
        if(cacheFile.exists()){
            val reader = BufferedReader(InputStreamReader(cacheFile.inputStream()))
            var line = reader.readLine()
            while(line != null){
                val list = line.split(" ")
                val listCurrencies = ArrayList<CurrencyInfo>()
                val data = list[0]
                if(date == data){
                    for(i in 1 until list.size){
                        val textCurrencyInfo = list[i]
                        val fields = textCurrencyInfo.split(":")
                        if(fields.size == 3){
                            val currency = CurrencyInfo(fields[0], fields[1], fields[2].toFloat())
                            listCurrencies.add(currency)
                        }
                    }
                    _cachedDates.add(date)
                    return CurrencyInfoOfDay(listCurrencies)
                }
                line = reader.readLine()
            }
            reader.close()
        }
        return null
    }

}