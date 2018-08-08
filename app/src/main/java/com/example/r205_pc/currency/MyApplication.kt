package com.example.r205_pc.currency

import android.app.Application
import com.example.r205_pc.currency.utils.RetrofitUtil
import java.io.File

/**
 * Created by r205-pc on 20.07.2018.
 */
class MyApplication : Application()  {
    override fun onCreate() {
        super.onCreate()
        val fileName = "currencyInfoSaved"
        val cacheFile = File(this.filesDir, fileName)
        retrofit.setCacheFile(cacheFile)
    }
    companion object {
        private val retrofit = RetrofitUtil()
        fun getRetrofitUtil() = retrofit
    }

}