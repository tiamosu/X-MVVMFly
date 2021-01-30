package com.tiamosu.fly.core.data.bean

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

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getResponse(isBodyData: Boolean = false): T? {
        val parseData = (if (!isBodyData) data else response?.body) ?: return null
        return GsonFactory.fromJson<T>(parseData)
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
        return code == 200
    }
}