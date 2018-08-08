package com.example.r205_pc.currency.utils

import android.content.Context
import java.io.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by r205-pc on 17.07.2018.
 */
class DataCache{
    private var cacheFile:File? = null
    private val cacheMap = ConcurrentHashMap<String, CurrencyInfoOfDay>()
    init{
        readDataFromCacheFile()

    }

    fun setCacheFile(file: File){
        cacheFile = file
        readDataFromCacheFile()
    }
    fun addDataToCache(date: String, currencyInfoOfDay: CurrencyInfoOfDay){
        cacheMap[date] = currencyInfoOfDay
        if(cacheFile != null){
            val writer = PrintWriter(OutputStreamWriter(FileOutputStream(cacheFile, true)))

            writer.print("$date")
            for(currency in currencyInfoOfDay.list){
                writer.print(" ${currency.baseCurrency}:${currency.currency}:${currency.saleRateNB}:${currency.purchaseRateNB}")
            }
            writer.println()
            writer.flush()
            writer.close()
        }
    }
    fun getCacheMap() = cacheMap

    private fun readDataFromCacheFile(){
        if(cacheFile != null){
            if(cacheFile!!.exists()){
                val reader = BufferedReader(InputStreamReader(cacheFile!!.inputStream()))
                var line = reader.readLine()
                while(line != null){
                    val list = line.split(" ")
                    val listCurrencies = ArrayList<CurrencyInfo>()
                    val data = list[0]
                    for(i in 1 until list.size){
                        val textCurrencyInfo = list[i]
                        val fields = textCurrencyInfo.split(":")
                        if(fields.size == 4){
                            val currency = CurrencyInfo(fields[0], fields[1], fields[2].toFloat(), fields[3].toFloat())
                            listCurrencies.add(currency)
                        }
                    }
                    val currencyInfoOfDay = CurrencyInfoOfDay(listCurrencies)
                    cacheMap[data] = currencyInfoOfDay
                    line = reader.readLine()
                }
                reader.close()
            }
        }

    }


}