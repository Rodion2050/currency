package com.example.r205_pc.currency

import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.r205_pc.currency.utils.*
import com.example.r205_pc.currency.utils.Currency
import kotlinx.android.synthetic.main.activity_calculator.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class CalculatorActivity : AppCompatActivity(), View.OnClickListener, SelectCurrencyDialogFragment.OnCurrencySelectedListener{
    private lateinit var activeCurrencyTextView:TextView
    private val preferencesHelper = PreferencesHelper(this)
    private var baseCurrency = ""
    private var baseCurrencyRate = 1f
    private var currency = ""
    private var currencyRate = 1f
    private var currencyAmount = 0f
    private val currencyList = mutableListOf<CurrencyInfo>()
    private val currencyDescriptionsList = mutableListOf<Currency>()
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        getCurrenciesOfDate(formatter.format(Date()))
        initCurrencyDescriptionsList()

        calculatorButton0.setOnClickListener(this)
        calculatorButton1.setOnClickListener(this)
        calculatorButton2.setOnClickListener(this)
        calculatorButton3.setOnClickListener(this)
        calculatorButton4.setOnClickListener(this)
        calculatorButton5.setOnClickListener(this)
        calculatorButton6.setOnClickListener(this)
        calculatorButton7.setOnClickListener(this)
        calculatorButton8.setOnClickListener(this)
        calculatorButton9.setOnClickListener(this)
        calculatorButtonPoint.setOnClickListener(this)
        calculatorButtonDeleteAll.setOnClickListener(this)
        calculatorButtonDeleteOneDigit.setOnClickListener(this)
        calculatorCurrencyAmount.setOnClickListener(this)
        calculatorCurrencyBaseAmount.setOnClickListener(this)
        calculatorCurrency.setOnClickListener(this)
        calculatorCurrencyBase.setOnClickListener(this)
        setActiveCurrencyTextView(calculatorCurrencyAmount)

        setBaseCurrency(preferencesHelper.getBaseCurrency())
        setCurrency("USD")
        calculateCurrency()

    }

    private val ACTIVE_BASE_CURRENCY_TV_KEY = "ActiveCurrency"
    private val ACTIVE_CURRENCY_AMOUNT_KEY = "Amount"
    private val CURRENCY_KEY = "Currency"
    private val BASE_CURRENCY_KEY = "BaseCurrency"
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState != null){
            outState.putBoolean(ACTIVE_BASE_CURRENCY_TV_KEY, false)//0-currency field is active, 1-baseCurrency field is active
            outState.putFloat(ACTIVE_CURRENCY_AMOUNT_KEY, currencyAmount)
            outState.putString(CURRENCY_KEY, currency)
            outState.putString(BASE_CURRENCY_KEY, baseCurrency)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean(ACTIVE_BASE_CURRENCY_TV_KEY)){
                setActiveCurrencyTextView(calculatorCurrencyBaseAmount)
            }else{
                setActiveCurrencyTextView(calculatorCurrencyAmount)
            }
            currencyAmount = savedInstanceState.getFloat(ACTIVE_CURRENCY_AMOUNT_KEY)
            activeCurrencyTextView.text = currencyAmount.toString()
            setCurrency(savedInstanceState.getString(CURRENCY_KEY))
            setBaseCurrency(savedInstanceState.getString(BASE_CURRENCY_KEY))
            calculateCurrency()
        }
    }

    private fun setBaseCurrency(code:String){
        baseCurrency = code
        for(i in currencyList){
            if(i.currency == code){
                baseCurrencyRate = i.rate
            }
        }
        calculatorCurrencyBase.text = code
        for(i in currencyDescriptionsList){
            if(i.code == baseCurrency){
                calculatorBaseCurrencyDescription.text = i.description
            }
        }
    }
    private fun setCurrency(code:String){
        currency = code
        calculatorCurrency.text = code
        for(i in currencyList){
            if(i.currency == code){
                currencyRate = i.rate
            }
        }
        for(i in currencyDescriptionsList){
            if(i.code == currency){
                calculatorCurrencyDescription.text = i.description
            }
        }
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.calculatorButton0 -> updateCurrencyViewWithDigit(0)
                R.id.calculatorButton1 -> updateCurrencyViewWithDigit(1)
                R.id.calculatorButton2 -> updateCurrencyViewWithDigit(2)
                R.id.calculatorButton3 -> updateCurrencyViewWithDigit(3)
                R.id.calculatorButton4 -> updateCurrencyViewWithDigit(4)
                R.id.calculatorButton5 -> updateCurrencyViewWithDigit(5)
                R.id.calculatorButton6 -> updateCurrencyViewWithDigit(6)
                R.id.calculatorButton7 -> updateCurrencyViewWithDigit(7)
                R.id.calculatorButton8 -> updateCurrencyViewWithDigit(8)
                R.id.calculatorButton9 -> updateCurrencyViewWithDigit(9)
                R.id.calculatorButtonPoint -> activeCurrencyTextView.text = "${activeCurrencyTextView.text}."
                R.id.calculatorButtonDeleteAll -> activeCurrencyTextView.text = ""
                R.id.calculatorButtonDeleteOneDigit -> activeCurrencyTextView.text = activeCurrencyTextView.text.toString().dropLast(1)
                R.id.calculatorCurrencyAmount -> setActiveCurrencyTextView(calculatorCurrencyAmount)
                R.id.calculatorCurrencyBaseAmount ->setActiveCurrencyTextView(calculatorCurrencyBaseAmount)
                R.id.calculatorCurrency -> {
                    currencyToSelect = 0
                    selectCurrency()
                }
                R.id.calculatorCurrencyBase -> {
                    currencyToSelect = 1
                    selectCurrency()
                }

            }
            if(activeCurrencyTextView.text.isEmpty()){
                activeCurrencyTextView.text = "0"
            }
            currencyAmount = activeCurrencyTextView.text.toString().toFloat()
            calculateCurrency()
        }
    }

    private fun setActiveCurrencyTextView(textView:TextView){
        when(textView.id){
            R.id.calculatorCurrencyAmount -> {
                activeCurrencyTextView = calculatorCurrencyAmount
                calculatorCurrencyAmount.setTextColor(Color.RED)
                calculatorCurrencyBaseAmount.setTextColor(Color.BLACK)
                calculatorCurrencyAmount.text = "0"
            }
            R.id.calculatorCurrencyBaseAmount -> {
                activeCurrencyTextView = calculatorCurrencyBaseAmount
                calculatorCurrencyBaseAmount.setTextColor(Color.RED)
                calculatorCurrencyAmount.setTextColor(Color.BLACK)
                calculatorCurrencyBaseAmount.text = "0"
            }
        }
    }

    private fun updateCurrencyViewWithDigit(digit:Int){
        if(activeCurrencyTextView.text == "0"){
            activeCurrencyTextView.text = digit.toString()
        }else{
            activeCurrencyTextView.text = "${activeCurrencyTextView.text}$digit"
        }
    }



    private fun calculateCurrency() {

        val rate = currencyRate/baseCurrencyRate
        if(activeCurrencyTextView.id == R.id.calculatorCurrencyAmount){
            calculatorCurrencyBaseAmount.text = String.format("%f", (1.0/rate)*currencyAmount)
        }else{
            calculatorCurrencyAmount.text =  String.format("%f", rate*currencyAmount)
        }
    }

    private var currencyToSelect = 0

    private fun selectCurrency(){
        val dialogFragment = SelectCurrencyDialogFragment()
        val bundle = Bundle()
        bundle.putString(dialogFragment.CURRENCY_LIST_ARGUMENT_KEY, preferencesHelper.getUsedCurrencySymbols())
        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentManager, "selectCurrency")
    }

    override fun currencySelected(code: String) {
        if(currencyToSelect == 0){
            setCurrency(code)
        }else if(currencyToSelect == 1){
            setBaseCurrency(code)
        }
        calculateCurrency()
    }

    private fun getCurrenciesOfDate(date: String) {
        FetchCurrenciesRates(this).execute(date)
    }

    /**
     * Получение данных о курсах*/
    class FetchCurrenciesRates(activity: CalculatorActivity) : AsyncTask<String, Unit, CurrencyInfoOfDay>(){
        private val activity = WeakReference<CalculatorActivity>(activity)
        private var date = ""
        override fun doInBackground(vararg p0: String): CurrencyInfoOfDay {
            val api = MyApplication.getFixerApi()
            date = p0[0]


            return api.getCurrenciesRatesOfDay(p0[0])
        }

        override fun onPostExecute(result: CurrencyInfoOfDay) {
            val act = activity.get()
            if(act != null){
                val baseCurrency = act.preferencesHelper.getBaseCurrency()
                val listCurrencies = result.list
                act.currencyList.clear()
                for(i in listCurrencies){
                    act.currencyList.add(i)
                }

                act.setBaseCurrency(baseCurrency)
                act.setCurrency(act.currency)
                act.calculateCurrency()

            }
        }
    }

    private fun initCurrencyDescriptionsList(){
        val result = CurrencyListGetter().getCurrenciesFromResArray(this)
        currencyDescriptionsList.clear()
        for(i in result){
            currencyDescriptionsList.add(i)
        }
    }

}
