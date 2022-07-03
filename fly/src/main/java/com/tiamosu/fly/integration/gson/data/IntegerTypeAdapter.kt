package com.tiamosu.fly.integration.gson.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.math.BigDecimal

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class IntegerTypeAdapter : TypeAdapter<Int>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): Int {
        if (`in` == null) {
            return 0
        }
        return when (`in`.peek()) {
            JsonToken.NUMBER -> {
                return try {
                    `in`.nextInt()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    `in`.nextDouble().toInt()
                }
            }
            JsonToken.STRING -> {
                val result = `in`.nextString()
                return if (result == null || "" == result) {
                    0
                } else try {
                    result.toInt()
                } catch (e: NumberFormatException) {
                    BigDecimal(result).toFloat().toInt()
                }
            }
            JsonToken.NULL -> {
                `in`.nextNull()
                0
            }
            else -> {
                `in`.skipValue()
                0
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Int?) {
        out.value(value ?: 0)
    }
}