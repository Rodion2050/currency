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
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), CurrencyAdapter.OnCurrencyChooseListener, DatePickerDialogFragment.OnDateChosenListener {
    private val TAG = "MainActivity"
    private val currencyAdapter = CurrencyAdapter("", "", mapOf(Pair("", CurrencyInfoOfDay(emptyList()))), 1.0f, false, this)
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private var currDate:String = formatter.format(Date())
    private val fixerApi = MyApplication.getFixerApi()
    private val preferencesHelper = PreferencesHelper(this)
    private val currenciesMap = ConcurrentHashMap<String, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main2)
        //setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(this, SelectCurrenciesActivity::class.java))
        }
        initCurrenciesMap()

        initRecyclerView()


        if(savedInstanceState != null){
             currDate = savedInstanceState.getString("CurrDate", formatter.format(Date()))
        }

    }

    override fun onStart() {
        super.onStart()
        fixerApi.setSymbols(preferencesHelper.getUsedCurrencySymbols())
        fixerApi.setSupportedSymbols(resources.getStringArray(R.array.currenciesList))
        getCurrenciesOfDate(currDate)


    }


    override fun onCurrencyChooseListener(currency: CurrencyInfo) {
        val invertRate = preferencesHelper.getInvertRates()
        val baseCurrency = preferencesHelper.getBaseCurrency()
        var descr = ""
        if(!invertRate){
            descr += "1 ${currenciesMap[baseCurrency]} = ${String.format("%f",currency.rate)} ${currenciesMap[currency.currency]}"
        }else{
            descr += "1 ${currenciesMap[currency.currency]} = ${String.format("%f", currency.rate)} ${currenciesMap[baseCurrency]}"
        }
        currencyRateDescription.text = descr
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

            val map = HashMap<String, CurrencyInfoOfDay>()
            map[date] = api.getCurrenciesRatesOfDay(date)
            if(act != null){
                val fmt = act.formatter
                val dateToCompare = fmt.format(fmt.parse(date).previousDay())
                map[dateToCompare] = api.getCurrenciesRatesOfDay(dateToCompare)
            }
            return map
        }

        override fun onPostExecute(result: Map<String,CurrencyInfoOfDay>) {
            val act = activity.get()
            if(act != null){
                val fmt = act.formatter
                val baseCurrency = act.preferencesHelper.getBaseCurrency()
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
                val res = HashMap<String, CurrencyInfoOfDay>()
                //Remove base currency from list
                val dataForTodayList = result[date]?.list
                val modifiedData1 = mutableListOf<CurrencyInfo>()

                if(dataForTodayList != null){
                    for(i in dataForTodayList){
                        if(i.currency != baseCurrency){
                            modifiedData1.add(i)
                        }
                    }
                }
                res[date] = CurrencyInfoOfDay(modifiedData1)
                val dataForPrevDay = result[dateToCompare]?.list
                val modifiedData2 = mutableListOf<CurrencyInfo>()
                if(dataForPrevDay != null){
                    for(i in dataForPrevDay){
                        if(i.currency != baseCurrency){
                            modifiedData2.add(i)
                        }
                    }
                }
                res[dateToCompare] = CurrencyInfoOfDay(modifiedData2)


                act.currencyAdapter.setData(date, dateToCompare, res, baseCurrencyRate, act.preferencesHelper.getInvertRates())
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
            R.id.calculator_open_item -> startActivity(Intent(this, CalculatorActivity::class.java))
        }
        return true
    }

    private fun showDatePicker(){
        val newFragment = DatePickerDialogFragment()
        val args = Bundle()
        args.putString(newFragment.DATE_ARGUMENT, currDate)
        newFragment.arguments = args
        newFragment.show(fragmentManager, "datePicker")
    }

    override fun onDateChosenListener(date: Date) {
        currDate = formatter.format(date)
        Log.d(TAG,"Show data for $currDate")
        getCurrenciesOfDate(currDate)
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

    private fun initCurrenciesMap(){
        val codes = resources.getStringArray(R.array.currenciesList)
        val descriptions = resources.getStringArray(R.array.currenciesDescriptionList)
        for(i in 0 until codes.size){
            currenciesMap[codes[i]] = descriptions[i]
        }
    }

    fun DatePicker.date():Date{
        val date = formatter.parse("$year-${month+1}-$dayOfMonth")
        Log.d(TAG, formatter.format(date))
        return date
    }
}