package com.tiamosu.fly.http.request.base

import com.tiamosu.fly.http.model.HttpParams.FileWrapper
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/5.
 */
interface HasBody<R> {

    fun upRequestBody(requestBody: RequestBody?): R

    fun params(key: String?, file: File?): R

    fun params(key: String?, file: File?, fileName: String?): R

    fun params(key: String?, file: File?, fileName: String?, contentType: MediaType?): R

    fun addFileParams(key: String?, files: List<File>?): R

    fun addFileWrapperParams(key: String?, fileWrappers: List<FileWrapper>?): R

    fun upString(content: String?): R

    fun upString(content: String?, mediaType: MediaType?): R

    fun upJson(json: String?): R

    fun upJson(jsonObject: JSONObject?): R

    fun upJson(jsonArray: JSONArray?): R

    fun upBytes(bs: ByteArray?): R

    fun upBytes(bs: ByteArray?, mediaType: MediaType?): R

    fun upObject(any: Any?): R

    fun addParamsToUrl(isAddParamsToUrl: Boolean): R
}