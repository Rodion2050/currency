package com.example.r205_pc.currency

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.r205_pc.currency.utils.CurrenciesInfoUpdatedListener
import com.example.r205_pc.currency.utils.CurrencyInfoOfDay
import com.example.r205_pc.currency.utils.RetrofitUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val currencyAdapter = CurrencyAdapter("", "", mapOf(Pair("", CurrencyInfoOfDay(emptyList()))))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

        val retrofitUtil = RetrofitUtil(this)
        val formatter = retrofitUtil.formatter

        val currDate:Date = formatter.parse("12.07.2018")

        val fourDaysBefore = currDate.previousDay()

        Log.d(TAG, "Getting data for ${formatter.format(currDate)}")

        retrofitUtil.addCurrencyUpdatedListener(object : CurrenciesInfoUpdatedListener{
            override fun onCurrenciesInfoUpdated(date: String ,data: Map<String,CurrencyInfoOfDay>) {
                currencyAdapter.setData(formatter.format(currDate), formatter.format(currDate.previousDay()), data)
                if(!data.containsKey(formatter.format(fourDaysBefore))){
                    retrofitUtil.getCurrenciesOfDate(formatter.format(fourDaysBefore))
                }

            }

        })

        retrofitUtil.getCurrenciesOfDate(formatter.format(currDate))



    }

    private fun initRecyclerView(){
        val layout = LinearLayoutManager(this)
        currencyInfoRecyclerView.apply {
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = layout

            // specify an viewAdapter (see also next example)
            adapter = currencyAdapter

        }
    }
    fun Date.previousDay():Date{
        return Date(time - 24*60*60*1000)
    }
}
