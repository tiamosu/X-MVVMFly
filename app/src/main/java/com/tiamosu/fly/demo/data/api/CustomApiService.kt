package com.tiamosu.fly.demo.data.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
interface CustomApiService {

    @GET
    fun getFriend(@Url url: String): Observable<ResponseBody>
}