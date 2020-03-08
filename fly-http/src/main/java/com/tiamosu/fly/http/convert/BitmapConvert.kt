package com.tiamosu.fly.http.convert

import android.graphics.Bitmap
import com.blankj.utilcode.util.CloseUtils
import com.blankj.utilcode.util.ImageUtils
import okhttp3.Response

/**
 * 描述：Bitmap转换器
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
class BitmapConvert : Converter<Bitmap> {
    private var maxWidth = 0
    private var maxHeight = 0

    constructor() : this(1000, 1000)

    constructor(maxWidth: Int, maxHeight: Int) {
        this.maxWidth = maxWidth
        this.maxHeight = maxHeight
    }

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): Bitmap? {
        val body = response.body() ?: return null
        val inputStream = body.byteStream()
        val bitmap = ImageUtils.getBitmap(inputStream).let {
            ImageUtils.compressBySampleSize(it, maxWidth, maxHeight)
        }
        CloseUtils.closeIO(response, body, inputStream)
        return bitmap
    }
}