package com.tiamosu.fly.http.utils

import com.blankj.utilcode.util.CloseUtils
import java.io.*

/**
 * @author tiamosu
 * @date 2020/3/5.
 */
object FlyIOUtils {

    @JvmStatic
    @Throws(IOException::class)
    fun toByteArray(input: InputStream?): ByteArray? {
        input ?: return null
        val output = ByteArrayOutputStream()
        write(input, output)
        output.close()
        return output.toByteArray()
    }

    @JvmStatic
    fun toByteArray(input: Any?): ByteArray? {
        input ?: return null
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(input)
            oos.flush()
            return baos.toByteArray()
        } catch (e: IOException) {
            FlyHttpLog.printStackTrace(e)
        } finally {
            CloseUtils.closeIO(oos, baos)
        }
        return null
    }

    @JvmStatic
    fun toObject(input: ByteArray?): Any? {
        input ?: return null
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bais = ByteArrayInputStream(input)
            ois = ObjectInputStream(bais)
            return ois.readObject()
        } catch (e: Exception) {
            FlyHttpLog.printStackTrace(e)
        } finally {
            CloseUtils.closeIO(ois, bais)
        }
        return null
    }

    @Throws(IOException::class)
    private fun write(inputStream: InputStream, outputStream: OutputStream) {
        var len: Int
        val buffer = ByteArray(4096)
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
    }
}