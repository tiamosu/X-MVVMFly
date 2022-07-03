package com.tiamosu.fly.integration.gson.data

import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.json.JSONArray

/**
 * @author tiamosu
 * @date 2022/7/3
 *
 * 描述：JSONArray 类型解析适配器
 */
class JSONArrayTypeAdapter : TypeAdapter<JSONArray>() {
    private val proxy: TypeAdapter<JsonElement> by lazy { TypeAdapters.JSON_ELEMENT }

    override fun read(`in`: JsonReader?): JSONArray {
        if (`in` == null) {
            return JSONArray()
        }
        val read = proxy.read(`in`)
        if (read.isJsonArray) {
            kotlin.runCatching {
                return JSONArray(read.toString())
            }
        }
        return JSONArray()
    }

    override fun write(out: JsonWriter?, value: JSONArray?) {
        if (value == null) {
            out?.nullValue()
            return
        }
        proxy.write(out, proxy.fromJson(value.toString()))
    }
}