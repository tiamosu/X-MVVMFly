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
internal class BigDecimalTypeAdapter : TypeAdapter<BigDecimal>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): BigDecimal {
        if (`in` == null) {
            return BigDecimal(0)
        }
        return when (`in`.peek()) {
            JsonToken.NUMBER, JsonToken.STRING -> {
                val result = `in`.nextString()
                if (result == null || "" == result) BigDecimal(0) else BigDecimal(result)
            }
            JsonToken.NULL -> {
                `in`.nextNull()
                BigDecimal(0)
            }
            else -> {
                `in`.skipValue()
                BigDecimal(0)
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: BigDecimal?) {
        out.value(value ?: BigDecimal(0))
    }
}