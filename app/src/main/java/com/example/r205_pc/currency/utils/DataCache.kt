package com.example.r205_pc.currency.utils

import android.content.Context
import java.io.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Отвечает за кэширование данных
 * Created by r205-pc on 17.07.2018.
 */
class DataCache{
    /*Файл, содержащий курс валют по дням*/
    private var cacheFile:File? = null
    /*Файл, содержащий список доступных валют*/
    private var currencyListFile:File? = null
    private var cachedSymbols = ""
    /*map, каждой дате поставлен в соответствие курс валют в этот день*/
    private val cacheMap = ConcurrentHashMap<String, CurrencyInfoOfDay>()
    private val currenciesList = mutableListOf<Currency>()



    fun setCurrencyCacheFile(file: File){
        cacheFile = file
        readCurrencyInfoFromCache()
    }
    fun addCurrencyInfoToCache(date: String, currencyInfoOfDay: CurrencyInfoOfDay){
        cacheMap[date] = currencyInfoOfDay
        if(cacheFile != null){
            val writer = PrintWriter(OutputStreamWriter(FileOutputStream(cacheFile, true)))

            writer.print(date)
            for(currency in currencyInfoOfDay.list){
                writer.print(" ${currency.baseCurrency}:${currency.currency}:${currency.rate}")
            }
            writer.println()
            writer.flush()
            writer.close()
        }
    }

    private fun readCurrencyInfoFromCache(){
        if(cacheFile != null){
            if(cacheFile!!.exists()){
                val reader = BufferedReader(InputStreamReader(cacheFile!!.inputStream()))
                var line = reader.readLine()
                if(line != null){
                    cachedSymbols = line
                    line = reader.readLine()
                }
                while(line != null){
                    val list = line.split(" ")
                    val listCurrencies = ArrayList<CurrencyInfo>()
                    val data = list[0]
                    for(i in 1 until list.size){
                        val textCurrencyInfo = list[i]
                        val fields = textCurrencyInfo.split(":")
                        if(fields.size == 3){
                            val currency = CurrencyInfo(fields[0], fields[1], fields[2].toFloat())
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

    fun getCurrencyCacheMap() = cacheMap


    fun setAvailableCurrenciesFile(file:File){
        currencyListFile = file
        readAvailableCurrenciesFromCache()
    }

    fun writeAvailableCurrenciesToCache(currencies:List<Currency>){
        currenciesList.clear()
        for (i in currencies){
            currenciesList.add(i)
        }
        if(currencyListFile != null){
            val writer = PrintWriter(OutputStreamWriter(FileOutputStream(currencyListFile, true)))

            for(currency in currencies){
                writer.print("${currency.code}:${currency.description}\n")
            }
            writer.println()
            writer.flush()
            writer.close()
        }
    }

    private fun readAvailableCurrenciesFromCache(){
        if(currencyListFile != null){
            if(currencyListFile!!.exists()){
                currenciesList.clear()
                val reader = BufferedReader(InputStreamReader(currencyListFile!!.inputStream()))
                var line = reader.readLine()
                while(line != null){
                    val fields = line.split(":")
                    if(fields.size == 2){
                        val currency = Currency(fields[0], fields[1])
                        currenciesList.add(currency)
                    }
                    line = reader.readLine()
                }
                reader.close()
            }
        }
    }


    fun setCachedSymbols(syms:String){
        if(cachedSymbols != syms){
            cacheMap.clear()
            val writer = PrintWriter(OutputStreamWriter(FileOutputStream(cacheFile, false)))
            writer.println(syms)
            writer.flush()
        }
        cachedSymbols = syms
    }

    fun getCurrencyList() = currenciesList


}