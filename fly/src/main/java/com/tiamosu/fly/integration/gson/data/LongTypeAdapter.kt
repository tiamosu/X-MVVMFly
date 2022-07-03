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
internal class LongTypeAdapter : TypeAdapter<Long>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): Long {
        if (`in` == null) {
            return 0
        }
        return when (`in`.peek()) {
            JsonToken.NUMBER -> {
                return try {
                    `in`.nextLong()
                } catch (e: NumberFormatException) {
                    // 如果带小数点则会抛出这个异常
                    BigDecimal(`in`.nextString()).toLong()
                }
            }
            JsonToken.STRING -> {
                val result = `in`.nextString()
                return if (result == null || "" == result) {
                    0
                } else try {
                    result.toLong()
                } catch (e: NumberFormatException) {
                    BigDecimal(result).toLong()
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
    override fun write(out: JsonWriter, value: Long?) {
        out.value(value ?: 0)
    }
}