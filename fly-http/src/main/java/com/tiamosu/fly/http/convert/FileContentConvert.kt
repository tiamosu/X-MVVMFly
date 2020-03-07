package com.tiamosu.fly.http.convert

import com.blankj.utilcode.util.CloseUtils
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 描述：获取文本内容
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
class FileContentConvert : Converter<String> {

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): String? {
        val body = response.body() ?: return null
        val inputStream = body.byteStream()
        val reader = InputStreamReader(inputStream, "utf-8")
        val bufferedReader = BufferedReader(reader)
        var line: String?
        val builder = StringBuilder()
        do {
            line = bufferedReader.readLine() ?: break
            builder.append(line)
        } while (true)

        CloseUtils.closeIO(inputStream, reader, bufferedReader)
        return builder.toString()
    }
}
