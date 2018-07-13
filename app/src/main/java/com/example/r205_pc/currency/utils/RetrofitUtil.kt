package com.example.r205_pc.currency.utils

import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by r205-pc on 13.07.2018.
 */
class RetrofitUtil{

    companion object {
        private val retrofit = Retrofit.Builder()
                .baseUrl("https://api.privatbank.ua")
                .addConverterFactory(JSONConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val privatBankApi:PrivatBankApi = retrofit.create(PrivatBankApi::class.java)
    }
}