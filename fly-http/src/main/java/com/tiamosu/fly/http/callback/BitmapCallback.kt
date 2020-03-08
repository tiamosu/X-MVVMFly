package com.tiamosu.fly.http.callback

import android.graphics.Bitmap
import com.tiamosu.fly.http.convert.BitmapConvert
import okhttp3.ResponseBody

/**
 * 描述：返回图片的Bitmap
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class BitmapCallback : AbsCallback<Bitmap> {
    private var convert: BitmapConvert

    constructor() {
        convert = BitmapConvert()
    }

    constructor(maxWidth: Int, maxHeight: Int) {
        convert = BitmapConvert(maxWidth, maxHeight)
    }

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): Bitmap? {
        return convert.convertResponse(body)
    }
}