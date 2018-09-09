package com.example.r205_pc.currency

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import com.example.r205_pc.currency.utils.Currency
import com.example.r205_pc.currency.utils.CurrencyListGetter
import com.example.r205_pc.currency.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_select_currencies.*
import java.lang.ref.WeakReference

class SelectCurrenciesActivity : AppCompatActivity() {
    val currencyChooseAdapter = CurrencyChooseAdapter()
    val preferenceHelper = PreferencesHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_currencies)

        initRecyclerView()
        currencyChooseAdapter.setData(CurrencyListGetter().getCurrenciesFromResArray(this),
                preferenceHelper.getUsedCurrencySymbols())
    }

    private fun initRecyclerView() {
        currencyChooseRecyclerView.apply {
            setHasFixedSize(true)
            adapter = currencyChooseAdapter
            layoutManager = LinearLayoutManager(this@SelectCurrenciesActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceHelper.setUsedCurrencySymbols(currencyChooseAdapter.getSelectedCurrencies())
    }
}
