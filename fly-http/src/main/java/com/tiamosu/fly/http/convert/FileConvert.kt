package com.tiamosu.fly.http.convert

import android.text.TextUtils
import com.blankj.utilcode.util.CloseUtils
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.utils.FlyHttpUtils
import com.tiamosu.fly.utils.FileUtils
import com.tiamosu.fly.utils.Platform
import io.reactivex.functions.Action
import okhttp3.Response
import java.io.*

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class FileConvert : Converter<File> {
    private var folder: String? = null              //目标文件存储的文件夹路径
    private var fileName: String? = null            //目标文件存储的文件名
    private var callback: Callback<File>? = null    //下载回调

    constructor() : this(null)

    constructor(fileName: String?) : this(null, fileName)

    constructor(folder: String?, fileName: String?) {
        this.folder = folder
        this.fileName = fileName
    }

    fun setCallback(callback: Callback<File>?) {
        this.callback = callback
    }

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): File? {
        val body = response.body() ?: return null
        val url: String = response.request().url().toString()
        if (TextUtils.isEmpty(folder)) folder = "download"
        if (TextUtils.isEmpty(fileName)) fileName = FlyHttpUtils.getNetFileName(response, url)

        val file = FileUtils.createFile(folder, fileName!!)
        val inputStream = body.byteStream()
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        var bos: BufferedOutputStream? = null

        try {
            val progress = Progress()
            progress.totalSize = body.contentLength()
            progress.fileName = fileName
            progress.filePath = file.absolutePath
            progress.status = Progress.LOADING
            progress.url = url
            progress.tag = url

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
            CloseUtils.closeIO(bos, fos, bis, inputStream)
        }
        return null
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
