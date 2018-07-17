package com.example.r205_pc.currency.utils

import android.content.Context
import java.io.*

/**
 * Created by r205-pc on 17.07.2018.
 */
class DataCache(context: Context){
    private val fileName = "currencyInfoSaved"
    private val cacheFile = File(context.filesDir, fileName)


    private val cacheMap = HashMap<String, CurrencyInfoOfDay>()
    init{
        val reader = BufferedReader(InputStreamReader(cacheFile.inputStream()))
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
    fun addDataToCache(date: String, currencyInfoOfDay: CurrencyInfoOfDay){
        val writer = PrintWriter(OutputStreamWriter(FileOutputStream(cacheFile, true)))
        cacheMap[date] = currencyInfoOfDay
        writer.print("$date")
        for(currency in currencyInfoOfDay.list){
            writer.print(" ${currency.baseCurrency}:${currency.currency}:${currency.saleRateNB}:${currency.purchaseRateNB}")
        }
        writer.println()
        writer.flush()
        writer.close()
    }
    fun getCacheMap() = cacheMap

}