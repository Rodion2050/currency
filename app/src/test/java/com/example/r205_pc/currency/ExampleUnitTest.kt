package com.example.r205_pc.currency

import com.example.r205_pc.currency.utils.MathHelper
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun truncateFloat_isCorrect(){
        val m = MathHelper()
        assertEquals("1234.6", m.truncateNumberToNSymbols("1234.56", 6))
        assertEquals("1234.56", m.truncateNumberToNSymbols("1234.56", 7))
        assertEquals("123456", m.truncateNumberToNSymbols("123456", 5))
        assertEquals("0.0", m.truncateNumberToNSymbols("0.0", 5))
        assertEquals("+123", m.truncateNumberToNSymbols("+123.25", 4))
        assertEquals("+123", m.truncateNumberToNSymbols("+123.25", 5))
    }
}
