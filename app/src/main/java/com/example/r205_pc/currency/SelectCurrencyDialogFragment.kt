package com.example.r205_pc.currency

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.app.Activity
import android.content.Context


class SelectCurrencyDialogFragment : DialogFragment(), DialogInterface.OnClickListener{
    interface OnCurrencySelectedListener{
        fun currencySelected(code:String)
    }

    val CURRENCY_LIST_ARGUMENT_KEY = "CurrencyList"
    private var currenciesList = listOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        currenciesList = arguments.getString(CURRENCY_LIST_ARGUMENT_KEY).split(",")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select currency")
        builder.setItems(currenciesList.toTypedArray(), this)
        return builder.create()
    }


    private lateinit var mListener:OnCurrencySelectedListener

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        if(context is OnCurrencySelectedListener){
            mListener = context
        }

    }

    override fun onClick(p0: DialogInterface?, which: Int) {
        mListener.currencySelected(currenciesList[which])
    }
}