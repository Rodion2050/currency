package com.example.r205_pc.currency.utils

import android.content.Context
import com.example.r205_pc.currency.R

/**
 *
 */
class CurrencyListGetter{
    /**
     * Gets data from resources and makes a list from currencies
     * @param context app context that allows to get resources
     * @return list of currencies
     */
    fun getCurrenciesFromResArray(context: Context):List<Currency>{
        val codes = context.resources.getStringArray(R.array.currenciesList)
        val descriptions = context.resources.getStringArray(R.array.currenciesDescriptionList)
        val result = mutableListOf<Currency>()
        for(i in 0 until codes.size){
            result.add(Currency(codes[i], descriptions[i]))
        }
        return result

    }
}