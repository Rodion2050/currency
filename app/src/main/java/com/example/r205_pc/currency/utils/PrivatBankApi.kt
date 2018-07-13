package com.example.r205_pc.currency.utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Created by r205-pc on 13.07.2018.
 */
interface PrivatBankApi {
    @GET("/p24api/exchange_rates")
    fun getData(@Query("json") json:String = "1", @Query("date") date:String):Call<List<Currency>>
}