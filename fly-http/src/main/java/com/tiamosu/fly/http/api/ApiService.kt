package com.tiamosu.fly.http.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    operator fun get(
        @Url url: String,
        @QueryMap(encoded = true) params: Map<String, String>
    ): Observable<ResponseBody>


    //==========================//
    //         POST请求          //
    // =========================//
    @POST
    @FormUrlEncoded
    fun post(
        @Url url: String,
        @FieldMap(encoded = true) maps: Map<String, String>
    ): Observable<ResponseBody>

    @POST
    fun postBody(@Url url: String, @Body any: Any): Observable<ResponseBody>

    @POST
    fun postBody(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postJson(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>


    //==========================//
    //         PUT请求           //
    // =========================//
    @PUT
    fun put(
        @Url url: String,
        @QueryMap(encoded = true) maps: Map<String, String>
    ): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String, @Body any: Any): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @PUT
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun putJson(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>


    //==========================//
    //         DELETE请求        //
    // =========================//
    @DELETE
    fun delete(
        @Url url: String,
        @QueryMap(encoded = true) maps: Map<String, String>
    ): Observable<ResponseBody>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String, @Body any: Any): Observable<ResponseBody>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    fun deleteJson(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>


    //==========================//
    //       文件上传下载         //
    // =========================//
    @POST
    fun uploadFiles(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @Streaming
    @GET
    fun downloadFile(@Url url: String): Observable<ResponseBody>
}
