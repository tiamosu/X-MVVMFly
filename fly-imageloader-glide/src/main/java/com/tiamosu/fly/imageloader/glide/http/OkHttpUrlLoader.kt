package com.tiamosu.fly.imageloader.glide.http

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * A simple model loader for fetching media over http/https using OkHttp.
 *
 * @author tiamosu
 * @date 2018/8/27.
 */
class OkHttpUrlLoader(private val client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(
        model: GlideUrl, width: Int, height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model, OkHttpStreamFetcher(client, model))
    }

    /**
     * The default factory for [OkHttpUrlLoader]s.
     */
    class Factory
    /**
     * Constructor for a new Factory that runs requests using given client.
     * Constructor for a new Factory that runs requests using a static singleton client.
     * @param client this is typically an instance of `OkHttpClient`.
     */
    @JvmOverloads constructor(private val client: Call.Factory = getInternalClient()!!) :
        ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {}

        companion object {
            @Volatile
            private var internalClient: Call.Factory? = null

            private fun getInternalClient(): Call.Factory? {
                if (internalClient == null) {
                    synchronized(Factory::class.java) {
                        if (internalClient == null) {
                            internalClient = OkHttpClient()
                        }
                    }
                }
                return internalClient
            }
        }
    }
}
