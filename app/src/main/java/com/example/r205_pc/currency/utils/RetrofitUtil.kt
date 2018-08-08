package com.example.r205_pc.currency.utils

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.reflect.Array
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by r205-pc on 13.07.2018.
 */
class RetrofitUtil(){
    val TAG = "RetrofitUtil"
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.privatbank.ua")
            .addConverterFactory(JSONConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val privatBankApi:PrivatBankApi = retrofit.create(PrivatBankApi::class.java)
    private val currenciesUpdatedListeners  = ArrayList<CurrenciesInfoUpdatedListener>()
    private val timer = Timer()
    private val cachedData = DataCache()


    fun addCurrencyUpdatedListener(listener: CurrenciesInfoUpdatedListener){
        currenciesUpdatedListeners.add(listener)
    }
    fun removeCurrencyUpdatedListener(listener: CurrenciesInfoUpdatedListener){
        for(i in 0 until currenciesUpdatedListeners.size){
            if(currenciesUpdatedListeners[i] == listener){
                currenciesUpdatedListeners.removeAt(i)
            }
        }
    }

    fun setCacheFile(file: File){
        cachedData.setCacheFile(file)
    }

    fun getCurrenciesOfDate(dateStr: String){
        if(cachedData.getCacheMap().containsKey(dateStr)){
            for(listener in currenciesUpdatedListeners){
                listener.onCurrenciesInfoUpdated(dateStr, cachedData.getCacheMap())
            }
            return
        }
        privatBankApi.getData("1", dateStr).enqueue(object : retrofit2.Callback<List<CurrencyInfo>>{
            override fun onFailure(call: Call<List<CurrencyInfo>>?, t: Throwable?) {
                Log.d(TAG, "get Data failed ")
                t?.printStackTrace()
                //Retry after a second
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        getCurrenciesOfDate(dateStr)
                    }
                }, 10000)
            }

            override fun onResponse(call: Call<List<CurrencyInfo>>?, response: Response<List<CurrencyInfo>>?) {
                if(response != null){
                    val res = response.body()
                    if(res != null && currenciesUpdatedListeners.isNotEmpty()){
                        //Fill data
                        cachedData.addDataToCache(dateStr, CurrencyInfoOfDay(res))
                        for(listener in currenciesUpdatedListeners){
                            listener.onCurrenciesInfoUpdated(dateStr, cachedData.getCacheMap())
                        }
                    }


                }
            }
        })
    }



//    fun getCurrenciesOfDates(dates: List<String>){
//        for(day in dates){
//            privatBankApi.getData("1", day).enqueue(object : retrofit2.Callback<List<CurrencyInfo>>{
//                val map = HashMap<String, CurrencyInfoOfDay>()
//                override fun onFailure(call: Call<List<CurrencyInfo>>?, t: Throwable?) {
//                    Log.d(TAG, "get Data failed ")
//                    t?.printStackTrace()
//                }
//
//                override fun onResponse(call: Call<List<CurrencyInfo>>?, response: Response<List<CurrencyInfo>>?) {
//                    if(response != null){
//                        val res = response.body()
//
//                        if(res != null){
//
//                            map[dates[map.size]] = CurrencyInfoOfDay(res)
//
//                            if(map.size == dates.size){
//                                for(listener in currenciesUpdatedListeners){
//                                    listener.onCurrenciesInfoUpdated(map)
//                                }
//                            }
//
//                        }
//
//
//                    }
//                }
//            })
//            Thread.sleep(1000)
//        }
//    }

}