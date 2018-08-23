package com.example.r205_pc.currency

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import com.example.r205_pc.currency.utils.*
import com.example.r205_pc.currency.utils.Currency
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "MainActivity"
    private val currencyAdapter = CurrencyAdapter("", "", mapOf(Pair("", CurrencyInfoOfDay(emptyList()))), 1.0f, false)
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private var currDate:String = formatter.format(Date())
    private val fixerApi = MyApplication.getFixerApi()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main2)
        //setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(this, SelectCurrenciesActivity::class.java))
        }

        //setUsedCurrencySymbols("RUB,USD,UAH,AUD,BTC,GBP,JPY,CHF,CNY,ALL")

        initRecyclerView()

//        fixerApi.setSymbols(getUsedCurrencySymbols())
//        getCurrenciesOfDate(currDate)
//        showData()

        okButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)

        if(savedInstanceState != null){
             currDate = savedInstanceState.getString("CurrDate", formatter.format(Date()))
        }

    }

    override fun onStart() {
        super.onStart()
        fixerApi.setSymbols(getUsedCurrencySymbols())
        getCurrenciesOfDate(currDate)
        showData()


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cancelButton -> showData()
            R.id.okButton -> {
                currDate = formatter.format(datePickerView.date())
                getCurrenciesOfDate(currDate)
                showData()
            }
        }
    }
    /**
     * Сохраняет кодировки валют, которые интересуют пользователя, перечисленные через ","
     * */
    private fun setUsedCurrencySymbols(syms:String){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putString("CurrencySymbols", syms).apply()
    }
    private fun getUsedCurrencySymbols():String{
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getString("CurrencySymbols", "USD")
    }
    private fun getBaseCurrency():String{
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getString("BaseCurrency", "EUR")
    }
    private fun getInvertRates():Boolean{
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        return sp.getBoolean("InvertRates", false)
    }

    private fun getCurrenciesOfDate(date: String) {
        FetchCurrenciesRates(this).execute(date)
    }

    /**
     * Получение данных о курсах*/
    class FetchCurrenciesRates(activity: MainActivity) : AsyncTask<String, Unit, Map<String,CurrencyInfoOfDay>>(){
        private val activity = WeakReference<MainActivity>(activity)
        private var date = ""
        override fun doInBackground(vararg p0: String): Map<String,CurrencyInfoOfDay> {
            val api = MyApplication.getFixerApi()
            date = p0[0]
            val act = activity.get()


            var map = api.getCurrenciesRatesOfDay(p0[0])

            if(act != null){
                val fmt = act.formatter
                val dateToCompare = fmt.format(fmt.parse(date).previousDay())
                map = api.getCurrenciesRatesOfDay(dateToCompare)
            }
            return map
        }

        override fun onPostExecute(result: Map<String,CurrencyInfoOfDay>) {
            val act = activity.get()
            if(act != null){
                val fmt = act.formatter
                val baseCurrency = act.getBaseCurrency()
                val listCurrencies = result[date]?.list
                var baseCurrencyRate = 1.0f
                if(listCurrencies != null){
                    for(i in listCurrencies){
                        if(i.currency == baseCurrency){
                            baseCurrencyRate = i.rate
                            break
                        }
                    }
                }

                val dateToCompare = fmt.format(fmt.parse(date).previousDay())
                act.currencyAdapter.setData(date, dateToCompare, result, baseCurrencyRate, act.getInvertRates())
            }
        }
        fun Date.previousDay():Date{
            return Date(time - 24*60*60*1000)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("CurrDate", currDate)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.pick_date_item -> showDatePicker()
            R.id.settings_open_item -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return true
    }

    private fun showDatePicker(){
        currencyInfoRecyclerView.visibility = View.INVISIBLE
        val yearFmt = SimpleDateFormat("yyyy")
        val monthFmt = SimpleDateFormat("MM")
        val dayFmt = SimpleDateFormat("dd")
        val date = formatter.parse(currDate)
        datePickerView.init(yearFmt.format(date).toInt(), monthFmt.format(date).toInt() - 1, dayFmt.format(date).toInt(), {d, a, b, c -> Unit})
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

    fun DatePicker.date():Date{
        val date = formatter.parse("$year-${month+1}-$dayOfMonth")
        Log.d(TAG, formatter.format(date))
        return date
    }
}