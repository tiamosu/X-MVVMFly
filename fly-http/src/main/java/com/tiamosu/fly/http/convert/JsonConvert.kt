package com.tiamosu.fly.http.convert

import com.google.gson.JsonSyntaxException
import com.tiamosu.fly.http.callback.IGenericsSerializator
import com.tiamosu.fly.utils.getAppComponent
import org.json.JSONArray
import org.json.JSONObject

/**
 * 描述：对字符串转换为对应的Json数据
 * @author tiamosu
 * @date 2020/3/8.
 */
class JsonConvert : IGenericsSerializator {

    @Suppress("UNCHECKED_CAST")
    override fun <T> transform(response: String, classOfT: Class<T>): T? {
        return when (classOfT) {
            JSONObject::class.java -> {
                JSONObject(response) as? T
            }
            JSONArray::class.java -> {
                JSONArray(response) as? T
            }
            else -> {
                try {
                    getAppComponent().gson().fromJson(response, classOfT)
                } catch (e: JsonSyntaxException) {
                    null
                }
            }
        }
    }
}
