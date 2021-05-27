package com.tiamosu.fly.demo.data.bean

import android.os.Parcelable
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.integration.gson.GsonFactory
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

    inline val Boolean.json: String?
        get() = if (this) response?.body else data

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getResponse(isBodyData: Boolean = false): T? {
        val json = isBodyData.json ?: return null
        return GsonFactory.fromJson<T>(json)
    }

    fun getJSONObj(isBodyData: Boolean = false): JSONObject? {
        try {
            val json = isBodyData.json ?: return null
            return JSONObject(json)
        } catch (e: JSONException) {
            return null
        }
    }

    fun getJSONArr(isBodyData: Boolean = false): JSONArray? {
        try {
            val json = isBodyData.json ?: return null
            return JSONArray(json)
        } catch (e: JSONException) {
            return null
        }
    }

    fun isSuccess(): Boolean {
        return code == 0 || code == 200
    }
}