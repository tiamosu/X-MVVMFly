package com.tiamosu.fly.integration.gson.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * @author tiamosu
 * @date 2021/2/1.
 */
internal class DoubleTypeAdapter : TypeAdapter<Double>() {
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Double {
        return when (`in`.peek()) {
            JsonToken.NUMBER -> `in`.nextDouble()
            JsonToken.STRING -> {
                val result = `in`.nextString()
                if (result == null || "" == result) {
                    0.0
                } else result.toDouble()
            }
            JsonToken.NULL -> {
                `in`.nextNull()
                0.0
            }
            else -> {
                `in`.skipValue()
                0.0
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Double?) {
        out.value(value ?: 0.0)
    }
}