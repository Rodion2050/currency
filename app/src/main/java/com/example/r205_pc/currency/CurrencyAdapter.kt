package com.example.r205_pc.currency

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.r205_pc.currency.utils.CurrencyInfo
import com.example.r205_pc.currency.utils.CurrencyInfoOfDay
import com.example.r205_pc.currency.utils.MathHelper

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by r205-pc on 14.07.2018.
 */
class CurrencyAdapter(private var currDate: String,
                      private var dateToCompare: String,
                      private var currencyInfoMap : Map<String, CurrencyInfoOfDay>,
                      private var currencyBaseRate:Float,
                      private var invertRates:Boolean,
                      private val onCurrencyChooseListener: OnCurrencyChooseListener)
    : RecyclerView.Adapter<CurrencyAdapter.CurrencyAdapterViewHolder>() {
    private val TAG = "CurrencyAdapter"

    interface OnCurrencyChooseListener{
        fun onCurrencyChooseListener(currency:CurrencyInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_list_item, parent, false)
        return CurrencyAdapterViewHolder(view)
    }

    override fun getItemCount():Int {
        val list = currencyInfoMap[currDate]?.list
        return if(list == null) { 0 } else { list.size }
    }


    override fun onBindViewHolder(holder: CurrencyAdapterViewHolder, position: Int) {
        val currencyInfoDataSet = currencyInfoMap[currDate]?.list
        currencyInfoDataSet ?: return
        val currencyRate = if(invertRates){1/(currencyInfoDataSet[position].rate/currencyBaseRate)}
                           else{currencyInfoDataSet[position].rate/currencyBaseRate}
        val helper = MathHelper()
        holder.currencyRateTextView.text = helper.truncateNumberToNSymbols(Formatter(Locale.ENGLISH).format("%f", currencyRate).toString(), 7)
        holder.currencyNameTextView.text = currencyInfoDataSet[position].currency

        val currencyInfoImage = holder.currencyImageView
        when(currencyInfoDataSet[position].currency){
            "CAD" -> currencyInfoImage.setImageResource(R.drawable.cad)
            "CHF" -> currencyInfoImage.setImageResource(R.drawable.chf)
            "EUR" -> currencyInfoImage.setImageResource(R.drawable.eur)
            "GBP" -> currencyInfoImage.setImageResource(R.drawable.gbp)
            "RUB" -> currencyInfoImage.setImageResource(R.drawable.rub)
            "SEK" -> currencyInfoImage.setImageResource(R.drawable.sek)
            "UAH" -> currencyInfoImage.setImageResource(R.drawable.uah)
            "USD" -> currencyInfoImage.setImageResource(R.drawable.usd)
            "XAU" -> currencyInfoImage.setImageResource(R.drawable.xau)
            "AUD" -> currencyInfoImage.setImageResource(R.drawable.aud)
            "ILS" -> currencyInfoImage.setImageResource(R.drawable.ils)
            else -> currencyInfoImage.setImageResource(R.drawable.currency)
        }
        val currencyInfoDataSetPrevDate = currencyInfoMap[dateToCompare]?.list
        val currencyInfoDirection = holder.currencyInfoDirectionView
        if(currencyInfoDataSetPrevDate == null){
            currencyInfoDirection.setImageResource(R.drawable.arrow_up)
        }else{
            val currencyPrevRate = if(invertRates){1/(currencyInfoDataSetPrevDate[position].rate/currencyBaseRate)}
                                   else{currencyInfoDataSetPrevDate[position].rate/currencyBaseRate}

            val delta =  currencyRate - currencyPrevRate
            val deltaTextView = holder.deltaTextView
            deltaTextView.text = helper.truncateNumberToNSymbols(Formatter(Locale.ENGLISH).format("%+.2f", delta/currencyRate*100).toString() , 5) + "%"

            if(currencyRate >= currencyPrevRate){
                currencyInfoDirection.setImageResource(R.drawable.arrow_up)
                deltaTextView.setTextColor(Color.GREEN)
            }else{
                currencyInfoDirection.setImageResource(R.drawable.arrow_down)
                deltaTextView.setTextColor(Color.RED)
            }


        }
    }

    fun setData(date: String, prevDate : String, dataSet : Map<String, CurrencyInfoOfDay>, baseCurrencyRate: Float, invertRates: Boolean){
        currDate = date
        dateToCompare = prevDate
        currencyInfoMap = dataSet
        currencyBaseRate = baseCurrencyRate
        this.invertRates = invertRates
        notifyDataSetChanged()
    }



    inner class CurrencyAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private val TAG = "CurrencyAdapterVH"
        val currencyRateTextView = view.findViewById<TextView>(R.id.currency_info_rate)
        val currencyNameTextView = view.findViewById<TextView>(R.id.currency_info_name)
        val currencyImageView = view.findViewById<ImageView>(R.id.currency_info_image)
        val currencyInfoDirectionView = view.findViewById<ImageView>(R.id.currency_info_direction)
        val deltaTextView = view.findViewById<TextView>(R.id.currency_info_delta_percent)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            Log.d(TAG, adapterPosition.toString())
            val currList = currencyInfoMap[currDate]?.list
            if(currList != null){
                val currencyRate = if(invertRates){1/(currList[adapterPosition].rate/currencyBaseRate)}
                else{currList[adapterPosition].rate/currencyBaseRate}
                onCurrencyChooseListener.onCurrencyChooseListener(CurrencyInfo("", currList[adapterPosition].currency, currencyRate))
            }

        }
    }

}