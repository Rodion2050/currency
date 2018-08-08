package com.example.r205_pc.currency.utils

import android.util.Log
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by r205-pc on 13.07.2018.
 */
class JSONConverter: Converter<ResponseBody, List<CurrencyInfo>> {
    val TAG = "JSONConverter"
    override fun convert(value: ResponseBody): List<CurrencyInfo> {
        val body = value.string()
        Log.d(TAG, body)
        val jsonObject = JSONObject(body)
        val currArray = jsonObject.getJSONArray("exchangeRate")
        val list = ArrayList<CurrencyInfo>()
        for (i in 1 until currArray.length()){
            val currObj = currArray.getJSONObject(i)
            list.add(CurrencyInfo(currObj.getString("baseCurrency"),
                              currObj.getString("currency"),
                              currObj.getDouble("saleRateNB").toFloat(),
                              currObj.getDouble("purchaseRateNB").toFloat()))
        }
        return list

    }
}
class JSONConverterFactory : Converter.Factory(){
    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return JSONConverter()
    }
}