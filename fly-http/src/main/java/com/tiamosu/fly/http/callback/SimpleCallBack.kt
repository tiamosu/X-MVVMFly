package com.tiamosu.fly.http.callback

/**
 * 描述：简单的回调,默认可以使用该回调，不用关注其他回调方法
 * 使用该回调默认只需要处理onError、onSuccess两个方法既成功失败
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class SimpleCallBack<T> : CallBack<T>() {

    override fun onStart() {}

    override fun onCompleted() {}
}