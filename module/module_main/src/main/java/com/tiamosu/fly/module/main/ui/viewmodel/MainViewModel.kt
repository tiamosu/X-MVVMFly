package com.tiamosu.fly.module.main.ui.viewmodel

import android.util.Log
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.common.utils.jsonCallback
import com.tiamosu.fly.module.common.utils.stringCallback
import com.tiamosu.fly.module.main.data.api.APIs
import com.tiamosu.fly.module.main.data.bean.Friend

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
class MainViewModel : BaseViewModel() {

    fun requestFriendString() {
        FlyHttp[APIs.FRIEND_JSON]
            .build()
            .execute(stringCallback(onSuccess = {
                Log.e("xia", "it:$it")
            }))
    }

    fun requestFriendGson() {
        FlyHttp[APIs.FRIEND_JSON]
            .build()
            .execute(jsonCallback<Friend>(onSuccess = {
                Log.e("xia", "it:$it")
            }))
    }
}