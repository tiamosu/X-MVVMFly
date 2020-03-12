package com.tiamosu.fly.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.demo.api.CustomApiService
import com.tiamosu.fly.demo.base.BaseActivity
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import kotlinx.android.synthetic.main.activity_enter.*

/**
 * @author tiamosu
 * @date 2020/2/21.
 */
class EnterActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_enter
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {}

    override fun initEvent() {
        btn_enter_page_main.setOnClickListener {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
        btn_request.setOnClickListener {
            FlyHttp["/friend/json"]
                .addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                    put(HttpHeaders.HEAD_KEY_ACCEPT_ENCODING, "utf-8")
                }))
                .cacheMode(CacheMode.FIRSTCACHE)
                .cacheKey(this.javaClass.simpleName)
                .cacheTime(300)
                .cacheDiskConverter(SerializableDiskConverter())
                .build()
                .execute(object : StringCallback() {
                    override fun onSuccess(t: String?) {
//                        Log.e("xia", "t=========:$t")
                    }

                    override fun onSuccess(cacheResult: CacheResult<String>) {
                        Log.e("xia", "cacheResult======:$cacheResult")
                    }
                })
        }
        btn_remove_cache.setOnClickListener {
            FlyHttp.clearCache()
        }

        btn_request_custom.setOnClickListener {
            val custom = FlyHttp.custom("/friend/json").also {
                it.build()
            }
            val observable = custom.create(CustomApiService::class.java)
                ?.getFriend(custom.url)
            custom.apiCall(observable, object : StringCallback() {
                override fun onSuccess(t: String?) {
                    Log.e("xia", "t=========:$t")
                }
            })
        }
    }

    override fun doBusiness() {
    }
}