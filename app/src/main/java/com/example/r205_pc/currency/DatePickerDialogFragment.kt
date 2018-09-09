package com.example.r205_pc.currency

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{
    val DATE_ARGUMENT = "Date"

    interface OnDateChosenListener{
        fun onDateChosenListener(date:Date)
    }

    private lateinit var onDateChooseListener:OnDateChosenListener

    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if(arguments != null){
            val dateStr = arguments.getString(DATE_ARGUMENT)
            val date = formatter.parse(dateStr)
            calendar.time = date
        }

        val dialog = DatePickerDialog(activity, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        return dialog
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnDateChosenListener){
            onDateChooseListener = context
        }
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        onDateChooseListener.onDateChosenListener(calendar.time)
    }
}