package com.tiamosu.fly.http.callback

import com.blankj.utilcode.util.CloseUtils
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 描述：返回文本内容数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileContentCallback : ResultCallback<String>() {

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): String? {
        val inputStream = body.byteStream()
        val reader = InputStreamReader(inputStream, "utf-8")
        val bufferedReader = BufferedReader(reader)
        var line: String?
        val builder = StringBuilder()
        do {
            line = bufferedReader.readLine() ?: break
            builder.append(line)
        } while (true)

        val result = builder.toString()
        CloseUtils.closeIO(body, inputStream, reader, bufferedReader)
        return result
    }
}