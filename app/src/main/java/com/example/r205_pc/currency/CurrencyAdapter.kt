package com.example.r205_pc.currency

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.r205_pc.currency.utils.CurrencyInfo
import com.example.r205_pc.currency.utils.CurrencyInfoOfDay

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by r205-pc on 14.07.2018.
 */
class CurrencyAdapter(private var currDate: String, private var dateToCompare: String,private var currencyInfoMap : Map<String, CurrencyInfoOfDay>) : RecyclerView.Adapter<CurrencyAdapter.CurrencyAdapterViewHolder>() {
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
        holder.view.findViewById<TextView>(R.id.currency_info_rate).text = String.format("%.5f", currencyInfoDataSet[position].rate)
        holder.view.findViewById<TextView>(R.id.currency_info_name).text = currencyInfoDataSet[position].currency

        val currencyInfoImage = holder.view.findViewById<ImageView>(R.id.currency_info_image)
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
        val currencyInfoDirection = holder.view.findViewById<ImageView>(R.id.currency_info_direction)
        if(currencyInfoDataSetPrevDate == null){
            currencyInfoDirection.setImageResource(R.drawable.arrow_up)
        }else{
            val delta = currencyInfoDataSet[position].rate - currencyInfoDataSetPrevDate[position].rate
            val deltaTextView = holder.view.findViewById<TextView>(R.id.currency_info_delta_percent)
            deltaTextView.text = String.format("%+.3f%%", delta/currencyInfoDataSet[position].rate*100)

            if(currencyInfoDataSet[position].rate >= currencyInfoDataSetPrevDate[position].rate){
                currencyInfoDirection.setImageResource(R.drawable.arrow_up)
                deltaTextView.setTextColor(Color.GREEN)
            }else{
                currencyInfoDirection.setImageResource(R.drawable.arrow_down)
                deltaTextView.setTextColor(Color.RED)
            }


        }


    }

    fun setData(date: String, prevDate : String, dataSet : Map<String, CurrencyInfoOfDay>){
        currDate = date
        dateToCompare = prevDate
        currencyInfoMap = dataSet
        notifyDataSetChanged()
    }

    class CurrencyAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}