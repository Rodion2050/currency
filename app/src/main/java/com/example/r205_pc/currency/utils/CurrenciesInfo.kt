package com.example.r205_pc.currency.utils

/**
 * Created by r205-pc on 13.07.2018.
 */
data class CurrenciesInfo(val date: String, val bank: String, val baseCurrency: Float, val baseCurrencyLit : String, val exchangeRate : List<Currency>)