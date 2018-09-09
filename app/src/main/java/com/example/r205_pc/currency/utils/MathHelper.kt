package com.example.r205_pc.currency.utils

import android.util.Log
import java.text.Format
import java.util.*

class MathHelper{
    val TAG = "MathHelper"
    /**
     * Truncates number to n symbols
     * @param numStr string that contains the float-number
     * @param nSymbols amount of symbols that has to be in output strings
     * @return number truncated to n symbols
     */
    fun truncateNumberToNSymbols(numStr:String, nSymbols:Int):String{
        val num = numStr.toFloat()
        //Log.d(TAG, "Truncating number $num to $nSymbols symbols")
        if(numStr.length <= nSymbols || num == Math.round(num).toFloat()){//Don't need to truncate
            return numStr
        } else if(numStr.indexOf(".") + 1 == nSymbols){//next 2 if's is to fight with exceptions when we try to cast "." to string
            return numStr.substring(0, numStr.indexOf("."))
        }else if (numStr.contains(Regex("[+-]")) && numStr.indexOf(".") == nSymbols){
            return numStr.substring(0, numStr.indexOf("."))
        }else{
            val perenos = if(numStr.slice(IntRange(nSymbols,nSymbols)).toInt() >= 5) {1} else {0}
            val lastDigit = numStr.slice(IntRange(nSymbols-1, nSymbols-1)).toInt()
            val truncatedStr = numStr.dropLast(numStr.length - nSymbols + 1)
            return (truncatedStr + (lastDigit+perenos))
        }
    }
}