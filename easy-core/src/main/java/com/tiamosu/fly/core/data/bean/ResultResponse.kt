package com.tiamosu.fly.core.data.bean

import android.os.Parcelable
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.utils.getAppComponent
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
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
    private var any: Any? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> getResponse(cls: Class<T>): T? {
        data ?: return null
        if (any == null) {
            any = getAppComponent().gson().fromJson(data, cls)
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