package com.example.r205_pc.currency

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.r205_pc.currency.utils.CurrenciesInfo
import com.example.r205_pc.currency.utils.Currency
import com.example.r205_pc.currency.utils.RetrofitUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitUtil.privatBankApi.getData("1", "01.12.2014").enqueue(object : retrofit2.Callback<List<Currency>>{
            override fun onFailure(call: Call<List<Currency>>?, t: Throwable?) {
                Log.d(TAG, "get Data failed ")
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<List<Currency>>?, response: Response<List<Currency>>?) {
                if(response != null){
                    val res = response.body()
                    if(res != null){
                        currencyInfo.text = ""
                        for(curr in res){
                            currencyInfo.append("$curr\n\n\n")
                        }

                    }


                }
            }
        })
    }
}
