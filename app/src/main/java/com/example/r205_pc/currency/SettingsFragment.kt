package com.example.r205_pc.currency

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import com.example.r205_pc.currency.utils.Currency
import java.lang.ref.WeakReference
import android.preference.Preference
import android.preference.PreferenceManager
import com.example.r205_pc.currency.utils.PreferencesHelper


class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener{
    private val BASE_CURRENCY_KEY = "BaseCurrency"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        val pref = findPreference(BASE_CURRENCY_KEY)
        // Set summary to be the user-description for the selected value
        pref.summary = PreferenceManager.getDefaultSharedPreferences(activity).getString(BASE_CURRENCY_KEY, "EUR")
        FetchAvailableCurrencies(this).execute()


    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key == BASE_CURRENCY_KEY){
            val pref = findPreference(key)
            // Set summary to be the user-description for the selected value
            pref.summary = p0?.getString(key, "")
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    class FetchAvailableCurrencies(fragment: SettingsFragment) : AsyncTask<Unit, Unit, List<Currency>>(){

        private val fragment = WeakReference<SettingsFragment>(fragment)

        override fun doInBackground(vararg p0: Unit?): List<Currency> {
            val api = MyApplication.getFixerApi()
            return api.getSupportedSymbols()
        }
        override fun onPostExecute(result: List<Currency>) {
            val fragment = this.fragment.get()

            if(fragment != null){
                val currencies = PreferencesHelper(fragment.activity).getUsedCurrencySymbols().split(",")
                val arr = mutableListOf<String>()
                val arrCodes = mutableListOf<String>()
                for(i in result){
                    if(currencies.contains(i.code)){
                        arr.add(i.description)
                        arrCodes.add(i.code)
                    }

                }

                val pref = fragment.findPreference(fragment.BASE_CURRENCY_KEY)
                if(pref is ListPreference){
                    pref.entries = arr.toTypedArray()
                    pref.entryValues = arrCodes.toTypedArray()
                }
            }
        }
    }
}