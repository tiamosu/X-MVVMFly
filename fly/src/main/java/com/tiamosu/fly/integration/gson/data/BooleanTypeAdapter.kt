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
internal class BooleanTypeAdapter : TypeAdapter<Boolean>() {

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): Boolean {
        if (`in` == null) {
            return false
        }
        return when (`in`.peek()) {
            JsonToken.BOOLEAN -> `in`.nextBoolean()
            JsonToken.STRING ->                 // 如果后台返回 "true" 或者 "TRUE"，则处理为 true，否则为 false
                java.lang.Boolean.parseBoolean(`in`.nextString())
            JsonToken.NUMBER ->                 // 如果后台返回的是非 0 的数值则处理为 true，否则为 false
                `in`.nextInt() != 0
            JsonToken.NULL -> {
                `in`.nextNull()
                false
            }
            else -> {
                `in`.skipValue()
                false
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Boolean?) {
        out.value(value ?: false)
    }
}