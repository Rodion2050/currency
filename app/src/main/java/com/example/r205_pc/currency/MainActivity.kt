package com.example.r205_pc.currency

import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import com.example.r205_pc.currency.utils.CurrenciesInfoUpdatedListener
import com.example.r205_pc.currency.utils.CurrencyInfoOfDay
import com.example.r205_pc.currency.utils.RetrofitUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val currencyAdapter = CurrencyAdapter("", "", mapOf(Pair("", CurrencyInfoOfDay(emptyList()))))
    private val formatter = SimpleDateFormat("dd.MM.yyyy")
    private var currDate:Date = formatter.parse("12.07.2018")
    private val retrofitUtil = MyApplication.getRetrofitUtil()
    private val currenciesListener = CurrenciesUpdatedListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initRecyclerView()


        retrofitUtil.addCurrencyUpdatedListener(currenciesListener)

        retrofitUtil.getCurrenciesOfDate(formatter.format(currDate))

        okButton.setOnClickListener(ClickListener())
        cancelButton.setOnClickListener(ClickListener())

    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(view: View?){
            when(view?.id){
                R.id.cancelButton -> showData()
                R.id.okButton -> {
                    currDate = datePickerView.date()
                    retrofitUtil.getCurrenciesOfDate(formatter.format(currDate))
                    showData()
                }
            }
        }
    }

    inner class CurrenciesUpdatedListener : CurrenciesInfoUpdatedListener{
        override fun onCurrenciesInfoUpdated(date: String ,data: Map<String,CurrencyInfoOfDay>) {
            currencyAdapter.setData(formatter.format(currDate), formatter.format(currDate.previousDay()), data)
            val prevDay = currDate.previousDay()
            if(!data.containsKey(formatter.format(prevDay))){
                retrofitUtil.getCurrenciesOfDate(formatter.format(prevDay))
            }

        }
    }

    override fun onDestroy() {
        retrofitUtil.removeCurrencyUpdatedListener(currenciesListener)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.pick_date_item -> showDatePicker()
        }
        return true
    }

    private fun showDatePicker(){
        currencyInfoRecyclerView.visibility = View.INVISIBLE
        val yearFmt = SimpleDateFormat("yyyy")
        val monthFmt = SimpleDateFormat("MM")
        val dayFmt = SimpleDateFormat("dd")
        datePickerView.init(yearFmt.format(currDate).toInt(), monthFmt.format(currDate).toInt() - 1, dayFmt.format(currDate).toInt(), {d, a, b, c -> Unit})
        datePickerDial.visibility = View.VISIBLE
    }

    private fun showData(){
        currencyInfoRecyclerView.visibility = View.VISIBLE
        datePickerDial.visibility = View.INVISIBLE
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
    fun DatePicker.date():Date{
        val date = formatter.parse("$dayOfMonth.${month+1}.$year")
        Log.d(TAG, formatter.format(date))
        return date
    }
}
