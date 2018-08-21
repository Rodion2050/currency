package com.example.r205_pc.currency

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.r205_pc.currency.utils.Currency

class CurrencyChooseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var dataSet = listOf<Currency>()
    private var checkedCurrencies = mutableListOf<Boolean>()
    class ListItem(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_choose_list_item, parent, false)
        return ListItem(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val checkBox = view.findViewById<CheckBox>(R.id.currencyChooseCheckBox)
        val currCode = view.findViewById<TextView>(R.id.currencyChooseCode)
        val currDescr = view.findViewById<TextView>(R.id.currencyChooseName)
        val image = view.findViewById<ImageView>(R.id.currencyChooseImage)

        currCode.text = dataSet[position].code
        currDescr.text = dataSet[position].description


        checkBox.isChecked = checkedCurrencies[position]

        checkBox.setOnClickListener({view -> if(view is CheckBox)
            checkedCurrencies[position] = view.isChecked})
    }

    /**@param data - список валют, содержащий код валюты и описание
     * @param checked - Строка кодов выбранных валют, разделенных запятыми*/
    fun setData(data:List<Currency>, checked:String){
        dataSet = data
        checkedCurrencies = MutableList(dataSet.size, {false})
        val checkedCurrArray = checked.split(",")
        for(i in 0 until  dataSet.size){
            for(curr in checkedCurrArray){
                if(curr == dataSet[i].code){
                    checkedCurrencies[i] = true
                    break
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedCurrencies():String{
        var res = ""
        for(i in 0 until checkedCurrencies.size){
            if(checkedCurrencies[i]){
                res += dataSet[i].code
                if(i != checkedCurrencies.size - 1){
                    res += ","
                }
            }
        }
        return res
    }
}