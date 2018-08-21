package com.example.r205_pc.currency

import android.app.Application
import android.preference.PreferenceManager
import com.example.r205_pc.currency.utils.FixerApi
import java.io.File

/**
 * Created by r205-pc on 20.07.2018.
 */
class MyApplication : Application()  {
    override fun onCreate() {
        super.onCreate()
        val fileNameRates = "currencyRatesSaved"
        val fileNameCurrencies = "currenciesListSaved"
        val cacheRatesFile = File(this.filesDir, fileNameRates)
        val cacheCurrenciesFile = File(this.filesDir, fileNameCurrencies)
        api.setCacheFiles(cacheCurrenciesFile, cacheRatesFile)
    }
    companion object {

        private val api = FixerApi()
        fun getFixerApi() = api
    }
}