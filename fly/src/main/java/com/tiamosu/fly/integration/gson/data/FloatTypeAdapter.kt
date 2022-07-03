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
internal class FloatTypeAdapter : TypeAdapter<Float>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): Float {
        if (`in` == null) {
            return 0f
        }
        return when (`in`.peek()) {
            JsonToken.NUMBER -> `in`.nextDouble().toFloat()
            JsonToken.STRING -> {
                val result = `in`.nextString()
                if (result == null || "" == result) {
                    0f
                } else result.toFloat()
            }
            JsonToken.NULL -> {
                `in`.nextNull()
                0f
            }
            else -> {
                `in`.skipValue()
                0f
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Float?) {
        out.value(value ?: 0f)
    }
}