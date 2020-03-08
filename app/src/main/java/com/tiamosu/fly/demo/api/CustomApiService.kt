package com.tiamosu.fly.demo.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
interface CustomApiService {

    @GET
    fun custom(@Url url: String, @QueryMap map: Map<String, String>): Observable<ResponseBody>
}