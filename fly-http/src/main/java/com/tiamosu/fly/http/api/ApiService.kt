package com.tiamosu.fly.http.api

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.*

/**
 * 描述：通用的的api接口
 *
 * 1.加入基础API，减少Api冗余
 * 2.支持多种方式访问网络（get、post、put、delete），包含了常用的情况
 * 3.传统的Retrofit用法，服务器每增加一个接口，就要对应一个api，非常繁琐
 *
 * 注意事项：
 * 1.使用@url，而不是@Path注解，后者放到方法体上，会强制先urlencode，然后与baseurl拼接，请求无法成功
 * 2.注意事项： map不能为null,否则该请求不会执行,但可以size为空
 *
 * @author tiamosu
 * @date 2018/7/27.
 */
interface ApiService {

    //==========================//
    //         GET请求           //
    // =========================//
    @GET
    operator fun get(@Url url: String, @QueryMap params: Map<String, String>): Observable<Response>


    //==========================//
    //         POST请求          //
    // =========================//
    @POST
    @FormUrlEncoded
    fun post(@Url url: String, @FieldMap maps: Map<String, String>): Observable<Response>

    @POST
    fun postBody(@Url url: String, @Body any: Any): Observable<Response>

    @POST
    fun postBody(@Url url: String, @Body body: RequestBody): Observable<Response>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postJson(@Url url: String, @Body jsonBody: RequestBody): Observable<Response>


    //==========================//
    //         PUT请求           //
    // =========================//
    @PUT
    fun put(@Url url: String, @QueryMap maps: Map<String, String>): Observable<Response>

    @PUT
    fun putBody(@Url url: String, @Body any: Any): Observable<Response>

    @PUT
    fun putBody(@Url url: String, @Body body: RequestBody): Observable<Response>

    @PUT
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun putJson(@Url url: String, @Body jsonBody: RequestBody): Observable<Response>


    //==========================//
    //         DELETE请求        //
    // =========================//
    @DELETE
    fun delete(@Url url: String, @QueryMap maps: Map<String, String>): Observable<Response>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String, @Body any: Any): Observable<Response>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String, @Body body: RequestBody): Observable<Response>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    fun deleteJson(@Url url: String, @Body jsonBody: RequestBody): Observable<Response>


    //==========================//
    //       文件上传下载         //
    // =========================//
    @POST
    fun uploadFiles(@Url url: String, @Body body: RequestBody): Observable<Response>

    @Streaming
    @GET
    fun downloadFile(@Url url: String): Observable<Response>
}
