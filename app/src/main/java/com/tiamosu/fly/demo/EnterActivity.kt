package com.tiamosu.fly.demo

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.demo.base.BaseActivity
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.request.GetRequest
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

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}

    override fun initEvent() {
        btn_enter_page_main.setOnClickListener {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
        btn_request.setOnClickListener {
            GetRequest("/friend/json")
                .request()
        }
        btn_request1.setOnClickListener {
            GetRequest("/friend/json")
                .addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                    put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
                    put(HttpHeaders.HEAD_KEY_ACCEPT_ENCODING, "utf-8")
                }))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .request()
        }
    }

    override fun doBusiness() {
    }
}