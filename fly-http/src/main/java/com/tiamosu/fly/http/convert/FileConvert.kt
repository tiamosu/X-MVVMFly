package com.tiamosu.fly.http.convert

import android.text.TextUtils
import com.blankj.utilcode.util.CloseUtils
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.utils.FileUtils
import com.tiamosu.fly.utils.Platform
import io.reactivex.functions.Action
import okhttp3.ResponseBody
import java.io.*

/**
 * 描述：文件转换器
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
class FileConvert : Converter<File> {
    private var destFileDir: String                     //目标文件存储的文件夹路径
    private var destFileName: String                    //目标文件存储的文件名
    private var callback: Callback<File>? = null        //下载回调

    constructor() : this(null)

    constructor(destFileName: String?) : this(null, destFileName)

    constructor(destFileDir: String?, destFileName: String?) {
        this.destFileDir = if (!TextUtils.isEmpty(destFileDir)) destFileDir!! else "download"
        this.destFileName =
            if (!TextUtils.isEmpty(destFileName)) destFileName!! else "unknownfile_" + System.currentTimeMillis()
    }

    fun setCallback(callback: Callback<File>?) {
        this.callback = callback
    }

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): File? {
        val file = FileUtils.createFile(destFileDir, destFileName)
        val inputStream = body.byteStream()
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        var bos: BufferedOutputStream? = null

        try {
            val progress = Progress()
            progress.totalSize = body.contentLength()
            progress.fileName = destFileName
            progress.filePath = file.absolutePath

            bis = BufferedInputStream(inputStream)
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)

            val bytes = ByteArray(1024 * 8)
            var read: Int
            do {
                read = bis.read(bytes)
                if (read == -1) break

                bos.write(bytes, 0, read)
                onProgress(progress, read)
            } while (true)

            bos.flush()
            fos.flush()
        } catch (e: IOException) {
            return null
        } finally {
            CloseUtils.closeIO(body, bos, fos, bis, inputStream)
        }
        return file
    }

    private fun onProgress(progress: Progress, read: Int) {
        callback ?: return
        Progress.changeProgress(progress, read.toLong(), object : Progress.Action {
            override fun call(progress: Progress) {
                Platform.postOnMain(Action {
                    callback?.downloadProgress(progress) //进度回调的方法
                })
            }
        })
    }
}
