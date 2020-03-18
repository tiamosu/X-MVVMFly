@file:JvmName("FileUtils")

package com.tiamosu.fly.utils

import com.blankj.utilcode.util.SDCardUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun createFile(fileDirName: String?, fileName: String): File {
    return File(createDir(fileDirName), fileName)
}

fun createDir(fileDirName: String?): File? {
    val fileDirPath = Utils.getApp().getExternalFilesDir(null)?.absolutePath
        ?: Utils.getApp().cacheDir.absolutePath
    val folder = fileDirName ?: Utils.getApp().packageName
    val dir = "$fileDirPath/$folder/"
    val fileDir = File(dir)
    return createOrExistsDir(fileDir)
}

/**
 * @return 创建未存在的文件夹
 */
fun createOrExistsDir(file: File?): File? {
    val isExist = file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    return if (isExist) file else null
}

/**
 * 返回缓存文件夹
 */
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