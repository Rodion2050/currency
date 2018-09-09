package com.example.r205_pc.currency.utils

import android.content.Context
import android.preference.PreferenceManager
import com.example.r205_pc.currency.MainActivity

class PreferencesHelper(private val context: Context){
    /**
     * Сохраняет кодировки валют, которые интересуют пользователя, перечисленные через ","
     */
    fun setUsedCurrencySymbols(syms:String){
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putString("CurrencySymbols", syms).apply()
    }
    fun getUsedCurrencySymbols():String{
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getString("CurrencySymbols", "USD")
    }
    fun getBaseCurrency():String{
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getString("BaseCurrency", "EUR")
    }
    fun getInvertRates():Boolean{
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean("InvertRates", false)
    }
}