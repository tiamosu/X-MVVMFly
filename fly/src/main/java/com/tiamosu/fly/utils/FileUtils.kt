package com.tiamosu.fly.utils

import android.util.Log
import com.blankj.utilcode.util.SDCardUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * @author xia
 * @date 2018/7/28.
 */
object FileUtils {

    @JvmStatic
    fun createFile(fileDirName: String, fileName: String): File {
        return File(createDir(fileDirName), fileName)
    }

    @JvmStatic
    fun createDir(fileDirName: String): File? {
        val externalFileDirPath = Utils.getApp().getExternalFilesDir(null)?.absolutePath ?: ""
        Log.e("xia", externalFileDirPath)

        val dir = "$externalFileDirPath/$fileDirName/"
        val fileDir = File(dir)
        return createOrExistsDir(fileDir)
    }

    /**
     * @return 创建未存在的文件夹
     */
    @JvmStatic
    fun createOrExistsDir(file: File?): File? {
        val isExist = file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        return if (isExist) file else null
    }

    /**
     * 返回缓存文件夹
     */
    @JvmStatic
    fun getCacheFile(): File {
        var cacheFile: File? = null
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            //获取系统管理的sd卡缓存文件
            val file = Utils.getApp().externalCacheDir
            //如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
            cacheFile = file ?: createDir(Utils.getApp().packageName)
        }
        return cacheFile ?: Utils.getApp().cacheDir
    }
}
