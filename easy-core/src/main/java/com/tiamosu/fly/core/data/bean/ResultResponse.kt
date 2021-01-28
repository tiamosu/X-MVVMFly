package com.tiamosu.fly.core.data.bean

import android.os.Parcelable
import com.google.gson.reflect.TypeToken
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.utils.getAppComponent
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author tiamosu
 * @date 2020/7/18.
 */
@Parcelize
data class ResultResponse(
    val code: Int = -100,
    val msg: String = "",
    val data: String? = null,
    val exception: Throwable? = null,
    val response: @RawValue Response<String>? = null
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getResponse(isBodyData: Boolean = false): T? {
        val parseData = (if (!isBodyData) data else response?.body) ?: return null
        return try {
            val type = object : TypeToken<T>() {}.type
            when (type) {
                String::class.java -> parseData
                JSONObject::class.java -> JSONObject(parseData)
                JSONArray::class.java -> JSONArray(parseData)
                else -> getAppComponent().gson().fromJson(parseData, type)
            } as? T
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isSuccess(): Boolean {
        return code == 200
    }
}