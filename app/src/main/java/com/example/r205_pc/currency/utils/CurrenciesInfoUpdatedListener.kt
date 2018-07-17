package com.example.r205_pc.currency.utils

import java.util.*

/**
 * Created by r205-pc on 15.07.2018.
 */
interface CurrenciesInfoUpdatedListener {
    fun onCurrenciesInfoUpdated(date: String, data: Map<String, CurrencyInfoOfDay>)
}