package com.tiamosu.fly.http.https

import android.os.Build
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * @author tiamosu
 * @date 2018/9/27.
 */
class TLSSocketFactory(context: SSLContext) : SSLSocketFactory() {

    private val protocolArray by lazy {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
                arrayOf("SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2")
            }
            else -> {
                arrayOf("SSLv3", "TLSv1")
            }
        }
    }

    private val delegate: SSLSocketFactory = context.socketFactory

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress,
        localPort: Int
    ): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress,
        localPort: Int
    ): Socket? {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket is SSLSocket) {
            val protocols = socket.enabledProtocols
            val supports = ArrayList<String>()
            if (protocols?.isNotEmpty() == true) {
                supports.addAll(listOf(*protocols))
            }
            Collections.addAll(supports, *protocolArray)
            socket.enabledProtocols = supports.toTypedArray()
        }
        return socket
    }
}
