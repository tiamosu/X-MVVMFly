package com.tiamosu.fly.http.func

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tiamosu.fly.http.model.ApiResult
import com.tiamosu.fly.http.utils.FlyHttpUtils.getClass
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 描述：定义了ApiResult结果转换Func
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
class ApiResultFunc<T>(val type: Type) : Function<ResponseBody, ApiResult<T>> {

    protected val gson: Gson by lazy {
        GsonBuilder()
            .excludeFieldsWithModifiers(
                Modifier.FINAL,
                Modifier.TRANSIENT,
                Modifier.STATIC
            )
            .serializeNulls()
            .create()
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    override fun apply(responseBody: ResponseBody): ApiResult<T> {
        var apiResult = ApiResult<T>()
        apiResult.code = -1
        if (type is ParameterizedType) { //自定义ApiResult
            val cls: Class<T> = type.rawType as Class<T>
            if (ApiResult::class.java.isAssignableFrom(cls)) {
                val params = type.actualTypeArguments
                val clazz = getClass(params[0], 0)
                val rawType = getClass(type, 0)
                try {
                    val json = responseBody.string()
                    //增加是List<String>判断错误的问题
                    if (!MutableList::class.java.isAssignableFrom(rawType) && clazz == String::class.java) {
                        apiResult.data = json as T
                        apiResult.code = 0
                    } else {
                        val result = gson.fromJson<ApiResult<T>>(json, type)
                        if (result != null) {
                            apiResult = result
                        } else {
                            apiResult.msg = "json is null"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    apiResult.msg = e.message
                } finally {
                    responseBody.close()
                }
            } else {
                apiResult.msg = "ApiResult.class.isAssignableFrom(cls) err!!"
            }
        } else { //默认Apiresult
            try {
                val json = responseBody.string()
                val clazz: Class<T> = getClass(type, 0) as Class<T>
                if (clazz == String::class.java) {
                    val result = parseApiResult(json, apiResult)
                    if (result != null) {
                        apiResult = result
                        apiResult.data = json as T
                    } else {
                        apiResult.msg = "json is null"
                    }
                } else {
                    val result = parseApiResult(json, apiResult)
                    if (result != null) {
                        apiResult = result
                        if (apiResult.data != null) {
                            val data = gson.fromJson(apiResult.data.toString(), clazz)
                            apiResult.data = data
                        } else {
                            apiResult.msg = "ApiResult's data is null"
                        }
                    } else {
                        apiResult.msg = "json is null"
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                apiResult.msg = e.message
            } catch (e: IOException) {
                e.printStackTrace()
                apiResult.msg = e.message
            } finally {
                responseBody.close()
            }
        }
        return apiResult
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(JSONException::class)
    private fun parseApiResult(json: String, apiResult: ApiResult<T>): ApiResult<T>? {
        if (TextUtils.isEmpty(json)) return null
        val jsonObject = JSONObject(json)
        if (jsonObject.has("code")) {
            apiResult.code = jsonObject.getInt("code")
        }
        if (jsonObject.has("data")) {
            apiResult.data = jsonObject.getString("data") as T
        }
        if (jsonObject.has("msg")) {
            apiResult.msg = jsonObject.getString("msg")
        }
        return apiResult
    }
}