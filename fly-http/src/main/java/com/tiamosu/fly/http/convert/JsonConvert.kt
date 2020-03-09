package com.tiamosu.fly.http.convert

import com.tiamosu.fly.http.callback.IGenericsSerializator
import com.tiamosu.fly.utils.FlyUtils
import org.json.JSONArray
import org.json.JSONObject

/**
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
                val gson = FlyUtils.getAppComponent().gson()
                gson.fromJson(response, classOfT)
            }
        }
    }
}
