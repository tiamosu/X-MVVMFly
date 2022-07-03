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
internal class StringTypeAdapter : TypeAdapter<String>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): String {
        if (`in` == null) {
            return ""
        }
        return when (`in`.peek()) {
            JsonToken.STRING, JsonToken.NUMBER -> `in`.nextString() ?: ""
            JsonToken.BOOLEAN ->                 // 对于布尔类型比较特殊，需要做针对性处理
                java.lang.Boolean.toString(`in`.nextBoolean()) ?: ""
            JsonToken.NULL -> {
                `in`.nextNull()
                ""
            }
            else -> {
                `in`.skipValue()
                ""
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: String?) {
        out.value(value ?: "")
    }
}