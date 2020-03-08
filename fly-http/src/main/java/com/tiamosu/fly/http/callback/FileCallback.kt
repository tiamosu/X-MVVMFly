package com.tiamosu.fly.http.callback

import com.tiamosu.fly.http.convert.FileConvert
import okhttp3.ResponseBody
import java.io.File

/**
 * 描述：返回文件类型数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileCallback : AbsCallback<File> {
    private var convert: FileConvert

    constructor() : this(null)

    constructor(destFileName: String?) : this(null, destFileName)

    constructor(destFileDir: String?, destFileName: String?) {
        convert = FileConvert(destFileDir, destFileName)
        convert.setCallback(apply { })
    }

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): File? {
        return convert.convertResponse(body)
    }
}