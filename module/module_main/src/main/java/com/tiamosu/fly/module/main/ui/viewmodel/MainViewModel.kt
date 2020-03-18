package com.tiamosu.fly.module.main.ui.viewmodel

import android.util.Log
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.main.data.api.APIs

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
class MainViewModel : BaseViewModel() {

    fun requestFriendJson() {
        FlyHttp[APIs.FRIEND_JSON]
            .build()
            .execute(stringCallback {
                onStart {
                    Log.e("xia", "onStart")
                }
                onFinish {
                    Log.e("xia", "onFinally")
                }

                onError {
                    Log.e("xia", "onError:${it.message}")
                }

                onSuccess {
                    Log.e("xia", "onResponse:$it")
                }
            })
    }
}