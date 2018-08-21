package com.example.r205_pc.currency.utils

/**
 * Created by r205-pc on 13.07.2018.
 */
data class CurrencyInfo(val baseCurrency: String, val currency: String, val rate: Float)
class CurrencyInfoOfDay(val list: List<CurrencyInfo>)