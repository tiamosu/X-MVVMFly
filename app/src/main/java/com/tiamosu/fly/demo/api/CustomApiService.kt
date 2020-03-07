package com.tiamosu.fly.demo.api

import com.tiamosu.fly.http.api.ApiService
import io.reactivex.Observable
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
interface CustomApiService : ApiService {

    @GET
    fun custom(@Url url: String, @QueryMap map: Map<String, String>): Observable<Response>
}