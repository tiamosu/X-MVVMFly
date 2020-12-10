package com.tiamosu.fly.core.data.bean

import android.os.Parcelable
import com.google.gson.reflect.TypeToken
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.utils.getAppComponent
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.json.JSONArray
import org.json.JSONException
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
    @IgnoredOnParcel
    var any: Any? = null

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getResponse(): T? {
        data ?: return null
        if (any == null) {
            val type = object : TypeToken<T>() {}.type
            any = getAppComponent().gson().fromJson(data, type)
        }
        return any as? T
    }

    fun getDataJSONObj(): JSONObject? {
        try {
            data ?: return null
            return JSONObject(data)
        } catch (e: JSONException) {
            return null
        }
    }

    fun getDataJSONArr(): JSONArray? {
        try {
            data ?: return null
            return JSONArray(data)
        } catch (e: JSONException) {
            return null
        }
    }

    fun isSuccess(): Boolean {
        return code == 0
    }
}