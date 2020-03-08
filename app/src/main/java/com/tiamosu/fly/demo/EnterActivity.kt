package com.tiamosu.fly.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.demo.api.CustomApiService
import com.tiamosu.fly.demo.base.BaseActivity
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.utils.FlyHttpLog
import kotlinx.android.synthetic.main.activity_enter.*
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @author tiamosu
 * @date 2020/2/21.
 */
class EnterActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_enter
    }

    override fun initData(bundle: Bundle?) {
//        val custom = FlyHttp.custom<Any>("")
//        val observable = custom.create(CustomApiService::class.java)
//            ?.custom("/friend/json", mapOf())
//        custom.apiCall(observable)
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}

    override fun initEvent() {
        btn_enter_page_main.setOnClickListener {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
        btn_request.setOnClickListener {
            FlyHttp.get<String>("/friend/json")
                .build()
                .request(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        Log.e("xia", "body:" + response.body)
//                        FlyHttpLog.i("response:$response")
                    }
                })
        }
        btn_request1.setOnClickListener {
            FlyHttp.get<String>("/friend/json")
                .addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                    put(HttpHeaders.HEAD_KEY_ACCEPT_ENCODING, "utf-8")
                }))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build()
                .request(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        FlyHttpLog.i("response:$response")
                    }
                })
        }
    }

    override fun doBusiness() {
    }
}