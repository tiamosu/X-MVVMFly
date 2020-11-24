package com.tiamosu.fly.demo.ui.fragments

import android.text.Html
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.CacheViewModel
import com.tiamosu.fly.demo.databinding.FragmentCacheBinding
import com.tiamosu.fly.ext.addObserve
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.subscriber.BaseSubscriber
import io.reactivex.rxjava3.core.Observable

/**
 * @author tiamosu
 * @date 2020/3/19.
 */
class CacheFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentCacheBinding }
    private val viewModel: CacheViewModel by lazyViewModel()
    private var cacheMode = CacheMode.FIRSTREMOTE
    private val cacheKey = this.javaClass.name

    override fun getLayoutId() = R.layout.fragment_cache

    @Suppress("DEPRECATION")
    override fun initEvent() {
        val views = arrayOf(
            dataBinding.btnDefaultCache,
            dataBinding.btnFirstRemote,
            dataBinding.btnFirstCache,
            dataBinding.btnOnlyRemote,
            dataBinding.btnOnlyCache,
            dataBinding.btnCacheRemote,
            dataBinding.btnCacheRemoteDistinct
        )
        clickNoRepeat(views = views) {
            when (it) {
                dataBinding.btnDefaultCache -> {
                    //默认缓存，走的是okHttp cacheH
                    cacheMode = CacheMode.DEFAULT
                }
                dataBinding.btnFirstRemote -> {
                    //先请求网络，请求网络失败后再加载缓存 （自定义缓存RxCache）
                    cacheMode = CacheMode.FIRSTREMOTE
                }
                dataBinding.btnFirstCache -> {
                    //先加载缓存，缓存没有再去请求网络 （自定义缓存RxCache）
                    cacheMode = CacheMode.FIRSTCACHE
                }
                dataBinding.btnOnlyRemote -> {
                    //仅加载网络，但数据依然会被缓存 （自定义缓存RxCache）
                    cacheMode = CacheMode.ONLYREMOTE
                }
                dataBinding.btnOnlyCache -> {
                    //只读取缓存 （自定义缓存RxCache）
                    cacheMode = CacheMode.ONLYCACHE
                }
                dataBinding.btnCacheRemote -> {
                    //先使用缓存，不管是否存在，仍然请求网络，会回调两次（自定义缓存RxCache）
                    cacheMode = CacheMode.CACHEANDREMOTE
                }
                dataBinding.btnCacheRemoteDistinct -> {
                    // 先使用缓存，不管是否存在，仍然请求网络，
                    // 有缓存先显示缓存，等网络请求数据回来后发现和缓存是一样的就不会再返回，否则数据不一样会继续返回。
                    //（目的是为了防止数据是一致的也会刷新两次界面）（自定义缓存RxCache）
                    cacheMode = CacheMode.CACHEANDREMOTEDISTINCT
                }
            }
            viewModel.request(cacheMode, cacheKey)
        }

        dataBinding.btnRemoveCache.clickNoRepeat {
            FlyHttp.removeCache(cacheKey)
        }
        dataBinding.btnClearCache.clickNoRepeat {
            FlyHttp.clearCache()
        }
        dataBinding.btnCacheForKey.clickNoRepeat {
            //所有缓存的都是字符串
            val observable: Observable<String> = FlyHttp.getRxCacheBuilder()
                //获取缓存需要指定下转换器，默认就是SerializableDiskConverter 这里可以不用写
                //就是你网络请求用哪个转换器存储的缓存，那么读取时也要采用对应的转换器读取
                .diskConverter(SerializableDiskConverter())
                .build()
                //这个表示读取缓存根据时间，读取指定时间内的缓存，例如读取：5*60s之内的缓存
//                .load(cacheKey, 5 * 60)
                //这个表示读取缓存不根据时间只要有缓存就读取
                .load(cacheKey)

            observable.subscribe(object : BaseSubscriber<String>() {
                override fun onStart() {
                }

                override fun onNext(t: String) {
                    showToast("获取缓存成功：$t")
                    val spanned = Html.fromHtml("我来自缓存\n$t")
                    dataBinding.tvCacheContent.text = spanned
                }

                override fun onError(t: Throwable) {
                    showToast("获取缓存失败：" + t.message)
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    override fun createObserver() {
        addObserve(viewModel.responseLiveData) {
            val from = if (it?.isFromCache == true) "我来自缓存" else "我来自远程网络"
            val spanned = Html.fromHtml(from + "\n" + it?.body.toString())
            dataBinding.tvCacheContent.text = spanned
        }
    }

    override fun doBusiness() {}
}