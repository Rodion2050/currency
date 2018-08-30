package com.example.r205_pc.currency.utils

import android.util.Log

class MathHelper{
    val TAG = "MathHelper"
    fun truncateNumberToNSymbols(numStr:String, nSymbols:Int):String{

        val num = numStr.toFloat()
//        Log.d(TAG, "Truncating number $num")
        if(numStr.length <= nSymbols || num == Math.round(num).toFloat()){
            return numStr
        } else if(numStr.indexOf(".") + 1 == nSymbols){
            return Math.round(num).toString()
        }else{
            val perenos = if(numStr.slice(IntRange(nSymbols,nSymbols)).toInt() >= 5) {1} else {0}
            val lastDigit = numStr.slice(IntRange(nSymbols-1, nSymbols-1)).toInt()
            val truncatedStr = numStr.dropLast(numStr.length - nSymbols + 1)
            return (truncatedStr + (lastDigit+perenos))
        }
    }
}