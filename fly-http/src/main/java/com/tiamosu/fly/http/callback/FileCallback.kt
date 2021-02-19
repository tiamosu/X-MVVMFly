package com.tiamosu.fly.http.callback

import com.blankj.utilcode.util.CloseUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ThreadUtils
import com.tiamosu.fly.http.constant.DownloadStatus
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.utils.CacheUtils
import com.tiamosu.fly.utils.createFile
import com.tiamosu.fly.utils.launchMain
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * 描述：返回文件类型数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileCallback : NoCacheResultCallback<File> {
    private var destFileDir: String                     //目标文件存储的文件夹路径
    private var destFileName: String                    //目标文件存储的文件名
    internal var downloadFile: File                     //下载文件
        private set
    private var downloadTask: DownloadTask? = null

    constructor() : this(null)

    constructor(destFileName: String?) : this(null, destFileName)

    constructor(destFileDir: String?, destFileName: String?) {
        this.destFileDir = if (destFileDir.isNullOrBlank()) "download" else destFileDir
        this.destFileName =
            if (destFileName.isNullOrBlank()) "unknownFile_" + System.currentTimeMillis() else destFileName
        this.downloadFile = createFile(this.destFileDir, this.destFileName)
    }

    /**
     * 更新下载状态，是否进行断点下载
     */
    internal fun update(isBreakpointDownload: Boolean) {
        val isDownloading =
            CacheUtils.getDownloadStatus(downloadFile.absolutePath) == DownloadStatus.STATUS_LOADING
        val isBreakpointDownloading = isBreakpointDownload && isDownloading  //是否正在断点下载中
        if (!isBreakpointDownloading) {
            CacheUtils.setDownloadStatus(downloadFile.absolutePath, DownloadStatus.STATUS_DEFAULT)
            FileUtils.delete(downloadFile)
            this.downloadFile = createFile(this.destFileDir, this.destFileName)
        }
    }

    @Throws(Throwable::class)
    final override fun convertResponse(body: ResponseBody): File? {
        if (downloadTask == null) {
            DownloadTask(body)
                .also { downloadTask = it }
                .let(ThreadUtils::executeByIo)
        }
        return null
    }

    private inner class DownloadTask(private val responseBody: ResponseBody) :
        ThreadUtils.SimpleTask<File?>() {
        override fun doInBackground(): File? {
            return saveFile(responseBody)
        }

        override fun onSuccess(result: File?) {
            if (result == null) {
                return
            }
            launchMain {
                CacheUtils.setDownloadStatus(
                    downloadFile.absolutePath,
                    DownloadStatus.STATUS_COMPLETE
                )
                onSuccess(Response.success(false, result))
                onFinish()
            }
        }
    }

    private fun saveFile(body: ResponseBody): File? {
        CacheUtils.setDownloadStatus(downloadFile.absolutePath, DownloadStatus.STATUS_LOADING)

        val progress = Progress().apply {
            val range = downloadFile.length()
            totalSize = body.contentLength() + range
            currentSize = range
            fileName = destFileName
            filePath = downloadFile.absolutePath
        }

        var inputStream: InputStream? = null
        var raf: RandomAccessFile? = null
        try {
            raf = RandomAccessFile(downloadFile, "rw")
            raf.seek(downloadFile.length())
            inputStream = body.byteStream()
            val bytes = ByteArray(1024 * 8)

            var read: Int
            while (inputStream.read(bytes).also { read = it } != -1) {
                raf.write(bytes, 0, read)
                onProgress(progress, read)
            }
        } catch (e: Exception) {
            launchMain {
                onError(Response.error(false, e))
                onFinish()
            }
            return null
        } finally {
            CloseUtils.closeIO(inputStream, raf)
        }
        return downloadFile
    }

    private fun onProgress(progress: Progress, read: Int) {
        Progress.changeProgress(progress, read.toLong()) {
            launchMain {
                downloadProgress(it) //进度回调的方法
            }
        }
    }
}