package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.callback.JsonCallback
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.common.router.Router
import com.tiamosu.fly.module.common.utils.lazyViewModel
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.data.api.CustomApiService
import com.tiamosu.fly.module.main.data.bean.Friend
import com.tiamosu.fly.module.main.ui.viewmodel.MainViewModel
import com.tiamosu.fly.utils.newInstance
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {

    private val mainViewModel: MainViewModel by lazyViewModel()

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {
        btn_enter_other.setOnClickListener {
            start(newInstance(Router.obtainFragmentOtherCls()))
        }
        btn_request.setOnClickListener {
            mainViewModel.requestFriendJson()

//            FlyHttp["/friend/json"]
//                .addInterceptor(HeadersInterceptor(HttpHeaders().apply {
//                    put(HttpHeaders.HEAD_KEY_ACCEPT_ENCODING, "utf-8")
//                }))
//                .cacheMode(CacheMode.FIRSTCACHE)
//                .cacheKey(this.javaClass.simpleName)
//                .cacheTime(300)
//                .cacheDiskConverter(SerializableDiskConverter())
//                .build()
//                .execute(object : StringCallback() {
//                    override fun onSuccess(t: String?) {
////                        Log.e("xia", "t=========:$t")
//                    }
//
//                    override fun onSuccess(cacheResult: CacheResult<String>) {
//                        Log.e("xia", "cacheResult======:$cacheResult")
//                    }
//                })
        }
        btn_request_custom.setOnClickListener {
            val custom = FlyHttp.custom("/friend/json").also {
                it.build()
            }
            val observable = custom.create(CustomApiService::class.java)
                ?.getFriend(custom.url)
            custom.apiCall(observable, object : JsonCallback<Friend>() {
                override fun onSuccess(t: Friend?) {
                    Log.e("xia", "result=========:$t")
                }
            })
        }
        btn_remove_cache.setOnClickListener {
            FlyHttp.clearCache()
        }
    }

    override fun doBusiness() {}

    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            context.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            ToastUtils.showShort("再按一次退出")
        }
        return true
    }

    companion object {
        // 再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
        private var TOUCH_TIME: Long = 0
    }
}