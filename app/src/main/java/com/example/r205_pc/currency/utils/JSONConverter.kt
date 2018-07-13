package com.example.r205_pc.currency.utils

import android.util.Log
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by r205-pc on 13.07.2018.
 */
class JSONConverter: Converter<ResponseBody, List<Currency>> {
    val TAG = "JSONConverter"
    override fun convert(value: ResponseBody): List<Currency> {
        val body = value.string()
        Log.d(TAG, body)
        val jsonObject = JSONObject(body)
        val currArray = jsonObject.getJSONArray("exchangeRate")
        val list = ArrayList<Currency>()
        for (i in 0 until currArray.length()){
            val currObj = currArray.getJSONObject(i)
            list.add(Currency(currObj.getString("baseCurrency"),
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