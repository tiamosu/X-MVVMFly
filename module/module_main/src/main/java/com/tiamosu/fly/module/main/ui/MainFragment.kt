package com.tiamosu.fly.module.main.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.data.api.CustomApiService
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {
        btn_enter_page_main.setOnClickListener {

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

    override fun doBusiness() {}
}