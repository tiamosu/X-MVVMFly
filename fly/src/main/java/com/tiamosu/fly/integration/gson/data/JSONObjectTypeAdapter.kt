package com.tiamosu.fly.integration.gson.data

import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.json.JSONObject


/**
 * @author tiamosu
 * @date 2022/7/3
 *
 * 描述：JSONObject 类型解析适配器
 */
class JSONObjectTypeAdapter : TypeAdapter<JSONObject>() {
    private val proxy: TypeAdapter<JsonElement> by lazy { TypeAdapters.JSON_ELEMENT }

    override fun read(`in`: JsonReader?): JSONObject {
        if (`in` == null) {
            return JSONObject()
        }
        val read = proxy.read(`in`)
        if (read.isJsonObject) {
            kotlin.runCatching {
                return JSONObject(read.toString())
            }
        }
        return JSONObject()
    }

    override fun write(out: JsonWriter?, value: JSONObject?) {
        if (value == null) {
            out?.nullValue()
            return;
        }
        proxy.write(out, proxy.fromJson(value.toString()))
    }
}